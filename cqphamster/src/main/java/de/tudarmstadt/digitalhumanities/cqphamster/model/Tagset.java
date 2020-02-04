package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.util.Set;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;

public class Tagset extends Getable {
	
	private int id;
	
	@IndexedBy
	private String attributeCategory;
	
	private Set<String> tags;

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;	
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

}
