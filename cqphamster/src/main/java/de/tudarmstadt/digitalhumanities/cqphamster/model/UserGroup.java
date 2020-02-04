package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.util.List;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
public class UserGroup extends Getable {

	private int id;
	@IndexedBy
	private String name;
	
	private String description;
	
	private String avatar;
	
	private boolean mayCreateCorpora;
	
	private boolean adminRights;

	private List<Integer> corpusPermissionIds;
	
	@IndexedBy	
	private int ownerId;
	
	private List<Integer> usersIds;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getAvatar() {
		return avatar;
	}

	public boolean isMayCreateCorpora() {
		return mayCreateCorpora;
	}

	public boolean isAdminRights() {
		return adminRights;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setMayCreateCorpora(boolean mayCreateCorpora) {
		this.mayCreateCorpora = mayCreateCorpora;
	}

	public void setAdminRights(boolean adminRights) {
		this.adminRights = adminRights;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof UserGroup && ((UserGroup)o).getId() == this.getId();
	}

	public List<Integer> getCorpusPermissionIds() {
		return corpusPermissionIds;
	}

	public void setCorpusPermissionIds(List<Integer> corpusPermissionIds) {
		this.corpusPermissionIds = corpusPermissionIds;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public List<Integer> getUsersIds() {
		return usersIds;
	}

	public void setUsersIds(List<Integer> usersIds) {
		this.usersIds = usersIds;
	}
}
