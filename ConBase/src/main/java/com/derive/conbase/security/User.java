package com.derive.conbase.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.derive.conbase.model.Project;

public class User extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = -2718742746451434858L;

	private com.derive.conbase.model.User user;
	
	private Project currentProject;

	public com.derive.conbase.model.User getUser() {
		return user;
	}

	public void setDisplayName(com.derive.conbase.model.User user) {
		this.user = user;
	}

	public User(com.derive.conbase.model.User user, String username,
			String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, authorities);
		setDisplayName(user);
	}

	public Project getCurrentProject() {
		return currentProject;
	}

	public void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
	}
	
	
}