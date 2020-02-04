package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.util.Map;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;
@Requestable
public class WordEmbeddingsSet extends Getable {

	private int id;
	@IndexedBy
	private int corpusId;
	@IndexedBy
	private String name;
	@IndexedBy
	private String method;
	
	private int dimensions;
	
	private String description;

	public int getId() {
		return id;
	}

	public int getCorpusId() {
		return corpusId;
	}

	public String getName() {
		return name;
	}

	public int getDimensions() {
		return dimensions;
	}

	public String getDescription() {
		return description;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCorpusId(int corpusId) {
		this.corpusId = corpusId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
