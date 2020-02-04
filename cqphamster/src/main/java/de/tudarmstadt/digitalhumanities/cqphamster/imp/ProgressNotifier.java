package de.tudarmstadt.digitalhumanities.cqphamster.imp;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.digitalhumanities.cqphamster.core.TransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.model.Corpus;

public class ProgressNotifier extends JCasAnnotator_ImplBase {
	
	public static final String PARAM_STEP = "Step";
	@ConfigurationParameter(name = PARAM_STEP,
			description = "RawtextContainer",
			mandatory = true)
	private String step;
	
	public static final String PARAM_CORPUS_ID = "Corpus_Id";
	@ConfigurationParameter(name = PARAM_CORPUS_ID,
			description = "Corpus_Id",
			mandatory = true)
	private int corpusId;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		TransactionManager transMan = new TransactionManager();
		
		Corpus corpus = (Corpus) transMan.getObjectById(Corpus.class.getSimpleName(), corpusId);
		
		corpus.setImportIdentifier(step);
		
		try {
			transMan.addOrUpdateObject(corpus);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e.getMessage(), null, e);
		}
	}
	
}
