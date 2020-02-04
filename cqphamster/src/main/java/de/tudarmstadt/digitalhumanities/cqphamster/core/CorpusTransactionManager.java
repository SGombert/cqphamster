package de.tudarmstadt.digitalhumanities.cqphamster.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.lang.IgniteRunnable;

import de.tudarmstadt.digitalhumanities.cqphamster.model.PerSpanAnnotation;
import de.tudarmstadt.digitalhumanities.cqphamster.model.PerSpanAnnotationCategory;
import de.tudarmstadt.digitalhumanities.cqphamster.model.Token;

public class CorpusTransactionManager {
	
	private TransactionManager man;
	
	private String tokenCacheName;
	
	private String perSpanCacheName;
	
	private String perSpanCatergoryName;
	
	private String tagsetCacheName;
	
	public CorpusTransactionManager(int corpusId) {
		this.tokenCacheName = corpusId + "Tokens";
		this.perSpanCacheName = corpusId + "Spannotations";
		this.perSpanCatergoryName = corpusId + "SpannotationsCategories";
		this.tagsetCacheName = corpusId + "TagsetCacheName";
		this.man = new TransactionManager();
	}

	public void addToken(Token tk) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		this.man.addNewWithFixedId(this.tokenCacheName, tk);
	}

	public Token getToken(int id) {
		return (Token)this.man.getObjectById(this.tokenCacheName, id);
	}
	
	public List<Token> getTokens(List<Integer> ids) {
		ArrayList<Token> retList = new ArrayList<>(ids.size());
		
		for (Getable get : this.man.getObjectsByIds(this.tokenCacheName, ids))
			retList.add((Token)get);
		
		return retList;
	}
	
	public PerSpanAnnotation getPerSpanAnnotation(int id) {
		return (PerSpanAnnotation)this.man.getObjectById(this.perSpanCacheName, id);
	}
	
	public List<Getable> getPerSpanAnnotations(List<Integer> ids) {
		return this.man.getObjectsByIds(this.perSpanCacheName, ids);
	}
	
	public List<Getable> getPerSpanAnnotations(String name) throws ClassNotFoundException, NoSuchFieldException, SecurityException {
		return this.man.getObjectsByIndexedField(this.perSpanCatergoryName, "name", name);
	}
	
	public List<Getable> getPerSpanAnnotations(int firstToken, int lastToken) throws ClassNotFoundException, NoSuchFieldException, SecurityException, InterruptedException {
		List<Getable> retList = Collections.synchronizedList(new ArrayList<>());
		
		// TODO make configable
		ExecutorService exec = Ignition.ignite().executorService();
		
		for (int i = 0; i <= lastToken; i++) {
			
			int j = i;
			exec.execute(new IgniteRunnable() {
				private static final long serialVersionUID = 6469927624086639089L;

				@Override
				public void run() {
					try {
						for (Getable g : man.getObjectsByIndexedField(perSpanCacheName, "begin", 
								new Integer(j).toString())) {
							PerSpanAnnotation anno = (PerSpanAnnotation)g;
							
							if (anno.getEnd() > firstToken)
								retList.add(anno);
						}
					} catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
						exec.shutdown();
						throw new IgniteException(e);
					}
				}
				
			});
		}
		
		exec.awaitTermination(200, TimeUnit.MINUTES);
		
		Collections.sort(retList, (a, b) -> {
			return ((PerSpanAnnotation)a).getBegin() - ((PerSpanAnnotation)b).getBegin();
		});
		
		return retList;
		
	}
	
	public void addPerSpanAnnotation(PerSpanAnnotation anno) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		this.man.addOrUpdateObject(this.perSpanCacheName, anno);
	}

	public String getTokenCacheName() {
		return tokenCacheName;
	}

	public String getPerSpanCacheName() {
		return perSpanCacheName;
	}

	public String getPerSpanCatergoryName() {
		return perSpanCatergoryName;
	}

}
