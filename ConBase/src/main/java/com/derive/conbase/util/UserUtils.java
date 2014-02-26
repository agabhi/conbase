package com.derive.conbase.util;

import org.springframework.security.core.context.SecurityContextHolder;

import com.derive.conbase.model.Project;
import com.derive.conbase.model.User;

public class UserUtils {
	
	public static com.derive.conbase.security.User getSecurityUser() {
		com.derive.conbase.security.User user = (com.derive.conbase.security.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		return user;
	}
	
	public static User getLoggedInUser() {
		com.derive.conbase.security.User user = (com.derive.conbase.security.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		return user.getUser();
	}
	
	public static Project getCurrentProject() {
		com.derive.conbase.security.User user = (com.derive.conbase.security.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		return user.getCurrentProject();
	}
}
