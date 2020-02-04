package de.tudarmstadt.digitalhumanities.cqphamster.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.transactions.Transaction;
import org.dkpro.core.api.io.ResourceCollectionReaderBase;
import org.reflections.Reflections;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

public class TransactionManager {
	
	private static String[] cacheCategories;
	
	private static IgniteCache<String,Integer> categoryMaxId;
	
	private static Ignite ignite;
	
	public TransactionManager() {
		if (ignite == null) {
			Ignition.setClientMode(true);
			
			Ignition.start();
			ignite = Ignition.ignite();
			
		}
		
		if (cacheCategories == null) {
			Reflections ref = new Reflections("de.tudarmstadt.digitalhumanities.cqphamster.model");
			
			Set<Class<? extends Getable>> getables = ref.getSubTypesOf(Getable.class);		
			
			CacheConfiguration<String, Integer> cfg = new CacheConfiguration<>();

			cfg.setName("entity_ids");
			cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
			
			// Create cache with given name, if it does not exist.
			categoryMaxId = ignite.getOrCreateCache(cfg);
			
			getables.forEach(getableClass -> {
				String canonicalName = getableClass.getCanonicalName();
				
				if (categoryMaxId.get(canonicalName) == null)
					categoryMaxId.put(canonicalName, -1);
				
			});
		}
		
	}
	
	public IgniteCache<Integer,Getable> getCache(String cache) {
		CacheConfiguration<Integer,Getable> cfg = new CacheConfiguration<>();

		cfg.setName(cache);
		cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

		// Create cache with given name, if it does not exist.
		return ignite.getOrCreateCache(cfg);
	}
	
	public IgniteCache<Integer,? extends Object> getGenericCache(String cache) {
		CacheConfiguration<Integer,? extends Object> cfg = new CacheConfiguration<>();

		cfg.setName(cache);
		cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

		// Create cache with given name, if it does not exist.
		return ignite.getOrCreateCache(cfg);		
	}
	
	public void destroyCache(String cache) {
		this.getCache(cache).destroy();
	}
	
	public void destroyGenericCache(String cache) {
		this.getCache(cache).destroy();
	}

	public Getable getObjectById(String cache, int id) {
		return this.getCache(cache).get(id);
	}
	
	private String getIndexName(String cache, String fieldName) {
		return cache + fieldName;
	}
	
	private IgniteCache<String, Index<Object>> getIndexCache() {
		CacheConfiguration<String,Index<Object>> cfg = new CacheConfiguration<>();

		cfg.setName("index");
		cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

		// Create cache with given name, if it does not exist.
		return ignite.getOrCreateCache(cfg);		
	}
	
	private Integer getMaxIndexForCache(String cache) {
		CacheConfiguration<String,Integer> cfg = new CacheConfiguration<>();

		cfg.setName("maxids");
		cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

		// Create cache with given name, if it does not exist.
		return ignite.getOrCreateCache(cfg).get(cache);			
	}
	
	public int increaseMaxIndexForCache(String cache, int inc) {
		CacheConfiguration<String,Integer> cfg = new CacheConfiguration<>();

		cfg.setName("maxids");
		cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
		
		IgniteCache<String,Integer> ch = ignite.getOrCreateCache(cfg);
		
		Transaction t = ignite.transactions().txStart();
		if (ch.get(cache) != null)
			ch.put(cache, ch.get(cache) +1);
		else
			ch.put(cache, 0);
		t.commit();
		t.close();
		
		return ch.get(cache);
	}
	
	private Index<Object> createIndex(String cache, String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		IgniteCache<String,Index<Object>> indexCache = getIndexCache();
		IgniteCache<Integer,Getable> objectCache = getCache(cache);
		
		int maxIndex = getMaxIndexForCache(cache);
		Index<Object> idx = new Index<>();
		for (int i = 0; i <= maxIndex; i++) {
			Getable object = objectCache.get(i);
			
			if (object == null)
				continue;
			
			Field fld = object.getClass().getField(fieldName);
			
			if (!fld.isAnnotationPresent(IndexedBy.class))
				throw new IllegalArgumentException("Tried to index field not marked as indexable.");
			
			Object o = fld.get(object);
			
			idx.add(o, object.getId());
		}
		Transaction t = ignite.transactions().txStart();		
		indexCache.put(this.getIndexName(cache, fieldName), idx);
		t.commit();
		t.close();
		
		return idx;
	}
	
	private void addToIndices(Getable obj) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		IgniteCache<String,Index<Object>> indexCache = getIndexCache();
		Class<? extends Object> cls = obj.getClass();
		
		for (Field fld : cls.getFields()) {
			if (fld.isAnnotationPresent(IndexedBy.class)) {
				String indexName = this.getIndexName(cls.getCanonicalName(), fld.getName());
				
				Index<Object> idx = indexCache.get(indexName);
				
				if (idx == null)
					this.createIndex(cls.getCanonicalName(), fld.getName());
				else {
					idx.add(fld.get(obj), obj.getId());
					
					Transaction t = ignite.transactions().txStart();	
					indexCache.put(indexName, idx);
					t.commit();
					t.close();
				}
			}
		}	
	}
	
	private void updateIndices(Getable obj) throws IllegalArgumentException, IllegalAccessException {
		
		IgniteCache<String,Index<Object>> indexCache = getIndexCache();
		IgniteCache<Integer,Getable> objectCache = this.getCache(obj.getClass().getCanonicalName());
		Class<? extends Object> cls = obj.getClass();
		Getable oldObject = objectCache.get(obj.getId());

		for (Field fld : cls.getFields()) {
			if (fld.isAnnotationPresent(IndexedBy.class) && !fld.get(obj).equals(fld.get(oldObject))) {
				String indexName = this.getIndexName(cls.getCanonicalName(), fld.getName());
				
				Index<Object> idx = indexCache.get(indexName);
				
				idx.remove(fld.get(oldObject), oldObject.getId());
				idx.add(fld.get(obj), obj.getId());
				
				Transaction t = ignite.transactions().txStart();	
				indexCache.put(indexName, idx);				
				t.commit();
				t.close();
			}
		}	
	}
	
	public void addOrUpdateObject(String cache, Getable obj) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		IgniteCache<Integer,Getable> objectCache = this.getCache(cache);
		
		if (obj.getId() >= 0) {
			this.updateIndices(obj);
			Transaction t = ignite.transactions().txStart();
			objectCache.put(obj.getId(), obj);
			t.commit();
			t.close();
		} else {
			int id = this.increaseMaxIndexForCache(cache, 1);
			
			obj.setId(id);
			Transaction t = ignite.transactions().txStart();
			objectCache.put(id, obj);
			t.commit();
			t.close();
			this.addToIndices(obj);
		}
	}
	
	public void addNewWithFixedId(String cache, Getable obj) {
		IgniteCache<Integer,Getable> objectCache = this.getCache(cache);
		
		objectCache.put(obj.getId(), obj);
	}
	
	public void addOrUpdateObject(Getable obj) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		addOrUpdateObject(obj.getClass().getCanonicalName(),obj);
	}
	
	public List<Getable> getObjectsByIndexedField(String cache, String fieldName, String value) throws ClassNotFoundException, NoSuchFieldException, SecurityException {
		Class<? extends Object> cls = Class.forName(cache);
		
		if (cls != null) {
			Field fld = cls.getField(fieldName);
			
			if (fld != null && fld.isAnnotationPresent(IndexedBy.class)) {
				Index<Object> idx = this.getIndexCache().get(this.getIndexName(cache, fieldName));
				
				return this.getObjectsByIds(cache, idx.getIdentifiersByKey(value));
			}
		}
		
		return null;
	}
	
	// TODO finish this fort rights management
	public List<Getable> getObjectsByMultipleIndexedFields(String cache, Map<String,String> queryParams) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		Class<? extends Object> cls = Class.forName(cache);
		
		if (cls != null) {
			List<List<Integer>> lists = new ArrayList<>();
			int lengthLongest = 0;
			
			for (Entry<String,String> queryParam : queryParams.entrySet()) {
				Field fld = cls.getField(queryParam.getKey());
				
				if (fld != null & fld.isAnnotationPresent(IndexedBy.class)) {
					Index<Object> idx = this.getIndexCache().get(this.getIndexName(cache, queryParam.getKey()));
					List<Integer> ids = idx.getIdentifiersByKey(queryParam.getValue());
					
					lists.add(ids);
				}
			}
			
			Collections.sort(lists, (a, b) -> a.size() - b.size());
			
			
		}
		
		return null;
	}
	
	public List<Getable> getObjectsByIds(String cache, int minId, int maxId) {
		List<Getable> ret = new ArrayList<>(maxId - minId + 1);
		
		IgniteCache<Integer,Getable> ch = this.getCache(cache);
		for (int i = minId; i < maxId; i++) {
			Getable obj = ch.get(i);
			
			if (obj == null)
				break;
			
			ret.add(obj);
		}
		
		return ret;		
	}
	
	public List<Getable> getObjectsByIds(String cache, List<Integer> ids) {
		List<Getable> ret = new ArrayList<>(ids.size());
		
		IgniteCache<Integer,Getable> ch = this.getCache(cache);
		for (Integer id : ids) {
			Getable obj = ch.get(id);
			
			if (obj == null)
				continue;
			
			ret.add(obj);
		}
		
		return ret;		
	}
	
	public List<Getable> getAllObjects(String cache) {
		List<Getable> ret = new ArrayList<>();
		
		IgniteCache<Integer,Getable> ch = this.getCache(cache);
		ch.forEach(c -> ret.add(c.getValue()));
		
		return ret;
	}
	
}
