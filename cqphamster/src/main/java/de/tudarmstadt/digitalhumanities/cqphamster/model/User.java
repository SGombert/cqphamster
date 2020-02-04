package de.tudarmstadt.digitalhumanities.cqphamster.model;

import java.time.ZonedDateTime;
import java.util.List;

import de.tudarmstadt.digitalhumanities.cqphamster.core.Getable;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.DoNotSend;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.IndexedBy;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.NeedsHashing;
import de.tudarmstadt.digitalhumanities.cqphamster.javaannos.Requestable;

@Requestable
public class User extends Getable {


	private int id;
	
	@IndexedBy
	private String username;
	
	@DoNotSend
	@NeedsHashing
	private String password;
	
	@IndexedBy
	private String fullName;
	
	@IndexedBy
	private String institution;
	
	@IndexedBy
	private String role;
	
	@IndexedBy
	private String eMail;
	

	private String avatar;
	@IndexedBy
	private boolean admin;
	@IndexedBy
	private boolean mayManageCorpora;
	

	public List<Integer> getOwnedGroupsIds() {
		return ownedGroupsIds;
	}

	public List<Integer> getMembershipGroupsIds() {
		return membershipGroupsIds;
	}

	public List<Integer> getQueryIds() {
		return queryIds;
	}

	public List<Integer> getCorporaIds() {
		return corporaIds;
	}

	public void setOwnedGroupsIds(List<Integer> ownedGroupsIds) {
		this.ownedGroupsIds = ownedGroupsIds;
	}

	public void setMembershipGroupsIds(List<Integer> membershipGroupsIds) {
		this.membershipGroupsIds = membershipGroupsIds;
	}

	public void setQueryIds(List<Integer> queryIds) {
		this.queryIds = queryIds;
	}

	public void setCorporaIds(List<Integer> corporaIds) {
		this.corporaIds = corporaIds;
	}

	private String apiKey;
	

	private ZonedDateTime registrationDate;

	private List<Integer> ownedGroupsIds;

	private List<Integer> membershipGroupsIds;
	
	private List<Integer> queryIds;
	
	private List<Integer> corporaIds;

	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getFullName() {
		return fullName;
	}

	public String getInstitution() {
		return institution;
	}

	public String getRole() {
		return role;
	}

	public String geteMail() {
		return eMail;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}	
	
	public ZonedDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(ZonedDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof User && ((User)o).getId() == this.getId();
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isMayManageCorpora() {
		return mayManageCorpora;
	}

	public void setMayManageCorpora(boolean mayManageCorpora) {
		this.mayManageCorpora = mayManageCorpora;
	}
}
