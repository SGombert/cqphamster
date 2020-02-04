package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.DoNotSend;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.NeedsObjectSpecificPermission;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.NeedsPermission;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
@NeedsPermission
@NeedsObjectSpecificPermission
public class Corpus extends Getable {

	private int id;
	
	private String name;
	
	private String license;
	
	private String description;
	@IndexedBy
	private int ownerId;
	
	private int sectionSize;
	@DoNotSend
	private List<Integer> groupPermissionIds;
	
	private ZonedDateTime creationDate;
	
	private Set<String> perSpanAnnotationCategories;

	private Set<String> perTokenAnnotationCategories;

	private List<Integer> queryIds;
	
	private boolean importing;

	private String importIdentifier;
	
	@IndexedBy
	private int languageId;
	
	@IndexedBy
	private String corpusCwbId;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLicense() {
		return license;
	}

	public String getDescription() {
		return description;
	}

	public int getSectionSize() {
		return sectionSize;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSectionSize(int sectionSize) {
		this.sectionSize = sectionSize;
	}
	
	public String getImportIdentifier() {
		return importIdentifier;
	}

	public void setImportIdentifier(String importIdentifier) {
		this.importIdentifier = importIdentifier;
	}
	
	public ZonedDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(ZonedDateTime creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Corpus && ((Corpus)o).getId() == this.getId();
	}

	public List<Integer> getQueryIds() {
		return queryIds;
	}

	public void setQueryIds(List<Integer> queryIds) {
		this.queryIds = queryIds;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public List<Integer> getGroupPermissionIds() {
		return groupPermissionIds;
	}

	public void setGroupPermissionIds(List<Integer> groupPermissionIds) {
		this.groupPermissionIds = groupPermissionIds;
	}

	public int getLanguageId() {
		return languageId;
	}

	public void setLanguageId(int languageId) {
		this.languageId = languageId;
	}

	public boolean isImporting() {
		return importing;
	}

	public void setImporting(boolean importing) {
		this.importing = importing;
	}

	public String getCorpusCwbId() {
		return corpusCwbId;
	}

	public void setCorpusCwbId(String corpusCwbId) {
		this.corpusCwbId = corpusCwbId;
	}

	public Set<String> getPerSpanAnnotationCategories() {
		return perSpanAnnotationCategories;
	}

	public void setPerSpanAnnotationCategories(Set<String> perSpanAnnotationCategories) {
		this.perSpanAnnotationCategories = perSpanAnnotationCategories;
	}

	public Set<String> getPerTokenAnnotationCategories() {
		return perTokenAnnotationCategories;
	}

	public void setPerTokenAnnotationCategories(Set<String> perTokenAnnotationCategories) {
		this.perTokenAnnotationCategories = perTokenAnnotationCategories;
	}
	
}
