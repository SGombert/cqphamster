package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.util.List;

import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
public class CorpusImportExportFormat {

	private int id;
	
	private String name;
	
	private String importReaderClassPath;
	
	private String exportWriterClassPath;
	
	private List<String> annotationCategories;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImportReaderClassPath() {
		return importReaderClassPath;
	}

	public String getExportWriterClassPath() {
		return exportWriterClassPath;
	}

	public List<String> getAnnotationCategories() {
		return annotationCategories;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImportReaderClassPath(String importReaderClassPath) {
		this.importReaderClassPath = importReaderClassPath;
	}

	public void setExportWriterClassPath(String exportWriterClassPath) {
		this.exportWriterClassPath = exportWriterClassPath;
	}

	public void setAnnotationCategories(List<String> annotationCategories) {
		this.annotationCategories = annotationCategories;
	}
}
