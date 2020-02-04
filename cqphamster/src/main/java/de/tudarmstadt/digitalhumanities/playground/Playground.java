package de.tudarmstadt.digitalhumanities.playground;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.ignite.Ignition;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.cpe.CpePipeline;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.berkeleyparser.BerkeleyParser;
import org.dkpro.core.clearnlp.ClearNlpSegmenter;
import org.dkpro.core.commonscodec.ColognePhoneticTranscriptor;
import org.dkpro.core.commonscodec.DoubleMetaphonePhoneticTranscriptor;
import org.dkpro.core.commonscodec.PhoneticTranscriptor_ImplBase;
import org.dkpro.core.corenlp.CoreNlpParser;
import org.dkpro.core.corenlp.CoreNlpPosTagger;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.dkpro.core.io.bnc.BncReader;
import org.dkpro.core.io.imscwb.ImsCwbReader;
import org.dkpro.core.io.negra.NegraExportReader;
import org.dkpro.core.io.text.TextReader;
import org.dkpro.core.io.tiger.TigerXmlReader;
import org.dkpro.core.matetools.MateParser;
import org.dkpro.core.opennlp.OpenNlpChunker;
import org.dkpro.core.opennlp.OpenNlpLemmatizer;
import org.dkpro.core.opennlp.OpenNlpNamedEntityRecognizer;
import org.dkpro.core.opennlp.OpenNlpParser;
import org.dkpro.core.opennlp.OpenNlpPosTagger;
import org.dkpro.core.opennlp.OpenNlpSegmenter;

import de.tudarmstadt.digitalhumanities.cqphamster.core.TransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.imp.VRTTempWriterConsumer;
import de.tudarmstadt.digitalhumanities.cqphamster.model.Corpus;
import de.tudarmstadt.digitalhumanities.cqphamster.model.User;
import se.lth.cs.srl.preprocessor.tokenization.StanfordPTBTokenizer;

public class Playground {


	
	public static void main(String... args) throws ResourceInitializationException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, AnalysisEngineProcessException, CollectionException, IOException {	
		TransactionManager man = new TransactionManager();
		
		User us = new User();
		us.seteMail("sebig125@gmail.com");
		us.setPassword("lelele");
		us.setId(-1);
		
		man.addOrUpdateObject(us);
		
		Corpus corpus = new Corpus();
		
		corpus.setName("Dickens");
		corpus.setImportIdentifier("reading");
		corpus.setImporting(true);
		corpus.setOwnerId(0);
		corpus.setId(-1);
		
		corpus.setCorpusCwbId(corpus.getName().toLowerCase().replace("^('/[^\\W\\d_]+/)", "") + RandomStringUtils.randomAlphabetic(5).toLowerCase());
		
		man.addOrUpdateObject(corpus);

			CollectionReaderDescription read = createReaderDescription(ImsCwbReader.class,
					ImsCwbReader.PARAM_LANGUAGE, "en", 
					ImsCwbReader.PARAM_SOURCE_LOCATION, "D:/dickens.vrt",
					ImsCwbReader.PARAM_MAPPING_ENABLED, true,
					ImsCwbReader.PARAM_SOURCE_ENCODING, "latin1",
					ImsCwbReader.PARAM_READ_SENTENCES, true
					);
			
			AnalysisEngineDescription phon = createEngineDescription(ClearNlpSegmenter.class,
					ClearNlpSegmenter.PARAM_WRITE_TOKEN, false,
					ClearNlpSegmenter.PARAM_WRITE_SENTENCE, true);
			//AnalysisEngineDescription cons = createEngineDescription(OpenNlpParser.class);
			
			AnalysisEngineDescription consu = createEngineDescription(VRTTempWriterConsumer.class, 
					VRTTempWriterConsumer.WRITE_CHUNKS, false,
					VRTTempWriterConsumer.WRITE_CONSTITUENTS, false,
					VRTTempWriterConsumer.WRITE_DEPENDENCY, false,
					VRTTempWriterConsumer.WRITE_LEMMA, true,
					VRTTempWriterConsumer.WRITE_MORPHOSEMNATICS, false,
					VRTTempWriterConsumer.WRITE_NAMED_ENTITY, false,
					VRTTempWriterConsumer.WRITE_PHONETICS, false,
					VRTTempWriterConsumer.WRITE_POS, true,
					VRTTempWriterConsumer.PATH_TO_VRT, "D:/vrttemp/vrt_temp.vrt",
					VRTTempWriterConsumer.CORPUS_ID, corpus.getId());
			
			SimplePipeline.runPipeline(read, phon, consu);
		
	}
}
