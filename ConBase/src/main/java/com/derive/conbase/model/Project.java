package com.derive.conbase.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class Project {
	
	private Long id;
	private String name;
	private String description;
	private User user;
	private List<User> invitedUsers;
	private Date createdOn;
	private Boolean active;
	private Short allowedUsers;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Boolean isActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public List<User> getInvitedUsers() {
		return invitedUsers;
	}
	public void setInvitedUsers(List<User> invitedUsers) {
		this.invitedUsers = invitedUsers;
	}
	public Map<Boolean, List<String>> validate() {
		Map<Boolean, List<String>> resultMap = new LinkedHashMap<Boolean, List<String>>();
		List<String> messages = new ArrayList<String>();
		boolean status = true;
		if (StringUtils.isBlank(name)) {
			status = false;
			messages.add("Name cannot be empty.");
		}
		if (user == null || user.getId() == null) {
			status = false;
			messages.add("User should be assigned to a project.");
		}
		resultMap.put(status, messages);
		return resultMap;
	}
	public Short getAllowedUsers() {
		return allowedUsers;
	}
	public void setAllowedUsers(Short allowedUsers) {
		this.allowedUsers = allowedUsers;
	}
	
}
