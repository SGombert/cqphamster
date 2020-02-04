package de.tudarmstadt.digitalhumanities.cqphamster.model;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
public class AnnotationModel extends Getable {
	

	int id;
	@IndexedBy
	int annotatorId;
	@IndexedBy
	int languageId;

	String name;
	
	String url;
	
	public int getId() {
		return id;
	}

	public int getAnnotatorId() {
		return annotatorId;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAnnotatorId(int annotatorId) {
		this.annotatorId = annotatorId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public int getLanguageId() {
		return languageId;
	}

	public void setLanguageId(int languageId) {
		this.languageId = languageId;
	}
}
