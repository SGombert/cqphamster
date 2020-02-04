package de.tudarmstadt.digitalhumanities.cqphamster.model;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
public class Annotator extends Getable {

	private int id;
	
	private String name;
	
	private String uimaAnnotatorClassName;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUimaAnnotatorClassName() {
		return uimaAnnotatorClassName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUimaAnnotatorClassName(String uimaAnnotatorClassName) {
		this.uimaAnnotatorClassName = uimaAnnotatorClassName;
	}
}
