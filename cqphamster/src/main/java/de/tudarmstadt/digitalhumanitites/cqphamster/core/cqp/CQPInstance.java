package de.tudarmstadt.digitalhumanitites.cqphamster.core.cqp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.digitalhumanities.cqphamster.model.Corpus;
import de.tudarmstadt.digitalhumanities.cqphamster.util.Utils;


public class CQPInstance {

	private static String osSuffix = System.clearProperty("os.name").contains("indows") ? ".exe" : "";
	
	private int queryLockId;
	
	private BufferedWriter toCqp;
	
	private BufferedReader fromCqp;
	
	private BufferedReader errorFromCqp;
	
	private String errorString;
	
	private Process cqp;
	
	public CQPInstance() throws IOException, InterruptedException {
		String pathToCqp = Utils.getConfigurationValue("pathToCwbBins");
		
		String[] args = {
				pathToCqp + "/cqp",
				"-r",
				Utils.getConfigurationValue("pathToCorpusData"),
				"-c",
		};
		
		System.out.println(Utils.getConfigurationValue("pathToCorpusData"));
		
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.directory(new File(Utils.getConfigurationValue("pathToCorpusData")));
		
		this.cqp = pb.start();
		
		this.toCqp = new BufferedWriter(new OutputStreamWriter(this.cqp.getOutputStream(), "utf8"));
		this.fromCqp = new BufferedReader(new InputStreamReader(this.cqp.getInputStream(), "utf8"));
		this.errorFromCqp = new BufferedReader(new InputStreamReader(this.cqp.getErrorStream(), "utf8"));

		
		this.queryLockId = new SecureRandom().nextInt();
		if (this.queryLockId < 0)
			this.queryLockId *= -1;
		
		System.out.println(this.fromCqp.readLine());
		
	}
	
	public List<int[]> getQueryPositions(Corpus corpus, String query) throws IOException {
		System.out.println(query);
		
		StringBuilder queryBuild = new StringBuilder();
		queryBuild.append(corpus.getCorpusCwbId().toUpperCase());
		queryBuild.append(";set QueryLock ");
		queryBuild.append(this.queryLockId);
		queryBuild.append(";A = ");
		queryBuild.append(query);
		queryBuild.append(";unlock ");
		queryBuild.append(this.queryLockId);
		queryBuild.append(";dump A;.EOL.;");
		
		System.out.println(queryBuild.toString());
		
		PrintWriter printWrite = new PrintWriter(this.toCqp);

		printWrite.println(queryBuild.toString());
		printWrite.flush();
		
		boolean errorOccured = false;
		StringBuilder errorStringBuild = new StringBuilder();
		
		while (!this.errorFromCqp.ready() && !this.fromCqp.ready()) {
		}
		
		String ln;
		while (this.errorFromCqp.ready() && (ln = this.errorFromCqp.readLine()) != null) {
			errorOccured = true;
			errorStringBuild.append(ln);
			errorStringBuild.append('\n');
		}

		if (errorOccured) {
			this.errorString = errorStringBuild.toString();
			System.out.print(this.errorString);
			return null;
		}
		
		ArrayList<int[]> positionsList = new ArrayList<>();
		
		while (this.fromCqp.ready() && !(ln = this.fromCqp.readLine()).equals("-::-EOL-::-")) {
			String[] splitLn = ln.split("\t");
			
			int[] pos = {
					Integer.parseInt(splitLn[0]),
					Integer.parseInt(splitLn[1]),
					Integer.parseInt(splitLn[2]),
					Integer.parseInt(splitLn[3])
			};
			positionsList.add(pos);
		}
		
		return positionsList;
	}
	
	public void close() {
		this.cqp.destroy();
	}

	public String getErrorString() {
		return errorString;
	}


}
