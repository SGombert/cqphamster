package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.util.List;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
public class Language extends Getable {
	
	private int id;
	
	private String identifier;
	
	private String languageValue;
	
	private List<Integer> corporaIds;

	public int getId() {
		return id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getLanguageValue() {
		return languageValue;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setLanguageValue(String languageValue) {
		this.languageValue = languageValue;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Language && ((Language)o).getId() == this.getId();
	}

	public List<Integer> getCorporaIds() {
		return corporaIds;
	}

	public void setCorporaIds(List<Integer> corporaIds) {
		this.corporaIds = corporaIds;
	}
	
}
