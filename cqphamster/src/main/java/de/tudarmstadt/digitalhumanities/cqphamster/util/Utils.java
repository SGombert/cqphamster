package de.tudarmstadt.digitalhumanities.cqphamster.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class Utils {
	
	private static boolean win = System.getProperty("os.name").contains("indows");
	
	private static String USER_HOME = System.getProperty("user.home");
	
	public static final String PATH_TO_TEMP = "pathToTemp";
	public static final String PATH_TO_CORPUS_DATA = "pathToCorpusData";
	public static final String IMPORT_ANNOTATORS_CONFIG_PATH = "importAnnotatorsConfigPath";

	
	public static String pathToConfigFolder = USER_HOME + "/.cqphamster/";
	public static String pathToScriptsFolder = pathToConfigFolder + "/scripts/";
	
	private static String pathToConfigFile = pathToConfigFolder + "/conf.properties";
	
	private static Properties props;
	
	public static String getConfigurationValue(String key) throws IOException {
		if (props == null) {
			props = new Properties();
			props.load(new FileInputStream(pathToConfigFile));
		}
		
		return props.getProperty(key);
	}
	
	public static void createStandardConfigurationIfNotFound() throws IOException {
		if (!new File(pathToConfigFile).exists()) {
			new File(pathToConfigFolder).mkdirs();
			new File(pathToConfigFile).createNewFile();
			props = new Properties();
			props.load(Utils.class.getClassLoader().getResourceAsStream("conf.properties"));
			props.store(new FileOutputStream(pathToConfigFile),"");
		}
		
		new File(getConfigurationValue(PATH_TO_CORPUS_DATA)).mkdirs();
		new File(getConfigurationValue(PATH_TO_TEMP)).mkdirs();
		new File(getConfigurationValue(IMPORT_ANNOTATORS_CONFIG_PATH)).mkdirs();
	}
	
	public static void writeScript(String name) throws IOException {
		File scriptFile = new File(pathToScriptsFolder + '/' + name);
		
		if (!scriptFile.exists()) {
			scriptFile.mkdirs();
			scriptFile.createNewFile();
		}
		
		BufferedReader scriptReader = new BufferedReader(new InputStreamReader(Utils.class.getClassLoader().getResourceAsStream("scripts/" + name)));
		BufferedWriter scriptWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(scriptFile)));
		
		String ln;
		while ((ln = scriptReader.readLine()) != null) {
			scriptWriter.append(ln);
			scriptWriter.newLine();
		}
		scriptReader.close();
		scriptWriter.close();
	}
}
