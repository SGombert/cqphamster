package de.tudarmstadt.digitalhumanities.cqphamster.model;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
public class PerTokenAnnotationCategory extends Getable {

	private int id;
	@IndexedBy
	private int corpusId;
	
	private String name;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCorpusId() {
		return corpusId;
	}

	public void setCorpusId(int corpusId) {
		this.corpusId = corpusId;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof PerTokenAnnotationCategory && ((PerTokenAnnotationCategory)o).getId() == this.getId();
	}
}
