package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.util.Map;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
public class Token extends Getable {

	private int id;

	@IndexedBy
	private int corpusId;
	
	private String tokenString;
	
	private Map<String, String> annotations;

	public int getId() {
		return id;
	}

	public int getCorpusId() {
		return corpusId;
	}

	public Map<String, String> getAnnotations() {
		return annotations;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCorpusId(int corpusId) {
		this.corpusId = corpusId;
	}

	public void setAnnotations(Map<String, String> annotations) {
		this.annotations = annotations;
	}

	public String getTokenString() {
		return tokenString;
	}

	public void setTokenString(String tokenString) {
		this.tokenString = tokenString;
	}
}
