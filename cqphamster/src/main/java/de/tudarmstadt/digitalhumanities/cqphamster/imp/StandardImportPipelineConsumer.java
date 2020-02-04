package de.tudarmstadt.digitalhumanities.cqphamster.imp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.digitalhumanities.cqphamster.core.CorpusTransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.core.TransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.model.PerSpanAnnotation;
import de.tudarmstadt.digitalhumanitites.cqphamster.core.cqp.CQPImporter;
import de.tudarmstadt.digitalhumanitites.cqphamster.core.cqp.VRTDumpBuilder;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.phonetics.type.PhoneticTranscription;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Div;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Heading;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

public class StandardImportPipelineConsumer extends JCasAnnotator_ImplBase {
	public static final String CORPUS_ID = "corpus_id";
	@ConfigurationParameter(name = CORPUS_ID, description = "corpus_id", mandatory = true)
	private int corpusId;

	private static void addAnnotationIfThere(JCas jCas, Class<? extends Annotation> type, int begin, int end,
			HashMap<String, String> map, String accessMethodName)

			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {

		Collection<? extends Annotation> col = JCasUtil.selectCovered(jCas, type, begin, end);

		if (col.size() > 0) {
			Method accessMethod = type.getMethod(accessMethodName);

			map.put(type.getSimpleName(), (String) accessMethod.invoke(new ArrayList<>(col).get(0)));
		}

	}

	private static void putOrPlacehold(String key, String value, HashMap<String, String> map) {
		map.put(key, value.length() > 0 ? value : "-");
	}

	private static void addMorphologicalCategoriesIfThere(JCas jCas, int begin, int end,
			HashMap<String, String> map) {
		
		Collection<MorphologicalFeatures> col = JCasUtil.selectCovered(jCas, MorphologicalFeatures.class, begin, end);

		if (col.size() > 0) {
			MorphologicalFeatures feats = new ArrayList<>(col).get(0);

			putOrPlacehold("Animacy", feats.getAnimacy(), map);
			putOrPlacehold("Aspect", feats.getAspect(), map);
			putOrPlacehold("Case", feats.getCase(), map);
			putOrPlacehold("Definiteness", feats.getDefiniteness(), map);
			putOrPlacehold("Degree", feats.getDegree(), map);
			putOrPlacehold("Gender", feats.getGender(), map);
			putOrPlacehold("Mood", feats.getMood(), map);
			putOrPlacehold("Negative", feats.getNegative(), map);
			putOrPlacehold("Number", feats.getNumber(), map);
			putOrPlacehold("NumType", feats.getNumType(), map);
			putOrPlacehold("Person", feats.getPerson(), map);
			putOrPlacehold("Possessive", feats.getPossessive(), map);
			putOrPlacehold("PronType", feats.getPronType(), map);
			putOrPlacehold("Reflex", feats.getReflex(), map);
			putOrPlacehold("Tense", feats.getTense(), map);
			putOrPlacehold("Voice", feats.getVoice(), map);
		}
	}

	private static void addGovernorAnnoIfThere(JCas jCas, Token governor, Class<? extends Annotation> type,
			HashMap<String, String> annotationMap, String accessMethodName)

			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {
		
		Collection<?> anno = JCasUtil.selectCovered(jCas, type, governor);

		if (anno.size() > 0) {
			Method accessMethod = type.getMethod(accessMethodName);

			annotationMap.put("Governor" + type.getSimpleName(),
					(String) accessMethod.invoke(new ArrayList<>(anno).get(0)));
		}
	}

	private static void addDependencyCategoriesIfThere(JCas jCas, Token token,
			HashMap<String, String> annotationMap)

			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {
		
		Collection<Dependency> deps = JCasUtil.selectCovered(jCas, Dependency.class, token);

		if (deps.size() > 0) {
			Dependency dep = new ArrayList<>(deps).get(0);

			annotationMap.put("DependencyType", dep.getDependencyType());
			annotationMap.put("Governor", dep.getGovernor().getCoveredText());

			if (dep.getDependencyType().equals("ROOT")) {
				addGovernorAnnoIfThere(jCas, dep.getGovernor(), POS.class, annotationMap, "getPosValue");
				addGovernorAnnoIfThere(jCas, dep.getGovernor(), Lemma.class, annotationMap, "getValue");
				addGovernorAnnoIfThere(jCas, dep.getGovernor(), PhoneticTranscription.class, annotationMap, "getTranscription");
				addGovernorAnnoIfThere(jCas, dep.getGovernor(), NamedEntity.class, annotationMap, "getValue");
				addGovernorAnnoIfThere(jCas, dep.getGovernor(), Chunk.class, annotationMap, "getChunkValue");
			} else {
				addGovernorAnnoIfThere(jCas, token, POS.class, annotationMap, "getPosValue");
				addGovernorAnnoIfThere(jCas, token, Lemma.class, annotationMap, "getValue");
				addGovernorAnnoIfThere(jCas, token, PhoneticTranscription.class, annotationMap, "getTranscription");
				addGovernorAnnoIfThere(jCas, token, NamedEntity.class, annotationMap, "getValue");
				addGovernorAnnoIfThere(jCas, token, Chunk.class, annotationMap, "getChunkValue");
			}
		}
	}
	
	private static void addGenericPerSpanAnnotation(JCas jcas, Map<Integer,Integer> beginMap, 
			Map<Integer,Integer> endMap, ThreadPoolExecutor tp, 
			VRTDumpBuilder vrtDumpBuilder, CorpusTransactionManager corTrans, 
			Class<? extends Annotation> type) {
		
		for (Annotation anno : JCasUtil.select(jcas, type)) {
			
			tp.execute(() -> {
				PerSpanAnnotation annotation = new PerSpanAnnotation();
				
				annotation.setName("p");
				annotation.setBegin(beginMap.get(anno.getBegin()));
				annotation.setEnd(endMap.get(anno.getEnd()));
				
				try {
					corTrans.addPerSpanAnnotation(annotation);
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				vrtDumpBuilder.addPerSpanAnnotation(annotation);
			});
			
		}
		
	}
	
	private static void addConstituentPerSpanAnnotation(JCas jcas, Map<Integer,Integer> beginMap, 
			Map<Integer,Integer> endMap, ThreadPoolExecutor tp, 
			VRTDumpBuilder vrtDumpBuilder, CorpusTransactionManager corTrans) {
		
		for (Constituent consti : JCasUtil.select(jcas, Constituent.class)) {
			
			tp.execute(() -> {
				PerSpanAnnotation annotation = new PerSpanAnnotation();
				
				annotation.setName(consti.getConstituentType());
				annotation.setBegin(beginMap.get(consti.getBegin()));
				annotation.setEnd(endMap.get(consti.getEnd()));
				
				try {
					corTrans.addPerSpanAnnotation(annotation);
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				vrtDumpBuilder.addPerSpanAnnotation(annotation);
			});
			
		}
	}
	
	@Override
	public void process(JCas aJCas) 
			
			throws AnalysisEngineProcessException {
		
		CorpusTransactionManager corTrans = new CorpusTransactionManager(corpusId);
		TransactionManager man = new TransactionManager();

		int i = 0;
		// TODO make thread pool configuarable
		ThreadPoolExecutor tp = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
		
		VRTDumpBuilder vrtDumpBuilder = new VRTDumpBuilder(this.corpusId);
		
		Map<Integer,Integer> tokenBeginPositionsToTokenIndices 
			= Collections.synchronizedMap(new HashMap<>());
		
		Map<Integer,Integer> tokenEndPositionsToTokenIndices 
			= Collections.synchronizedMap(new HashMap<>());

		for (Sentence sent : JCasUtil.select(aJCas, Sentence.class)) {

			Collection<Token> tokens = JCasUtil.selectCovered(Token.class, sent);

			final int j = i;

			tp.execute(() -> {
				
				int iInClosure = j;

				for (Token tk : tokens) {

					de.tudarmstadt.digitalhumanities.cqphamster.model.Token mdTk = 
							new de.tudarmstadt.digitalhumanities.cqphamster.model.Token();

					mdTk.setTokenString(tk.getCoveredText());
					mdTk.setCorpusId(corpusId);
					mdTk.setId(iInClosure);
					
					tokenBeginPositionsToTokenIndices.put(tk.getBegin(), mdTk.getId());

					HashMap<String, String> annos = new HashMap<>();

					try {
						annos.put("id", new Integer(j).toString());

						addAnnotationIfThere(aJCas, POS.class, tk.getBegin(), tk.getEnd(), annos, "getPosValue");
						addAnnotationIfThere(aJCas, Lemma.class, tk.getBegin(), tk.getEnd(), annos, "getValue");
						addAnnotationIfThere(aJCas, PhoneticTranscription.class, tk.getBegin(), tk.getEnd(), annos,
								"getTranscription");
						addAnnotationIfThere(aJCas, NamedEntity.class, tk.getBegin(), tk.getEnd(), annos, "getValue");
						addAnnotationIfThere(aJCas, Chunk.class, tk.getBegin(), tk.getEnd(), annos, "getChunkValue");

						addMorphologicalCategoriesIfThere(aJCas, tk.getBegin(), tk.getEnd(), annos);
						
						addDependencyCategoriesIfThere(aJCas, tk, annos);

						mdTk.setAnnotations(annos);

						corTrans.addToken(mdTk);
						
						vrtDumpBuilder.addToken(mdTk);

					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException | NoSuchFieldException e) {
							e.printStackTrace();
					}

					iInClosure++;
				}
			});
			
			i += tokens.size();
		}
		
		try {
			// TODO max import timeout configable
			tp.awaitTermination(1, TimeUnit.DAYS);
			
			addGenericPerSpanAnnotation(aJCas, tokenBeginPositionsToTokenIndices, 
					tokenEndPositionsToTokenIndices, tp, vrtDumpBuilder, 
					corTrans, Paragraph.class);
			
			addGenericPerSpanAnnotation(aJCas, tokenBeginPositionsToTokenIndices, 
					tokenEndPositionsToTokenIndices, tp, vrtDumpBuilder, 
					corTrans, Heading.class);
			
			addGenericPerSpanAnnotation(aJCas, tokenBeginPositionsToTokenIndices, 
					tokenEndPositionsToTokenIndices, tp, vrtDumpBuilder, 
					corTrans, Sentence.class);
			
			addConstituentPerSpanAnnotation(aJCas, tokenBeginPositionsToTokenIndices, 
					tokenEndPositionsToTokenIndices, tp, vrtDumpBuilder, 
					corTrans);
			
			
			
			//new CQPImporter(vrtDumpBuilder).doImport();
			
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}

	}

}
