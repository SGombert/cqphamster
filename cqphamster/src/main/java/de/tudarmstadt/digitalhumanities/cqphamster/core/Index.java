package de.tudarmstadt.digitalhumanities.cqphamster.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Index<T extends Object> {
	
	private HashMap<T, ArrayList<Integer>> indexDict = new HashMap<>();

	public List<Integer> getIdentifiersByKey(T key) {
		ArrayList<Integer> ret = this.indexDict.get(key);
		
		return ret != null ? ret : new ArrayList<Integer>();
	}
	
	public List<Integer> getIdentifiersByMultipleKeys(T... keys) {
		ArrayList<Integer> ret = new ArrayList<>();
		
		for (T key : keys) {
			ArrayList<Integer> result = this.indexDict.get(key);
			
			if (result != null)
				ret.addAll(result);
		}
		
		return ret;
	}
	
	public void add(T key, int indexValue) {
		if (this.indexDict.get(key) == null)
			this.indexDict.put(key, new ArrayList<>());
		
		this.indexDict.get(key).add(indexValue);
	}
	
	public void remove(T key, int indexValue) {
		if (this.indexDict.get(key) == null)
			this.indexDict.put(key, new ArrayList<>());
		
		if (this.indexDict.get(key).contains(indexValue))
			this.indexDict.get(key).remove(indexValue);
	}
	
}
