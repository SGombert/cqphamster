package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.util.HashMap;
import java.util.Set;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.NeedsPermission;

@NeedsPermission
public class CorpusTypeFrequencyDistribution extends Getable {
	
	private int id;
	@IndexedBy
	private int queryId;
	@IndexedBy
	private int corpusId;
	
	private HashMap<String,Integer> relativeFrequencies;
	
	private HashMap<String,Integer> absoluteFrequencies;
	
	private Set<String> distinctTypes;

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public int getQueryId() {
		return queryId;
	}

	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}

	public int getCorpusId() {
		return corpusId;
	}

	public void setCorpusId(int corpusId) {
		this.corpusId = corpusId;
	}

	public HashMap<String, Integer> getRelativeFrequencies() {
		return relativeFrequencies;
	}

	public void setRelativeFrequencies(HashMap<String, Integer> relativeFrequencies) {
		this.relativeFrequencies = relativeFrequencies;
	}

	public HashMap<String, Integer> getAbsoluteFrequencies() {
		return absoluteFrequencies;
	}

	public void setAbsoluteFrequencies(HashMap<String, Integer> absoluteFrequencies) {
		this.absoluteFrequencies = absoluteFrequencies;
	}

	public Set<String> getDistinctTypes() {
		return distinctTypes;
	}

	public void setDistinctTypes(Set<String> distinctTypes) {
		this.distinctTypes = distinctTypes;
	}

}
