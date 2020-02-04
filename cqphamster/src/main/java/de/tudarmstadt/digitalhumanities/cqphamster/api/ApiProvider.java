package de.tudarmstadt.digitalhumanities.cqphamster.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.core.TransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.DoNotSend;

public class ApiProvider<T extends Getable> {
	
	private TransactionManager man = new TransactionManager();
	
	private String cacheName;
	
	private static Map<String,List<String>> doNotSends = new HashMap<>();
	
	public ApiProvider(String cacheName) {
		this.cacheName = cacheName;
	}
	
	private void addToDoNotSends(T object) {
		if (!doNotSends.containsKey(cacheName)) {
			
			doNotSends.put(cacheName, new ArrayList<>());
			
			Class<? extends Getable> cls = object.getClass();
			
			for (Field f : cls.getFields()) {
				DoNotSend v = f.getAnnotation(DoNotSend.class);
				
				if (v != null)
					doNotSends.get(cacheName).add(f.getName());
				
			}
		}
	}
	
	private void filterOutDoNotSends(T object) throws Exception {
		if (!doNotSends.get(cacheName).isEmpty()) {
			Class<? extends Getable> cls = object.getClass();
			
			for (String fieldName : doNotSends.get(cacheName)) {
				Field f = cls.getField(fieldName);
				Method setter = cls.getMethod(fieldName, f.getType());
				
				Object[] helper = new Object[1];
				helper[0] = null;
				
				setter.invoke(object, helper);
			}
		}
	}
	
	private boolean hasPermission() {
		
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public T getSingleObject(int id) throws Exception {
		T obj = (T)man.getObjectById(cacheName, id);
		
		if (obj != null) {
			addToDoNotSends(obj);
			filterOutDoNotSends(obj);
		}
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getAllObjects() throws Exception {
		List<T> ret = (List<T>)man.getAllObjects(cacheName);
		
		if (ret.size() > 0) {
			addToDoNotSends(ret.get(0));
			
			for (T object : ret)
				filterOutDoNotSends(object);
		}
		
		return ret;
	}
	
	public void addOrUpdateObject(T object) throws Exception {
		
	}
}
