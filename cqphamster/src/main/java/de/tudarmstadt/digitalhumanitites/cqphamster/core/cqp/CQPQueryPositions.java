package de.tudarmstadt.digitalhumanitites.cqphamster.core.cqp;

import java.util.List;

public class CQPQueryPositions {

	private List<int[]> positions;
	
	public CQPQueryPositions(List<int[]> positions) {
		this.positions = positions;
	}
	
	public int size() {
		return this.positions.size();
	}
	
	public int getBeginOfPosition(int positionId) {
		return this.positions.get(positionId)[0];
	}
	
	public int getEndOfPosition(int positionId) {
		return this.positions.get(positionId)[1];
	}
	
	public int getBeginOfFocus(int positionId) {
		return this.positions.get(positionId)[2];
	}
	
	public int getEndOfFocus(int positionId) {
		return this.positions.get(positionId)[3];
	}
	
}
