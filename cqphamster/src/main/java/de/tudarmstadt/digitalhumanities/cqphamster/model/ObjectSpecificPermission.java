package de.tudarmstadt.digitalhumanities.cqphamster.model;

import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
public class ObjectSpecificPermission extends GeneralPermission {
	
	public final static String MAY_VIEW = "may_view";
	public final static String MAY_EDIT = "may_edit";
	public final static String MAY_DELETE = "may_delete";
	
	public final static String LIMITED_ACCESS = "limited_access";
	
	public final static String MAY_EXPORT = "may_export";
	
	private int respectiveObjectId;

	public ObjectSpecificPermission(String affects, boolean singleUserOrUserGroup, int respectiveId, int respectiveObjectId) {
		super(affects, singleUserOrUserGroup, respectiveId);
		
		this.setRespectiveObjectId(respectiveObjectId);
	}

	public int getRespectiveObjectId() {
		return respectiveObjectId;
	}

	public void setRespectiveObjectId(int respectiveObjectId) {
		this.respectiveObjectId = respectiveObjectId;
	}

}
