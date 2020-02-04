package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.time.ZonedDateTime;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;
import de.tudarmstadt.digitalhumanitites.cqphamster.core.cqp.CQPQueryPositions;

@Requestable
public class Query extends Getable {

	private int id;
	@IndexedBy
	private String query;
	
	private ZonedDateTime creationDate;
	@IndexedBy
	private int corpusId;
	@IndexedBy
	private int userId;
	
	private CQPQueryPositions positions;
	
	public int getId() {
		return id;
	}

	public String getQuery() {
		return query;
	}

	public ZonedDateTime getCreationDate() {
		return creationDate;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setCreationDate(ZonedDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public int getCorpusId() {
		return corpusId;
	}

	public int getUserId() {
		return userId;
	}

	public void setCorpusId(int corpusId) {
		this.corpusId = corpusId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Query && ((Query)o).getId() == this.getId();
	}

	public CQPQueryPositions getPositions() {
		return positions;
	}

	public void setPositions(CQPQueryPositions positions) {
		this.positions = positions;
	}
}
