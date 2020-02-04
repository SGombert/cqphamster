package de.tudarmstadt.digitalhumanities.cqphamster.model;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;

public class WordEmbedding extends Getable {
	
	private int id;
	
	@IndexedBy
	private int wordEmbeddingSetId;
	
	@IndexedBy
	private String word;
	
	private Double[] dimensionValues;

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;	
	}

	public Double[] getDimensionValues() {
		return dimensionValues;
	}

	public void setDimensionValues(Double[] dimensionValues) {
		this.dimensionValues = dimensionValues;
	}
	
	

}
