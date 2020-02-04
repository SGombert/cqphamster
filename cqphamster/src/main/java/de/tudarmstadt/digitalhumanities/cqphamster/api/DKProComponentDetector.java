package de.tudarmstadt.digitalhumanities.cqphamster.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.dkpro.core.api.io.ResourceCollectionReaderBase;
import org.dkpro.core.api.segmentation.SegmenterBase;
import org.dkpro.core.commonscodec.PhoneticTranscriptor_ImplBase;
import org.reflections.Reflections;

import de.tudarmstadt.digitalhumanities.cqphamster.util.Utils;
import eu.openminted.share.annotations.api.Component;
import eu.openminted.share.annotations.api.constants.OperationType;

public class DKProComponentDetector {
	
	private static final String ENABLED = "ENABLED";
	
	public static final String DEPENDENCY_PARSERS = "dependencyParsers";
	public static final String CONSTITUENCY_PARSERS = "constituencyParsers";
	public static final String NAMED_ENTITY_RECOGNIZERS = "namedEntityRecognizers";
	public static final String PHONETIC_TRANSCRIPTORS = "phoneticTranscriptors";
	public static final String CHUNKERS = "chunkers";
	public static final String LEMMATIZERS = "lemmatizers";
	public static final String POS_TAGGERS = "posTaggers";
	public static final String SEGMENTERS = "segmenters";
	
	public static final String READERS = "readers";
	
	private static Map<String,List<Class<? extends JCasAnnotator_ImplBase>>> annotators;
	private static List<Class<? extends ResourceCollectionReaderBase>> readers;

	private static List<String> packages() {
		List<String> pacs = new ArrayList<>();
		
		pacs.add("org.dkpro.core.treetagger");
		pacs.add("org.dkpro.core.opennlp");
		pacs.add("org.dkpro.core.udpipe");
		pacs.add("org.dkpro.core.nlp4j");
		pacs.add("org.dkpro.core.corenlp");
		pacs.add("org.dkpro.core.clearnlp");
		pacs.add("org.dkpro.core.arktools");
		pacs.add("org.dkpro.core.ixa");
		pacs.add("org.dkpro.core.hunpos");
		pacs.add("org.dkpro.core.commonscodec");
		pacs.add("org.dkpro.core.mecab");
		pacs.add("org.dkpro.core.sfst");
		pacs.add("org.dkpro.core.rftagger");
		
		pacs.add("org.dkpro.core.io.aclanthology");
		pacs.add("org.dkpro.core.io.ancora");
		pacs.add("org.dkpro.core.io.bnc");
		pacs.add("org.dkpro.core.io.conll");
		pacs.add("org.dkpro.core.io.html");
		pacs.add("org.dkpro.core.io.lif");
		pacs.add("org.dkpro.core.io.lxf");
		pacs.add("org.dkpro.core.io.negra");
		pacs.add("org.dkpro.core.io.nitf");
		pacs.add("org.dkpro.core.io.nif");
		pacs.add("org.dkpro.core.io.penntree");
		pacs.add("org.dkpro.core.io.reuters");
		pacs.add("org.dkpro.core.io.rtf");
		pacs.add("org.dkpro.core.io.tcf");
		pacs.add("org.dkpro.core.io.tei");
		pacs.add("org.dkpro.core.io.tiger");
		pacs.add("org.dkpro.core.io.tuebadz");
		pacs.add("org.dkpro.core.io.tuepp");
		pacs.add("org.dkpro.core.io.web1t");
		pacs.add("org.dkpro.core.io.xces");
		
		return pacs;
	}
	
	private static String encodeAnnotatorList(List<Class<? extends JCasAnnotator_ImplBase>> lst) {
		StringBuilder strBuild = new StringBuilder();
		
		for (Class<? extends JCasAnnotator_ImplBase> cls : lst) {
			strBuild.append(cls.getSimpleName());
			strBuild.append(',');
			strBuild.append(cls.getName());
			strBuild.append(',');
			strBuild.append(ENABLED);
			strBuild.append('\n');
		}
		
		return strBuild.toString();
	}
	
	private static String encodeReaderList(List<Class<? extends ResourceCollectionReaderBase>> lst) {
		StringBuilder strBuild = new StringBuilder();
		
		for (Class<? extends ResourceCollectionReaderBase> cls : lst) {
			strBuild.append(cls.getSimpleName());
			strBuild.append(',');
			strBuild.append(cls.getName());
			strBuild.append(',');
			strBuild.append(ENABLED);
			strBuild.append('\n');			
		}
		
		return strBuild.toString();
	}
	
	private static void writeToFile(String category, String content) throws IOException {
		String path = Utils.getConfigurationValue(Utils.IMPORT_ANNOTATORS_CONFIG_PATH);
		
		BufferedWriter write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + '/' + category + ".txt"), "utf8"));
		
		write.write(content);
		
		write.close();
	}
	
	@SuppressWarnings("unchecked")
	private static void updateSingleAnnotatorList(String key, String path) throws IOException {
		BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(path + '/' + key + ".txt"), "utf8"));
		
		annotators.put(key, new ArrayList<>());
		
		read.lines().forEach(line -> {
			String[] elems = line.split(",");
			
			if (ENABLED.equals(elems[2])) {
				List<Class<? extends JCasAnnotator_ImplBase>> lst = annotators.get(key);
				try {
					lst.add((Class<? extends JCasAnnotator_ImplBase>)Class.forName(elems[1]));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		
		});
		
		read.close();
	}
	
	@SuppressWarnings("unchecked")
	private static void updateReaderList(String key, String path) throws IOException {
		BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(path + '/' + key + ".txt"), "utf8"));
		
		read.lines().forEach(line -> {
			String[] elems = line.split(",");
			
			if (ENABLED.equals(elems[2])) {
				try {
					readers.add((Class<? extends ResourceCollectionReaderBase>)Class.forName(elems[1]));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		
		});
		
		read.close();
	}
	

	public static void updateAnnotatorLists() throws IOException {
		String path = Utils.getConfigurationValue(Utils.IMPORT_ANNOTATORS_CONFIG_PATH);
		
		annotators = new HashMap<>();
		
		updateSingleAnnotatorList(SEGMENTERS, path);
		updateSingleAnnotatorList(POS_TAGGERS, path);
		updateSingleAnnotatorList(LEMMATIZERS, path);
		updateSingleAnnotatorList(PHONETIC_TRANSCRIPTORS, path);
		updateSingleAnnotatorList(NAMED_ENTITY_RECOGNIZERS, path);
		updateSingleAnnotatorList(CONSTITUENCY_PARSERS, path);
		updateSingleAnnotatorList(DEPENDENCY_PARSERS, path);
		updateSingleAnnotatorList(CHUNKERS, path);
		
		updateReaderList(READERS, path);
		
	}
	
	public static List<String> getInputsForCategory(Class<? extends JCasAnnotator_ImplBase> cls) {
		
		TypeCapability cap = cls.getAnnotationsByType(TypeCapability.class)[0];
		
		return Arrays.asList(cap.inputs());
	}
	
	public static List<String> getOutputsForCategory(Class<? extends JCasAnnotator_ImplBase> cls) {
		TypeCapability cap = cls.getAnnotationsByType(TypeCapability.class)[0];
		
		return Arrays.asList(cap.outputs());		
	}
	
	public static List<Class<? extends JCasAnnotator_ImplBase>> getAnnotatorsForCategory(String cat) {
		return annotators.get(cat);
	}

	public static void detectDKProComponents() throws IOException {
		
		List<Class<? extends JCasAnnotator_ImplBase>> segmenters = new ArrayList<>();
		List<Class<? extends JCasAnnotator_ImplBase>> posTaggers = new ArrayList<>();
		List<Class<? extends JCasAnnotator_ImplBase>> lemmatizers = new ArrayList<>();
		List<Class<? extends JCasAnnotator_ImplBase>> phoneticTranscriptors = new ArrayList<>();
		List<Class<? extends JCasAnnotator_ImplBase>> namedEntityRecognizers = new ArrayList<>();
		List<Class<? extends JCasAnnotator_ImplBase>> constituencyParsers = new ArrayList<>();
		List<Class<? extends JCasAnnotator_ImplBase>> dependencyParsers = new ArrayList<>();
		List<Class<? extends JCasAnnotator_ImplBase>> chunkers = new ArrayList<>();
		
		List<Class<? extends ResourceCollectionReaderBase>> readers = new ArrayList<>();
		
		for (String pacName : packages()) {
			System.out.println(pacName);
			Reflections ref = new Reflections(pacName);
			
			Set<Class<? extends ResourceCollectionReaderBase>> readerClassesToLookAt = ref.getSubTypesOf(ResourceCollectionReaderBase.class);
			
			for (Class<? extends ResourceCollectionReaderBase> cls : readerClassesToLookAt) {
				if (!"JCasCollectionReader_ImplBase".equals(cls.getSimpleName())) {
					readers.add(cls);
				}
			}
			
			Set<Class<? extends JCasAnnotator_ImplBase>> classesToLookAt = ref.getSubTypesOf(JCasAnnotator_ImplBase.class);
			
			for (Class<? extends JCasAnnotator_ImplBase> cls : classesToLookAt) {
				
				if (cls.getSuperclass() != null && cls.getSuperclass().getName().equals(SegmenterBase.class.getName())) {
					segmenters.add(cls);
					continue;
				}
				
				if (cls.getSuperclass() != null && cls.getSuperclass().getName().equals(PhoneticTranscriptor_ImplBase.class.getName())) {
					phoneticTranscriptors.add(cls);
					continue;
				}
				
				if (cls.isAnnotationPresent(Component.class)) {
					
					Component comp = cls.getAnnotationsByType(Component.class)[0];
					
					switch (comp.value()) {
					case OperationType.POS_TAGGING:
						posTaggers.add(cls);
						continue;
					case OperationType.PART_OF_SPEECH_TAGGER:
						posTaggers.add(cls);
						continue;
					case OperationType.MORPHOLOGICAL_TAGGER:
						posTaggers.add(cls);
						continue;
					case OperationType.LEMMATIZER:
						lemmatizers.add(cls);
						continue;
					case OperationType.NAMED_ENTITITY_RECOGNIZER:
						namedEntityRecognizers.add(cls);
						continue;
					case OperationType.CONSTITUENCY_PARSER:
						constituencyParsers.add(cls);
						continue;
					case OperationType.DEPENDENCY_PARSER:
						dependencyParsers.add(cls);
						continue;
					case OperationType.CHUNKER:
						chunkers.add(cls);
						continue;
					}
				}		
			}
			
 		}
		
		writeToFile(SEGMENTERS, encodeAnnotatorList(segmenters));
		writeToFile(POS_TAGGERS, encodeAnnotatorList(posTaggers));
		writeToFile(LEMMATIZERS, encodeAnnotatorList(lemmatizers));
		writeToFile(PHONETIC_TRANSCRIPTORS, encodeAnnotatorList(phoneticTranscriptors));
		writeToFile(NAMED_ENTITY_RECOGNIZERS, encodeAnnotatorList(namedEntityRecognizers));
		writeToFile(CONSTITUENCY_PARSERS, encodeAnnotatorList(constituencyParsers));
		writeToFile(DEPENDENCY_PARSERS, encodeAnnotatorList(dependencyParsers));
		writeToFile(CHUNKERS, encodeAnnotatorList(chunkers));
		
		writeToFile(READERS, encodeReaderList(readers));
		
	}

}
