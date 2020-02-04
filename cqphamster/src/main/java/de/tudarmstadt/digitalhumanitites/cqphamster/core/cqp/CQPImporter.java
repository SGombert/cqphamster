package de.tudarmstadt.digitalhumanitites.cqphamster.core.cqp;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import de.tudarmstadt.digitalhumanities.cqphamster.util.Utils;

public class CQPImporter {
	
	private VRTDumpBuilder dumpBuilder;

	public CQPImporter(VRTDumpBuilder dumpBuilder) {
		this.dumpBuilder = dumpBuilder;
	}
	
	public void doImport() throws IOException, InterruptedException {
		String pathToTemp = Utils.getConfigurationValue("pathToTemp");
		
		File pathToTempFile = new File(pathToTemp);
		pathToTempFile.mkdirs();
		
		StringBuilder corpusTempFilePathBuilder = new StringBuilder();
		
		corpusTempFilePathBuilder.append(pathToTemp);
		corpusTempFilePathBuilder.append('/');
		corpusTempFilePathBuilder.append(dumpBuilder.getCorpusId());
		corpusTempFilePathBuilder.append(".vrt");
		
		File tempVRT = new File(corpusTempFilePathBuilder.toString());
		
		tempVRT.createNewFile();
		
		BufferedWriter o = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempVRT), "utf8"));
		o.append(this.dumpBuilder.getVRTString());
		o.close();
		
		String[] args = this.dumpBuilder.getCQPImportParameters();
		
		Process encode = Runtime.getRuntime().exec(args);
		
		encode.waitFor();
		
	}
}
