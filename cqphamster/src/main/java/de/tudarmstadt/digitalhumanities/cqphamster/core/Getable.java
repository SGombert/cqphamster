package de.tudarmstadt.digitalhumanities.cqphamster.core;

public abstract class Getable {

	public abstract int getId();
	
	public abstract void setId(int id);
	
	@Override
	public boolean equals(Object o) {
		return this.getClass().getName().equals(o.getClass().getName()) 
				&& ((Getable)o).getId() == this.getId();
	}
}
