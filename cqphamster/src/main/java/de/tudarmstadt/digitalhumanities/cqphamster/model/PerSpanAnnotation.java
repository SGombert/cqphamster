package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;

public class PerSpanAnnotation extends Getable {

	private int id;
	
	private String name;
	
	@IndexedBy
	private int begin;
	@IndexedBy
	private int end;
	@IndexedBy
	private int perSpanAnnotationCategory;
	
	private Map<String,String> attributes = new HashMap<>();

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public String getOpeningTag() {
		StringBuilder strBuild = new StringBuilder();
		
		strBuild.append('<');
		strBuild.append(this.name);
		strBuild.append(' ');
		
		for (Entry<String,String> attribute : this.attributes.entrySet()) {
			strBuild.append(attribute.getKey());
			strBuild.append("=\"");
			strBuild.append(attribute.getValue());
			strBuild.append("\" ");
		}
		
		strBuild.deleteCharAt(strBuild.length() -1);
		strBuild.append('>');
		
		return strBuild.toString();
	}
	
	public String getClosingTag() {
		StringBuilder strBuild = new StringBuilder();
		
		strBuild.append("</");
		strBuild.append(this.name);
		strBuild.append('>');
		
		return strBuild.toString();
	}
	
}
