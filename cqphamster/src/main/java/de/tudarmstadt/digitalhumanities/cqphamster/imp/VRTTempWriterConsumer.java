package de.tudarmstadt.digitalhumanities.cqphamster.imp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.digitalhumanities.cqphamster.core.CorpusTransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.core.TransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.model.Corpus;
import de.tudarmstadt.digitalhumanities.cqphamster.model.PerSpanAnnotation;
import de.tudarmstadt.digitalhumanities.cqphamster.util.Utils;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.phonetics.type.PhoneticTranscription;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Heading;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

public class VRTTempWriterConsumer extends JCasAnnotator_ImplBase {
	
	public static final String CORPUS_ID = "corpus_id";
	@ConfigurationParameter(name = CORPUS_ID, description = "corpus_id", mandatory = true)
	private int corpusId;
	
	public static final String PATH_TO_VRT = "path_to_vrt";
	@ConfigurationParameter(name = PATH_TO_VRT, description = "vrt_temp_path", mandatory = true)
	private String pathToVrt;
	
	public static final String WRITE_POS = "write_pos";
	@ConfigurationParameter(name = WRITE_POS, description = "write_pos", mandatory = true)
	private boolean writePOS;
	
	public static final String WRITE_LEMMA = "write_lemma";
	@ConfigurationParameter(name = WRITE_LEMMA, description = "write_lemma", mandatory = true)
	private boolean writeLemma;
	
	public static final String WRITE_PHONETICS = "write_phonetics";
	@ConfigurationParameter(name = WRITE_PHONETICS, description = "write_phonetics", mandatory = true)
	private boolean writePhonetics;
	
	public static final String WRITE_NAMED_ENTITY = "write_named_entity";
	@ConfigurationParameter(name = WRITE_NAMED_ENTITY, description = "write_named_entity", mandatory = true)
	private boolean writeNamedEntity;
	
	public static final String WRITE_CHUNKS = "write_chunks";
	@ConfigurationParameter(name = WRITE_CHUNKS, description = "write_chunks", mandatory = true)
	private boolean writeChunks;
	
	public static final String WRITE_MORPHOSEMNATICS = "write_morphosemantics";
	@ConfigurationParameter(name = WRITE_MORPHOSEMNATICS, description = "write_morphosemantics", mandatory = true)
	private boolean writeMorphosemantics;
	
	public static final String WRITE_CONSTITUENTS = "write_constituents";
	@ConfigurationParameter(name = WRITE_CONSTITUENTS, description = "write_constituents", mandatory = true)
	private boolean writeConstituents;
	
	public static final String WRITE_DEPENDENCY = "write_dependency";
	@ConfigurationParameter(name = WRITE_DEPENDENCY, description = "write_dependency", mandatory = true)
	private boolean writeDependency;
	
	private BufferedWriter writer;
	
	private int tokenIndex;
	
	private int constMaxLevel;
	
	private List<String> attributeOrder;
	
	private Set<String> perSpanCategories;
	
	private Set<String> perTokenCategories;
	
	private Map<String,Set<String>> perSpanAttribute;
	
	private Map<String,Integer> typeFrequencies;
	
	private CorpusTransactionManager corpusTransaction;
	
	private TransactionManager transaction;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		
		this.corpusTransaction = new CorpusTransactionManager(this.corpusId);
		this.transaction = new TransactionManager();
		
		try {
			Corpus corpus = (Corpus)this.transaction.getObjectById(Corpus.class.getCanonicalName(), this.corpusId);
			
			corpus.setImportIdentifier("Indexing Corpus");
			this.transaction.addOrUpdateObject(corpus);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}		
		
		this.tokenIndex = 0;
		
		this.attributeOrder = new ArrayList<>();
		this.perSpanCategories = new HashSet<>();
		this.perTokenCategories = new HashSet<>();
		this.perTokenCategories.add("word");
		
		this.perSpanAttribute = new HashMap<>();
		
		this.typeFrequencies = new HashMap<>();
		
		this.attributeOrder.add("id");
		this.attributeOrder.add("idinsentence");
		
		if (writePOS) this.attributeOrder.add("pos");
		if (writeLemma) this.attributeOrder.add("lemma");
		if (writePhonetics) this.attributeOrder.add("phonetictranscription");
		if (writeNamedEntity) this.attributeOrder.add("namedenity");
		if (writeChunks) this.attributeOrder.add("chunk");
		
		if (writeMorphosemantics) {
			this.attributeOrder.add("gender");
			this.attributeOrder.add("number");
			this.attributeOrder.add("case");
			this.attributeOrder.add("degree");
			this.attributeOrder.add("verbform");
			this.attributeOrder.add("tense");
			this.attributeOrder.add("mood");
			this.attributeOrder.add("voice");
			this.attributeOrder.add("definiteness");
			this.attributeOrder.add("person");
			this.attributeOrder.add("aspect");
			this.attributeOrder.add("animacy");
			this.attributeOrder.add("num_type");
			this.attributeOrder.add("possessive");
			this.attributeOrder.add("prontype");
			this.attributeOrder.add("reflex");

		}
		
		if (writeDependency) {
			this.attributeOrder.add("dependencytype");
			this.attributeOrder.add("governor");
			this.attributeOrder.add("governorpos");
			this.attributeOrder.add("governorlemma");
			this.attributeOrder.add("governoridinsentence");
			this.attributeOrder.add("governordependencytype");
		}
		
		this.perTokenCategories.addAll(this.attributeOrder);
		
		try {
			File outFile = new File(this.pathToVrt);
			outFile.getParentFile().mkdirs();
			
			if (!outFile.exists())
				outFile.createNewFile();
			
			this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf8"));
			
			this.writer.write("<corpus>");
			this.writer.newLine();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}
	
	private String getTokenTSVLine(String token, Map<String,String> attributes) {
		StringBuilder strBuild = new StringBuilder();
		
		strBuild.append(token);
		
		for (String attribute : this.attributeOrder) {
			strBuild.append('\t');
			strBuild.append(attributes.get(attribute));
		}
		
		return strBuild.toString();
	}
	
	private String writeOrPlacehold(String str) {
		return str != null && str.length() > 0 ? str : "-";
	}
	
	private Map<String,String> getAttributeMapForToken(int id, JCas aJCas, Token tk, Sentence sent) {
		
		HashMap<String,String> attributeMap = new HashMap<>();
		
		if (this.writePOS)
			attributeMap.put("pos", writeOrPlacehold(JCasUtil.selectCovered(aJCas, POS.class, tk).size() > 0 ? JCasUtil.selectCovered(aJCas, POS.class, tk).get(0).getPosValue() : null));
		
		if (this.writeLemma)
			attributeMap.put("lemma", writeOrPlacehold(JCasUtil.selectCovered(aJCas, Lemma.class, tk).size() > 0 ? JCasUtil.selectCovered(aJCas, Lemma.class, tk).get(0).getValue() : null));

		if (this.writePhonetics)
			attributeMap.put("phonetictranscription", writeOrPlacehold(JCasUtil.selectCovered(aJCas, PhoneticTranscription.class, tk).size() > 0 ? JCasUtil.selectCovered(aJCas, PhoneticTranscription.class, tk).get(0).getTranscription() : null));
		
		if (this.writeNamedEntity)
			attributeMap.put("namedentity", writeOrPlacehold(JCasUtil.selectCovered(aJCas, NamedEntity.class, tk).size() > 0 ? JCasUtil.selectCovered(aJCas, NamedEntity.class, tk).get(0).getValue() : null));
		
		if (this.writeChunks)
			attributeMap.put("chunk", writeOrPlacehold(JCasUtil.selectCovered(aJCas, Chunk.class, tk).size() > 0 ? JCasUtil.selectCovered(aJCas, Chunk.class, tk).get(0).getChunkValue() : null));
		
		if (this.writeMorphosemantics) {
			MorphologicalFeatures feats = JCasUtil.selectCovered(aJCas, MorphologicalFeatures.class, tk).size() > 0 ? JCasUtil.selectCovered(aJCas, MorphologicalFeatures.class, tk).get(0) : null;
		
			if (feats != null) {
				attributeMap.put("gender", writeOrPlacehold(feats.getGender()));
				attributeMap.put("number", writeOrPlacehold(feats.getNumber()));
				attributeMap.put("case", writeOrPlacehold(feats.getCase()));
				attributeMap.put("degree", writeOrPlacehold(feats.getDegree()));
				attributeMap.put("verbForm", writeOrPlacehold(feats.getVerbForm()));
				attributeMap.put("tense", writeOrPlacehold(feats.getTense()));
				attributeMap.put("mood", writeOrPlacehold(feats.getMood()));
				attributeMap.put("voice", writeOrPlacehold(feats.getVoice()));
				attributeMap.put("definiteness", writeOrPlacehold(feats.getDefiniteness()));
				attributeMap.put("person", writeOrPlacehold(feats.getPerson()));
				attributeMap.put("aspect", writeOrPlacehold(feats.getAspect()));
				attributeMap.put("animacy", writeOrPlacehold(feats.getAnimacy()));
				attributeMap.put("negative", writeOrPlacehold(feats.getNegative()));
				attributeMap.put("numType", writeOrPlacehold(feats.getNumType()));
				attributeMap.put("possessive", writeOrPlacehold(feats.getPossessive()));
				attributeMap.put("pronType", writeOrPlacehold(feats.getPronType()));
				attributeMap.put("reflex", writeOrPlacehold(feats.getReflex()));
			} else {
				attributeMap.put("gender", writeOrPlacehold(null));
				attributeMap.put("number", writeOrPlacehold(null));
				attributeMap.put("case", writeOrPlacehold(null));
				attributeMap.put("degree", writeOrPlacehold(null));
				attributeMap.put("verbForm", writeOrPlacehold(null));
				attributeMap.put("tense", writeOrPlacehold(null));
				attributeMap.put("mood", writeOrPlacehold(null));
				attributeMap.put("voice", writeOrPlacehold(null));
				attributeMap.put("definiteness", writeOrPlacehold(null));
				attributeMap.put("person", writeOrPlacehold(null));
				attributeMap.put("aspect", writeOrPlacehold(null));
				attributeMap.put("animacy", writeOrPlacehold(null));
				attributeMap.put("negative", writeOrPlacehold(null));
				attributeMap.put("numType", writeOrPlacehold(null));
				attributeMap.put("possessive", writeOrPlacehold(null));
				attributeMap.put("pronType", writeOrPlacehold(null));
				attributeMap.put("reflex", writeOrPlacehold(null));			
			}

		}
		
		if (this.writeDependency) {
			Dependency dep = JCasUtil.selectCovered(aJCas, Dependency.class, tk).get(0);
			
			attributeMap.put("dependencyType", writeOrPlacehold(dep.getDependencyType()));
			attributeMap.put("governor", writeOrPlacehold(dep.getGovernor().getCoveredText()));

			
			int j = 0;
			boolean isFound = false;
			for (Token t : JCasUtil.selectCovered(aJCas, Token.class, sent)) {
				if (t.equals(dep.getGovernor())) {
					isFound = true;
					break;
					
				}

				j++;
			}
			
			if (isFound) {
				attributeMap.put("governorPos", writeOrPlacehold(JCasUtil.selectCovered(aJCas, POS.class, dep.getGovernor()).size() > 0 ? JCasUtil.selectCovered(aJCas, POS.class, dep.getGovernor()).get(0).getPosValue() : null));
				attributeMap.put("governorLemma", writeOrPlacehold(JCasUtil.selectCovered(aJCas, Lemma.class, dep.getGovernor()).size() > 0 ? JCasUtil.selectCovered(aJCas, Lemma.class, dep.getGovernor()).get(0).getValue() : null));
				attributeMap.put("governorIdInSentence", new Integer(j).toString());
			} else {
				attributeMap.put("governorPos", "-");
				attributeMap.put("governorLemma", "-");
				attributeMap.put("governorIdInSentence", "-");
			}
		}
		
		return attributeMap;
	}
	
	private void mapCountIncrease(String key, Map<String,Integer> map) {
		if (map.get(key) == null)
			map.put(key, 0);
		
		map.put(key, map.get(key) +1);
	}
	
	private void addToListInMap(int key, PerSpanAnnotation anno, Map<Integer,List<PerSpanAnnotation>> map) {
		if (map.get(key) == null)
			map.put(key, new ArrayList<>());
		
		map.get(key).add(anno);
	}
	
	private void addToSetInMap(String key, String toAdd, Map<String, Set<String>> m) {
		if (m.get(key) == null)
			m.put(key, new HashSet<>());
		
		m.get(key).add(toAdd);
	}
	
	private void findPerSpanAnnotationIndices(PerSpanAnnotation anno, HashMap<Integer, Integer> beginMap, HashMap<Integer, Integer> endMap, int begin, int end) {
		int m = begin;
		int p = begin;
		
		int idx = -1;
		
		while (idx == -1) {
			if (beginMap.get(p) != null) {
				idx = p;
				break;
			}
			if (beginMap.get(m) != null) {
				idx = m;
				break;
			}
			m--;
			p++;
		}
		
		anno.setBegin(idx);
		
		m = end;
		p = end;
		
		idx = -1;
		
		while (idx == -1) {
			if (endMap.get(p) != null) {
				idx = p;
				break;
			}
			if (endMap.get(m) != null) {
				idx = m;
				break;
			}
			m--;
			p++;
		}
		
		anno.setEnd(idx);
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
			
		List<DocumentMetaData> metaDataList = new ArrayList<>(JCasUtil.select(aJCas, DocumentMetaData.class));
		
		DocumentMetaData metadata = metaDataList.size() > 0 ? metaDataList.get(0) : null;
		
		HashMap<String,String> documentPropertyMap = new HashMap<>();
		
		documentPropertyMap.put("language", aJCas.getDocumentLanguage());
		
		if (metadata != null) {
			documentPropertyMap.put("title", metadata.getDocumentTitle().replace(" ", "_"));
			documentPropertyMap.put("id", metadata.getDocumentId());
		}
		
		HashMap<Integer,Integer> beginCharIdxToBeginTokenIdx = new HashMap<>();
		HashMap<Integer,Integer> endCharIdxToEndTokenIdx = new HashMap<>();
		HashMap<Integer,de.tudarmstadt.digitalhumanities.cqphamster.model.Token> tokenByIndex = new HashMap<>();
		HashMap<de.tudarmstadt.digitalhumanities.cqphamster.model.Token,Integer> indexByToken = new HashMap<>();
		
		HashMap<Integer,List<PerSpanAnnotation>> beginPerSpan = new HashMap<>();
		HashMap<Integer,List<PerSpanAnnotation>> endPerSpan = new HashMap<>();
		
		int previousTokenIndex = this.tokenIndex;
		
		try {
			PerSpanAnnotation docDB = new PerSpanAnnotation();
			docDB.setBegin(this.tokenIndex);
			addToListInMap(docDB.getBegin(), docDB, beginPerSpan);
			docDB.setAttributes(documentPropertyMap);
			docDB.setName("doc");
			
			perSpanAttribute.put("doc", documentPropertyMap.keySet());
			
			for (Sentence sent : JCasUtil.select(aJCas, Sentence.class)) {
				System.out.println("in sentence");
				PerSpanAnnotation sentenceDB = new PerSpanAnnotation();
				sentenceDB.setName("s");
				sentenceDB.setBegin(tokenIndex);
				corpusTransaction.addPerSpanAnnotation(sentenceDB);
				
				int idInSent = 0;
				
				for (Token tk : JCasUtil.selectCovered(aJCas, Token.class, sent)) {	
					beginCharIdxToBeginTokenIdx.put(tk.getBegin(), this.tokenIndex);
					endCharIdxToEndTokenIdx.put(tk.getEnd(), this.tokenIndex);

					mapCountIncrease(tk.getCoveredText(), this.typeFrequencies);
					
					Map<String,String> attributeMap = getAttributeMapForToken(this.tokenIndex, aJCas, tk, sent);			
					
					de.tudarmstadt.digitalhumanities.cqphamster.model.Token dbToken = new de.tudarmstadt.digitalhumanities.cqphamster.model.Token();
					dbToken.setId(this.tokenIndex);
					dbToken.setCorpusId(this.corpusId);
					dbToken.setAnnotations(attributeMap);
					dbToken.setTokenString(tk.getCoveredText());
					
					attributeMap.put("id", new Integer(this.tokenIndex).toString());
					attributeMap.put("idinsentence", new Integer(idInSent).toString());
					
					tokenByIndex.put(this.tokenIndex, dbToken);
					indexByToken.put(dbToken, this.tokenIndex);
					
					corpusTransaction.addToken(dbToken);
					
					this.tokenIndex++;
					idInSent++;
				}

				sentenceDB.setName("s");
				sentenceDB.setEnd(this.tokenIndex -1);
				corpusTransaction.addPerSpanAnnotation(sentenceDB);
				
				this.perSpanCategories.add(sentenceDB.getName());
				
				if (this.perSpanAttribute.get("s") == null)
					this.perSpanAttribute.put("s", new HashSet<String>());
				
				this.perSpanAttribute.get("s").addAll(sentenceDB.getAttributes().keySet());
				
				addToListInMap(sentenceDB.getBegin(), sentenceDB, beginPerSpan);
				addToListInMap(sentenceDB.getEnd(), sentenceDB, endPerSpan);
			}
			
			docDB.setEnd(this.tokenIndex -1);
			this.corpusTransaction.addPerSpanAnnotation(docDB);
			
			addToListInMap(docDB.getEnd(), docDB, endPerSpan);		
			
			this.perSpanCategories.add(docDB.getName());
			
			for (Heading head : JCasUtil.select(aJCas, Heading.class)) {
				PerSpanAnnotation headDB = new PerSpanAnnotation();
				findPerSpanAnnotationIndices(headDB, beginCharIdxToBeginTokenIdx, endCharIdxToEndTokenIdx, head.getBegin(), head.getEnd());
				headDB.setName("h");
				corpusTransaction.addPerSpanAnnotation(headDB);
				
				this.perSpanCategories.add(headDB.getName());
				if (this.perSpanAttribute.get(headDB.getName()) == null)
					this.perSpanAttribute.put(headDB.getName(), new HashSet<String>());
				
				this.perSpanAttribute.get(headDB.getName()).addAll(headDB.getAttributes().keySet());
				
				addToListInMap(headDB.getBegin(), headDB, beginPerSpan);
				addToListInMap(headDB.getEnd(), headDB, endPerSpan);
			}
			
			for (Paragraph para : JCasUtil.select(aJCas, Paragraph.class)) {
				PerSpanAnnotation paraDB = new PerSpanAnnotation();
				findPerSpanAnnotationIndices(paraDB, beginCharIdxToBeginTokenIdx, endCharIdxToEndTokenIdx, para.getBegin(), para.getEnd());
				paraDB.setName("p");
				corpusTransaction.addPerSpanAnnotation(paraDB);
				
				if (this.perSpanAttribute.get(paraDB.getName()) == null)
					this.perSpanAttribute.put(paraDB.getName(), new HashSet<String>());
				
				this.perSpanAttribute.get(paraDB.getName()).addAll(paraDB.getAttributes().keySet());
				
				this.perSpanCategories.add(paraDB.getName());
				
				addToListInMap(paraDB.getBegin(), paraDB, beginPerSpan);
				addToListInMap(paraDB.getEnd(), paraDB, endPerSpan);
			}
			
			for (Constituent cons : JCasUtil.select(aJCas, Constituent.class)) {
				PerSpanAnnotation consDB = new PerSpanAnnotation();
				findPerSpanAnnotationIndices(consDB, beginCharIdxToBeginTokenIdx, endCharIdxToEndTokenIdx, cons.getBegin(), cons.getEnd());
				consDB.setName("cons_" + cons.getConstituentType());
				corpusTransaction.addPerSpanAnnotation(consDB);
				
				if (cons.getSyntacticFunction() != null && cons.getSyntacticFunction().length() > 0) {
					Map<String,String> atts = new HashMap<>();
					
					atts.put("syntacticFunction", cons.getSyntacticFunction());
					
					addToSetInMap(consDB.getName(), "syntacticFunction", this.perSpanAttribute);
					
					consDB.setAttributes(atts);
				}
				
				if (this.perSpanAttribute.get(consDB.getName()) == null)
					this.perSpanAttribute.put(consDB.getName(), new HashSet<String>());
				
				this.perSpanAttribute.get(consDB.getName()).addAll(consDB.getAttributes().keySet());
				
				this.perSpanCategories.add(consDB.getName());
				
				
				addToListInMap(consDB.getBegin(), consDB, beginPerSpan);
				addToListInMap(consDB.getEnd(), consDB, endPerSpan);
			}
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
		
		int consLevelCounter = 0;
		int consMaxCounter = 0;
		
		try {
			
			Stack<PerSpanAnnotation> perSpanXMLStack = new Stack<>();
			
			for (int i = previousTokenIndex; i < this.tokenIndex; i++) {
				
				if (beginPerSpan.get(i) != null) {
					for (PerSpanAnnotation anno : beginPerSpan.get(i)) {
						perSpanXMLStack.add(anno);
						
						if (anno.getName().contains("cons_")) {
							consLevelCounter++;
							
							if (consLevelCounter > consMaxCounter)
								consMaxCounter = consLevelCounter;
						}
						
						this.writer.write(anno.getOpeningTag());
						this.writer.newLine();
					}
				}
				
				this.writer.write(getTokenTSVLine(tokenByIndex.get(i).getTokenString(), tokenByIndex.get(i).getAnnotations()));
				this.writer.newLine();
				
				if (endPerSpan.get(i) != null) {
					for (PerSpanAnnotation annoToDump : endPerSpan.get(i)) {
						//PerSpanAnnotation actualAnno = perSpanXMLStack.pop();
						
						if (annoToDump.getName().contains("cons_"))
							consLevelCounter--;
						
						this.writer.write(annoToDump.getClosingTag());
						this.writer.newLine();
					}
				}
			}
			
			this.constMaxLevel = consMaxCounter;
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
		
	}
	
	private List<String> getCWBEncodeParams() throws IOException {
		ArrayList<String> paramsList = new ArrayList<>();
		
		Corpus c = (Corpus)this.transaction.getObjectById(Corpus.class.getCanonicalName(), corpusId);
		
		paramsList.add(Utils.getConfigurationValue("pathToCwbBins") + "/cwb-encode");
		
		paramsList.add("-d");
		paramsList.add(Utils.getConfigurationValue("pathToCorpusData") + "\\data\\" + c.getCorpusCwbId());
		
		new File(Utils.getConfigurationValue("pathToCorpusData") + "\\data\\" + c.getCorpusCwbId()).mkdirs();
		
		paramsList.add("-R");	
		paramsList.add(c.getCorpusCwbId().toLowerCase());
		
		paramsList.add("-f");
		paramsList.add(this.pathToVrt);
		
		paramsList.add("-c");
		paramsList.add("utf8");
		
		paramsList.add("-x");
		
		paramsList.add("-p");
		paramsList.add("word");
		
		for (String perTokenCategory : this.attributeOrder) {
			paramsList.add("-P");
			paramsList.add(perTokenCategory);
		}
		
		for (String perSpanCategory : this.perSpanCategories) {
			if (this.perSpanAttribute.get(perSpanCategory) != null)
				paramsList.add("-V");
			else
				paramsList.add("-S");
			
			StringBuilder strBuild = new StringBuilder();
			strBuild.append(perSpanCategory);
			strBuild.append(":");
			strBuild.append(perSpanCategory.contains("cons_") ? this.constMaxLevel : 0);
			
			if (this.perSpanAttribute.get(perSpanCategory) != null) {
				for (String att : this.perSpanAttribute.get(perSpanCategory)) {					
					
					strBuild.append('+');
					strBuild.append(att);	
				}
			}
			
			paramsList.add(strBuild.toString());
		}
		
		paramsList.add("-S");
		paramsList.add("corpus:0");
		
		paramsList.forEach(e -> System.out.println(e));
		
		return paramsList;
	}
	
	private List<String> getCwbHuffParams() throws IOException {
		List<String> paramsList = new ArrayList<>();
		
		Corpus c = (Corpus)this.transaction.getObjectById(Corpus.class.getCanonicalName(), corpusId);
		
		paramsList.add(Utils.getConfigurationValue("pathToCwbBins") +"/cwb-huffcode");
		
		paramsList.add("-r");
		paramsList.add(Utils.getConfigurationValue("pathToCorpusData"));
		
		paramsList.add("-A");
		paramsList.add(c.getCorpusCwbId().toUpperCase());
		
		return paramsList;
	}
	
	private List<String> getCwbCompressRedxParams() throws IOException {
		List<String> paramsList = new ArrayList<>();

		Corpus c = (Corpus)this.transaction.getObjectById(Corpus.class.getCanonicalName(), corpusId);
		
		paramsList.add(Utils.getConfigurationValue("pathToCwbBins") + "/cwb-compress-rdx");
		
		paramsList.add("-r");
		paramsList.add(Utils.getConfigurationValue("pathToCorpusData"));
		
		paramsList.add("-A");
		paramsList.add(c.getCorpusCwbId().toUpperCase());
		
		return paramsList;
	}
	
	private List<String> getCwbMakeParams() throws IOException {
		List<String> ret = new ArrayList<>();
		
		ret.add(Utils.getConfigurationValue("pathToCwbBins") + "/cwb-makeall");
		
		Corpus c = (Corpus)this.transaction.getObjectById(Corpus.class.getCanonicalName(), corpusId);
		
		ret.add("-r");
		ret.add(Utils.getConfigurationValue("pathToCorpusData"));
		
		ret.add("-M");
		ret.add("512");
		
		ret.add("-V");
		
		ret.add(c.getCorpusCwbId().toUpperCase());
		
		return ret;
	}
	
	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		try {
			this.writer.write("</corpus>");
			
			this.writer.close();
			
			ProcessBuilder pb = new ProcessBuilder(getCWBEncodeParams());
			pb.directory(new File(Utils.getConfigurationValue("pathToCorpusData")));
			
			Process proc = pb.start();
			proc.waitFor();
			
			BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream(), "ISO-8859-1"));
			
			String errStr;
			StringBuilder strBuild = new StringBuilder();
			while ((errStr = err.readLine()) != null) {
				strBuild.append(errStr);
				strBuild.append(' ');
			}
			errStr = strBuild.toString();
			
			System.out.println("Err str:" + errStr);
			
			if (errStr.length() > 0) {
				
				Corpus corpus = (Corpus)this.transaction.getObjectById(Corpus.class.getCanonicalName(), this.corpusId);
				corpus.setImportIdentifier("Failed: " + errStr);
				this.transaction.addOrUpdateObject(corpus);
				
				throw new Exception("CWB encode failed. Quit with following message: " + errStr);
			}
			err.close();
			
			pb = new ProcessBuilder(getCwbMakeParams());
			
			proc = pb.start();
			proc.waitFor();
			
			err = new BufferedReader(new InputStreamReader(proc.getErrorStream(), "ISO-8859-1"));
			
			strBuild = new StringBuilder();
			while ((errStr = err.readLine()) != null) {
				strBuild.append(errStr);
				strBuild.append(' ');
			}
			errStr = strBuild.toString();
			
			System.out.println("Err str:" + errStr);
			
			if (errStr.length() > 0) {
				
				Corpus corpus = (Corpus)this.transaction.getObjectById(Corpus.class.getCanonicalName(), this.corpusId);
				corpus.setImportIdentifier("Failed: " + errStr);
				corpus.setPerSpanAnnotationCategories(this.perSpanCategories);
				corpus.setPerTokenAnnotationCategories(this.perTokenCategories);
				this.transaction.addOrUpdateObject(corpus);

				
				throw new Exception("CWB encode failed. Quit with following message: " + errStr);
			}
			err.close();
			
			System.out.println("Complete");
			
			Corpus corpus = (Corpus)this.transaction.getObjectById(Corpus.class.getCanonicalName(), this.corpusId);
			corpus.setImportIdentifier("Complete");
			corpus.setImporting(false);
			corpus.setPerSpanAnnotationCategories(this.perSpanCategories);
			corpus.setPerTokenAnnotationCategories(this.perTokenCategories);
			this.transaction.addOrUpdateObject(corpus);
			
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
