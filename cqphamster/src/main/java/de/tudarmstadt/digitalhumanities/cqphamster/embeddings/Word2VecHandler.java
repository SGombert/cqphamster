package de.tudarmstadt.digitalhumanities.cqphamster.embeddings;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

public class Word2VecHandler {
	
	public static final String USE_CBOW = "CBOW";
	
	public static final String USE_SKIPGRAM = "SKIPGRAM";

	private Word2Vec word2vec;
	
	private boolean fit;
	
	public Word2VecHandler(int minWordFrequency, int dimensions, int windowSize, String whatAlgoToUse, int corpusId, boolean lowercaseTokens) throws ClassNotFoundException, NoSuchFieldException, SecurityException {
		
		TokenizerFactory tf = new DefaultTokenizerFactory();
		tf.setTokenPreProcessor(new CommonPreprocessor());
		
		this.word2vec = new Word2Vec.Builder()
				.minWordFrequency(minWordFrequency)
				.layerSize(dimensions)
				.windowSize(windowSize)
				.iterate(new SentenceHandlerIterator(corpusId, lowercaseTokens))
				.tokenizerFactory(tf)
				.build();
		
	}
	
	public void fit() {
		this.word2vec.fit();
		
		this.fit = true;
	}
	
	
}
