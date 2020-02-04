package de.tudarmstadt.digitalhumanitites.cqphamster.core.cqp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import de.tudarmstadt.digitalhumanities.cqphamster.model.PerSpanAnnotation;
import de.tudarmstadt.digitalhumanities.cqphamster.model.Token;
import de.tudarmstadt.digitalhumanities.cqphamster.util.Utils;

public class VRTDumpBuilder {
	
	private static String osSuffix = System.getProperty("os.name").contains("indows") ? ".exe" : "";
	
	private Map<Integer, Token> tokens = Collections.synchronizedMap(new HashMap<>());
	
	private Map<Integer, List<PerSpanAnnotation>> spannotationsByBegin = Collections.synchronizedMap(new HashMap<>());
	
	private Map<Integer, List<PerSpanAnnotation>> spannotationsByEnd = Collections.synchronizedMap(new HashMap<>());
	
	private Set<String> perSpanAnnotationCategories = Collections.synchronizedSet(new HashSet<>());
	
	private Map<String,Set<String>> possibleAttributesForAnno = Collections.synchronizedMap(new HashMap<>());
	
	private int maxPerSpanEmbedding = 0;
	
	private int corpusId;
	
	public VRTDumpBuilder(int corpusId) {
		this.corpusId = corpusId;
	}
	
	public void addToken(Token tk) {
		this.tokens.put(tk.getId(), tk);
	}
	
	private void putInIndexMap(int tokenIndex, PerSpanAnnotation anno, Map<Integer, List<PerSpanAnnotation>> map) {
		if (! map.containsKey(tokenIndex))
			map.put(tokenIndex, Collections.synchronizedList(new ArrayList<>()));
		
		map.get(tokenIndex).add(anno);
	}
	
	public void addPerSpanAnnotation(PerSpanAnnotation spannotation) {
		this.putInIndexMap(spannotation.getBegin(), spannotation, this.spannotationsByBegin);
		this.putInIndexMap(spannotation.getEnd(), spannotation, this.spannotationsByEnd);
		
		this.perSpanAnnotationCategories.add(spannotation.getName());
		
		if (!this.possibleAttributesForAnno.containsKey(spannotation.getName()))
			this.possibleAttributesForAnno.put(spannotation.getName(), Collections.synchronizedSet(new HashSet<>()));
		
		for (Entry<String,String> entry : spannotation.getAttributes().entrySet())
			this.possibleAttributesForAnno.get(spannotation.getName()).add(entry.getKey());
		
	}
	
	private List<String> getSortedPerTokenAnnotationKeys() {
		List<String> perTokenAnnotationKeys = new ArrayList<>(this.tokens.get(0).getAnnotations().size());
		
		for (Entry<String, String> anno : this.tokens.get(0).getAnnotations().entrySet())
			perTokenAnnotationKeys.add(anno.getKey());
		
		Collections.sort(perTokenAnnotationKeys);
		
		return perTokenAnnotationKeys;
	}
	
	// MUST BE CALLED AFTER toString
	public String[] getCQPImportParameters() throws IOException {
		if (this.tokens.isEmpty())
			return null;
		
		List<String> perTokenAnnotationKeys = getSortedPerTokenAnnotationKeys();
		
		List<String> parameters 
			= new ArrayList<>(perTokenAnnotationKeys.size() * 2 
					+ perSpanAnnotationCategories.size() * 2 + 2);
		
		parameters.add(Utils.getConfigurationValue("pathToCwbBins") + "/cwb-encode" + osSuffix);
		
		parameters.add("-d");
		parameters.add(Utils.getConfigurationValue("pathToCorpusData") + "/data/" + this.corpusId);
		parameters.add("-f");
		parameters.add(Utils.getConfigurationValue("pathToTemp") + '/' + this.corpusId + ".vrt");
		parameters.add("-R");
		parameters.add(Utils.getConfigurationValue("pathToRegistry"));
		
		for (String perTokenAnnotationKey : perTokenAnnotationKeys) {
			parameters.add("-P");
			parameters.add(perTokenAnnotationKey);
		}
		
		for (String perSpanAnnotationCategory : this.perSpanAnnotationCategories) {
			parameters.add("-S");
			
			StringBuilder spanCatBuild = new StringBuilder();
			spanCatBuild.append(perSpanAnnotationCategory);
			spanCatBuild.append(':');
			spanCatBuild.append(this.maxPerSpanEmbedding);
			
			for (String possibleAttr : this.possibleAttributesForAnno.get(perSpanAnnotationCategory)) {
				spanCatBuild.append('+');
				spanCatBuild.append(possibleAttr);
			}
			
			parameters.add(spanCatBuild.toString());
		}
		
		String[] ret = new String[parameters.size()];
		
		for (int i = 0; i < parameters.size(); i++)
			ret[i] = parameters.get(i);
		
		return ret;
		
	}
	
	public Map<String,String[]> getXMLRegistryEditOptions() throws IOException {
		Map<String,String[]> returnMap = Collections.synchronizedMap(new HashMap<>());
		
		for (String perSpanAnnotationCategory : this.perSpanAnnotationCategories) {
			List<String> parameterList = new ArrayList<>();
			
			parameterList.add(Utils.getConfigurationValue("pathToCwbBins") + "/cwb-regedit" + osSuffix);
			parameterList.add(new Integer(this.corpusId).toString());
			parameterList.add(":add");
			parameterList.add(":s");
			
			parameterList.add(perSpanAnnotationCategory);
			
			for (int i = 1; i <= this.maxPerSpanEmbedding; i++) {
				parameterList.add(perSpanAnnotationCategory + i);
			}
			
			for (String attributeCategory : this.possibleAttributesForAnno.get(perSpanAnnotationCategory)) {
				StringBuilder strBuild = new StringBuilder();
				
				strBuild.append(perSpanAnnotationCategory);
				strBuild.append('_');
				strBuild.append(attributeCategory);
				
				String res = strBuild.toString();
				
				for (int i = 1; i <= this.maxPerSpanEmbedding; i++)
					parameterList.add(res + i);
				
			}
			
			String[] parameterArray = new String[parameterList.size()];
			
			for (int i = 0; i < parameterList.size(); i++)
				parameterArray[i] = parameterList.get(i);
			
			returnMap.put(perSpanAnnotationCategory, parameterArray);
		}
		
		return returnMap;
	}
	
	public String getVRTString() {
		
		if (this.tokens.isEmpty())
			return "";
		
		List<String> perTokenAnnotationKeys = getSortedPerTokenAnnotationKeys();
		
		StringBuilder vrtBuild = new StringBuilder();
		
		Stack<PerSpanAnnotation> openSpannotations = new Stack<>();
		
		for (int i = 0; i < this.tokens.size(); i++) {
			Token tk = this.tokens.get(i);
			
			for (PerSpanAnnotation anno : this.spannotationsByBegin.get(tk.getId())) {
				openSpannotations.push(anno);
				vrtBuild.append(anno.getOpeningTag());
				vrtBuild.append('\n');
				
				if (openSpannotations.size() > this.maxPerSpanEmbedding)
					this.maxPerSpanEmbedding++;
					
			}
			
			vrtBuild.append(tk.getTokenString());
			
			for (String annotationKey : perTokenAnnotationKeys) {
				vrtBuild.append('\t');
				vrtBuild.append(tk.getAnnotations().get(annotationKey));
			}
			
			vrtBuild.append('\n');
			
			for (PerSpanAnnotation anno : this.spannotationsByEnd.get(tk.getId())) {
				vrtBuild.append(openSpannotations.pop().getClosingTag());
				vrtBuild.append('\n');
			}	
		}
		
		return vrtBuild.toString();
		
	}
	
	public int getCorpusId() {
		return this.corpusId;
	}
}
