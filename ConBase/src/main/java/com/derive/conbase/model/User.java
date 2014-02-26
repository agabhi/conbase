package com.derive.conbase.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.derive.conbase.util.EmailUtil;

public class User {

	private Long id;

	private String fullName;
	private String email;
	private String password;
	private String confirmationIdentifier;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getConfirmationIdentifier() {
		return confirmationIdentifier;
	}

	public void setConfirmationIdentifier(String confirmationIdentifier) {
		this.confirmationIdentifier = confirmationIdentifier;
	}

	public Map<Boolean, List<String>> validate() {
		Map<Boolean, List<String>> resultMap = new LinkedHashMap<Boolean, List<String>>();
		List<String> messages = new ArrayList<String>();
		boolean status = true;
		if (StringUtils.isBlank(fullName)) {
			status = false;
			messages.add("Fullname cannot be empty.");
		}
		if (StringUtils.isBlank(email)) {
			status = false;
			messages.add("Email cannot be empty.");
		} else {
			if (!EmailUtil.validateEmail(email)) {
				status = false;
				messages.add("Email not valid.");
			}
		}
		if (StringUtils.isBlank(email)) {
			status = false;
			messages.add("Password cannot be empty.");
		} else {
			if (password.trim().length() < 6) {
				status = false;
				messages.add("Password should be atleast 6 characters long.");
			}
		}
		resultMap.put(status, messages);
		return resultMap;
	}
	
}
