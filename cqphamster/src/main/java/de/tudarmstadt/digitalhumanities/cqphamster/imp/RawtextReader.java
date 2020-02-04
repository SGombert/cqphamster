package de.tudarmstadt.digitalhumanities.cqphamster.imp;


import java.io.IOException;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RawtextReader extends JCasCollectionReader_ImplBase {
	public static final String INPUT_JSON = "Input Json";
	@ConfigurationParameter(name = INPUT_JSON,
			description = "inputjson",
			mandatory = true)
	private String inputJson;
	
	private int i;
	
	private static RawtextContainer rawtextContainer;

	private void init() throws JsonParseException, JsonMappingException, IOException {
		if (rawtextContainer == null) {
			if (rawtextContainer == null) {
				ObjectMapper om = new ObjectMapper();
				rawtextContainer = om.readValue(inputJson, RawtextContainer.class);
			}			
		}
	}
	
	@Override
	public boolean hasNext() throws JsonParseException, JsonMappingException, IOException {
		init();
		
		return i < rawtextContainer.getTextStrings().length;
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(i, rawtextContainer.getTextStrings().length,
				Progress.ENTITIES) };
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {
		
		jCas.setDocumentLanguage(rawtextContainer.getLang());
		
		String textAtHand = rawtextContainer.getTextStrings()[i];
		jCas.setDocumentText(textAtHand);
		
		this.i++;
		
		if (this.i == this.rawtextContainer.getTextStrings().length)
			rawtextContainer = null;
	}


}
