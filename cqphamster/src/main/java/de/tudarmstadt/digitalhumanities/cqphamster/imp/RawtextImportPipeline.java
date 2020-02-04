package de.tudarmstadt.digitalhumanities.cqphamster.imp;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

import de.tudarmstadt.digitalhumanities.cqphamster.model.Language;

public class RawtextImportPipeline {

	private Language language;
	
	private int corpusId;
	
	private AnalysisEngine segmenter;
	
	private AnalysisEngine paragraphAnnotator;
	
	private AnalysisEngine posTagger;
	
	private AnalysisEngine lemmatizer;
	
	private AnalysisEngine phoneticTranscriptor;
	
	private AnalysisEngine stemmer;
	
	private AnalysisEngine chunker;
	
	private AnalysisEngine dependencyParser;
	
	private AnalysisEngine constituencyParser;
	
	private AnalysisEngine morphologicalAnalyzer;

	public Language getLanguage() {
		return language;
	}

	public AnalysisEngine getSegmenter() {
		return segmenter;
	}
	
	public AnalysisEngine getParagraphAnnotator() {
		return paragraphAnnotator;
	}

	public AnalysisEngine getPosTagger() {
		return posTagger;
	}

	public AnalysisEngine getLemmatizer() {
		return lemmatizer;
	}

	public AnalysisEngine getPhoneticTranscriptor() {
		return phoneticTranscriptor;
	}

	public AnalysisEngine getStemmer() {
		return stemmer;
	}
	
	public AnalysisEngine getChunker() {
		return chunker;
	}

	public AnalysisEngine getDependencyParser() {
		return dependencyParser;
	}

	public AnalysisEngine getConstituencyParser() {
		return constituencyParser;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setSegmenter(AnalysisEngine segmenter) {
		this.segmenter = segmenter;
	}
	
	public void setParagraphAnnotator(AnalysisEngine paragraphAnnotator) {
		this.paragraphAnnotator = paragraphAnnotator;
	}

	public void setPosTagger(AnalysisEngine posTagger) {
		this.posTagger = posTagger;
	}

	public void setLemmatizer(AnalysisEngine lemmatizer) {
		this.lemmatizer = lemmatizer;
	}

	public void setPhoneticTranscriptor(AnalysisEngine phoneticTranscriptor) {
		this.phoneticTranscriptor = phoneticTranscriptor;
	}

	public void setStemmer(AnalysisEngine stemmer) {
		this.stemmer = stemmer;
	}
	
	public void setChunker(AnalysisEngine chunker) {
		this.chunker = chunker;
	}

	public void setDependencyParser(AnalysisEngine dependencyParser) {
		this.dependencyParser = dependencyParser;
	}

	public void setConstituencyParser(AnalysisEngine constituencyParser) {
		this.constituencyParser = constituencyParser;
	}
	
	public AnalysisEngine getMorphologicalAnalyzer() {
		return morphologicalAnalyzer;
	}

	public void setMorphologicalAnalyzer(AnalysisEngine morphologicalAnalyzer) {
		this.morphologicalAnalyzer = morphologicalAnalyzer;
	}

	public void run(RawtextContainer rawtexts) throws AnalysisEngineProcessException, ResourceInitializationException, CollectionException, IOException {
		LinkedList<AnalysisEngine> engines = new LinkedList<>();
		
		if (this.segmenter != null)
			engines.add(this.segmenter);
		
		if (this.paragraphAnnotator != null)
			engines.add(this.paragraphAnnotator);
		
		if (this.posTagger != null)
			engines.add(this.posTagger);
		
		if (this.lemmatizer != null)
			engines.add(this.lemmatizer);
		
		if (this.phoneticTranscriptor != null)
			engines.add(this.phoneticTranscriptor);
		
		if (this.morphologicalAnalyzer != null)
			engines.add(this.phoneticTranscriptor);
		
		if (this.stemmer != null)
			engines.add(this.stemmer);
		
		if (this.chunker != null)
			engines.add(this.chunker);
		
		if (this.dependencyParser != null)
			engines.add(this.dependencyParser);
		
		if (this.constituencyParser != null)
			engines.add(this.constituencyParser);
		
		LinkedList<AnalysisEngine> finalEngines = new LinkedList<>();
		for (AnalysisEngine en : engines) {
			AnalysisEngine progressNotifier = createEngine(ProgressNotifier.class,
					ProgressNotifier.PARAM_CORPUS_ID, this.corpusId,
					ProgressNotifier.PARAM_STEP, en.getAnalysisEngineMetaData().getName());
			
			finalEngines.add(progressNotifier);
			finalEngines.add(en);
		}
		
		CollectionReader r = createReader(RawtextReader.class, 
				RawtextReader.INPUT_JSON, "" );
		
		SimplePipeline.runPipeline(r, (AnalysisEngine[])engines.toArray());
	}



}
