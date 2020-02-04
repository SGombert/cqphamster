package de.tudarmstadt.digitalhumanities.cqphamster.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.digitalhumanities.cqphamster.core.CorpusTransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.core.TransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.model.Corpus;
import de.tudarmstadt.digitalhumanities.cqphamster.model.Token;
import de.tudarmstadt.digitalhumanities.cqphamster.model.User;
import de.tudarmstadt.digitalhumanities.cqphamster.model.UserGroup;
import de.tudarmstadt.digitalhumanitites.cqphamster.core.cqp.CQPInstance;
import de.tudarmstadt.digitalhumanitites.cqphamster.core.cqp.CQPQueryPositions;

@RestController
@RequestMapping
public class Api {
	
	@GetMapping("/internal/corpora/{corpusId}/query/{cqpQuery}")
	public @ResponseBody List<int[]> login(@PathVariable(value="corpusId") int corpusId, @PathVariable(value="cqpQuery") String cqpQuery) throws IOException, InterruptedException {
		//CQPInstance cqpInstance = new CQPInstance();
		
		// TODO rights management
		
		// TODO queryPositionsLimit dependent on user group rights
		
		TransactionManager man = new TransactionManager();
		
		Corpus corpus = (Corpus) man.getObjectById(Corpus.class.getCanonicalName(), corpusId);
		
		if (corpus != null) {
			CQPInstance inst = new CQPInstance();
			return inst.getQueryPositions(corpus, cqpQuery);
		}
		
		return null;
	}
	
	@GetMapping("/internal/corpora/{corpusId}/kwic")
	public @ResponseBody List<List<Token>> concordance(@PathVariable(value="corpusId") int corpusId, @RequestParam int from, @RequestParam int to, @RequestParam int leftContext, @RequestParam int rightContext) {
		
		// TODO rights management
		
		CorpusTransactionManager corTrans = new CorpusTransactionManager(corpusId);
		
		int startFrom = from - leftContext;
		startFrom = startFrom > -1 ? startFrom : 0;
		
		int endAt = to + rightContext;
		
		ArrayList<Integer> idxList = new ArrayList<>(to - from + rightContext + leftContext);
		
		for (int i = startFrom; i <= endAt; i++)
			idxList.add(i);
		
		List<Token> tokens = corTrans.getTokens(idxList);
		for (int i = 0; i < tokens.size(); i++) {
			
		}
		
		List<Token> leftContextL = new ArrayList<>(leftContext);
		List<Token> rightContextL = new ArrayList<>(rightContext);
		List<Token> conc = new ArrayList<>(to - from +1);
		
		List<List<Token>> ret = new ArrayList<>();
		
		ret.add(leftContextL);
		ret.add(conc);
		ret.add(rightContextL);
				
		for (Token tk : conc) {
			System.out.println(tk.getTokenString());
		}
		
		return ret;
	}
	
	@GetMapping("/internal/corpora/{corpusId}/metadata")
	public @ResponseBody Corpus singleCorpus(@PathVariable(value="corpusId") int corpusId) throws Exception {
		return new ApiProvider<Corpus>(Corpus.class.getCanonicalName()).getSingleObject(corpusId);
	}
	
	@GetMapping("/internal/corpora/all")
	public @ResponseBody List<Corpus> allCorpora() throws Exception {
		return new ApiProvider<Corpus>(Corpus.class.getCanonicalName()).getAllObjects();
	}
	
	@GetMapping("/internal/user/{userId}")
	public @ResponseBody User sinlgeUser(@PathVariable(value="userId") int userId) throws Exception {
		return new ApiProvider<User>(User.class.getCanonicalName()).getSingleObject(userId);
	}
	
	@GetMapping("/internal/user/all")
	public @ResponseBody List<User> allUsers() throws Exception {
		return new ApiProvider<User>(User.class.getCanonicalName()).getAllObjects();
	}
	
	@GetMapping("/internal/usergroups/{usergroupId}")
	public @ResponseBody UserGroup singleUsergroup(@PathVariable(value="usergroupId") int usergroupId) throws Exception {
		return new ApiProvider<UserGroup>(UserGroup.class.getCanonicalName()).getSingleObject(usergroupId);
	}
	
	@GetMapping("/internal/usergroups/all")
	public @ResponseBody List<UserGroup> allUsergroups() throws Exception {
		return new ApiProvider<UserGroup>(UserGroup.class.getCanonicalName()).getAllObjects();
	}

}
