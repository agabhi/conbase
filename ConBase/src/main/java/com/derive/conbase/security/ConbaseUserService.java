package com.derive.conbase.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.transaction.annotation.Transactional;

import com.derive.conbase.model.Project;
import com.derive.conbase.service.ProjectService;
import com.derive.conbase.service.UserService;

public class ConbaseUserService implements UserDetailsService {

	private static final Logger logger = Logger
			.getLogger(ConbaseUserService.class);

	@Autowired
	UserService userService;

	@Autowired
	ProjectService projectService;

	@Transactional
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {

		logger.debug("************ userName = " + username);

		// Declare a null Spring UserLogin
		com.derive.conbase.security.User userDetails = null;

		try {
			// Search database for a user that matches the specified username
			// get the user view object
			com.derive.conbase.model.User loginUser = userService
					.findUserByEmailId(username);
			;
			// com.derive.conbase.model.User loginUser = null;
			// //if (entityUser != null) {
			// loginUser = EntityToVOConverter.convertToVO(entityUser);
			// }

			// Populate the userDetails object with details from the view user
			// getAuthorities() will translate the access level to the correct
			// role type

			UserAttributeEditor configAttribEd = new UserAttributeEditor();
			configAttribEd.setAsText("guestuser,ROLE_SECURE");

			// for other users get the details from database
			UserAttribute userAttributes = (UserAttribute) configAttribEd
					.getValue();
			Collection<GrantedAuthority> authorities = userAttributes
					.getAuthorities();
			if (loginUser != null) {
				authorities = getAuthorities(loginUser);
			}

			// userDetails = new User(username, userAttributes.getPassword(),
			// userAttributes
			// .isEnabled(), true, true, true, authorities);
			userDetails = new com.derive.conbase.security.User(loginUser,
					loginUser.getEmail(), loginUser.getPassword(), true, true,
					true, true, authorities);
			if (loginUser != null) {
				List<Project> projects = projectService
						.getAllOwnedProjectsByUserId(loginUser.getId());
				if (CollectionUtils.isNotEmpty(projects)) {
					for (Project project : projects) {
						if (project.isActive() != null && project.isActive()) {
							userDetails.setCurrentProject(project);
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error in retrieving user", e);
			throw new UsernameNotFoundException("Error in retrieving user", e);
		}
		// Return user to Spring for processing. the actual authentication is
		// done by spring
		return userDetails;
	}

	private Collection<GrantedAuthority> getAuthorities(
			com.derive.conbase.model.User user) {
		// Create a list of grants for this user
		List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
		authList.add(new SimpleGrantedAuthority("admin"));
		return authList;
	}
}
