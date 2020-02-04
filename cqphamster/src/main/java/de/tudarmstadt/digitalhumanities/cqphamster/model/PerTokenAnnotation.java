package de.tudarmstadt.digitalhumanities.cqphamster.model;

import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;

public class PerTokenAnnotation {

	private String name;
	
	@IndexedBy
	private int annotationCategory;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAnnotationCategory() {
		return annotationCategory;
	}

	public void setAnnotationCategory(int annotationCategory) {
		this.annotationCategory = annotationCategory;
	}


}
