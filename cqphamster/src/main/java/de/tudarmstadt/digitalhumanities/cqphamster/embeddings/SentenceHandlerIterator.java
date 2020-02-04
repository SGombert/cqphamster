package de.tudarmstadt.digitalhumanities.cqphamster.embeddings;

import java.util.List;

import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;

import de.tudarmstadt.digitalhumanities.cqphamster.core.CorpusTransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.core.TransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.model.PerSpanAnnotation;

public class SentenceHandlerIterator implements SentenceIterator {
	
	private List<Getable> sentences;
	
	private CorpusTransactionManager corTran;
	
	private int i = 0;
	
	private boolean lowercaseTokens;
	
	public SentenceHandlerIterator(int corpusId, boolean lowercaseTokens) throws ClassNotFoundException, NoSuchFieldException, SecurityException {
		this.corTran = new CorpusTransactionManager(corpusId);
		
		this.sentences = corTran.getPerSpanAnnotations("s");
		
		this.lowercaseTokens = lowercaseTokens;
	}

	@Override
	public String nextSentence() {
		StringBuilder strBuild = new StringBuilder();
		
		PerSpanAnnotation sentence = (PerSpanAnnotation) this.sentences.get(this.i);
		
		for (int j = sentence.getBegin(); j <= sentence.getEnd(); j++) {
			strBuild.append(
					this.lowercaseTokens ? 
							corTran.getToken(j).getTokenString().toLowerCase() : 
								corTran.getToken(j).getTokenString());
			strBuild.append(' ');
		}
		
		i++;
		
		return strBuild.toString();
	}

	@Override
	public boolean hasNext() {
		return i < this.sentences.size();
	}

	@Override
	public void reset() {
		this.i = 0;	
	}

	@Override
	public void finish() {
		this.i  = this.sentences.size();
		
	}

	@Override
	public SentencePreProcessor getPreProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPreProcessor(SentencePreProcessor preProcessor) {
		// TODO Auto-generated method stub
		
	}

}
