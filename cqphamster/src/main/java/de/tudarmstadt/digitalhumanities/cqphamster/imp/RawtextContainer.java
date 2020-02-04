package de.tudarmstadt.digitalhumanities.cqphamster.imp;

import java.util.Map;

public class RawtextContainer {
	
	private String[] textStrings;
	
	private String lang;
	
	private Map<Integer,Map<String,String>> textMetadata;

	public String[] getTextStrings() {
		return textStrings;
	}

	public Map<Integer, Map<String, String>> getTextMetadata() {
		return textMetadata;
	}

	public void setTextStrings(String[] textStrings) {
		this.textStrings = textStrings;
	}

	public void setTextMetadata(Map<Integer, Map<String, String>> textMetadata) {
		this.textMetadata = textMetadata;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
}
