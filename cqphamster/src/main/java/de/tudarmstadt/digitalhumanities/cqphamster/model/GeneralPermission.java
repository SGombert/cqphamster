package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.util.HashSet;
import java.util.Set;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
public class GeneralPermission extends Getable  {
	
	public static final String MAY_CREATE_AND_EDIT = "may_create";
	public static final String MAY_DELETE = "may_delete";
	
	public static final String MAY_VIEWALL = "may_viewall";
	
	public static final String MAY_EDIT_OTHERS = "may_edit_others";
	public static final String MAY_DELETE_OTHERS = "may_delete_others";
	
	public static final boolean SINGLE_USER = false;
	public static final boolean USER_GROUP = true;
	
	private int id;
	
	@IndexedBy
	private String affects;
	@IndexedBy
	private boolean singleUserOrUserGroup;
	@IndexedBy
	private int respectiveId;
	
	private Set<String> permissions;
	
	public GeneralPermission(String affects, boolean singleUserOrUserGroup, int respectiveId) {
		this.affects = affects;
		this.respectiveId = respectiveId;
		
		this.permissions = new HashSet<>();
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;	
	}

	public String getAffects() {
		return affects;
	}

	public int getRespectiveId() {
		return respectiveId;
	}

	public void setRespectiveId(int respectiveId) {
		this.respectiveId = respectiveId;
	}
	
	public void addPermission(String permission) {
		this.permissions.add(permission);
	}
	
	public void dropPermission(String permission) {
		this.permissions.remove(permission);
	}
}
