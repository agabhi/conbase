package com.derive.conbase.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.derive.conbase.dao.ProjectDAO;
import com.derive.conbase.model.Project;

@Service
public class ProjectService {
	
	@Autowired
	ProjectDAO projectDAO;
	
	public void addProject(Project project) {
		projectDAO.addProject(project);
	}
	
	public List<Project> getAllOwnedProjectsByUserId(long userId) {
		return projectDAO.getAllOwnedProjectsByUserId(userId);
	}
	
	public List<Project> getAllInvitedProjectsByUserId(long userId) {
		return projectDAO.getAllInvitedProjectsByUserId(userId);
	}
	
	public Project getProjectById(long projectId) {
		return projectDAO.findProjectById(projectId);
	}
	
	public void addUserToProject(final Long userId, final Long projectId) {
		projectDAO.addUserToProject(userId, projectId);
	}
	
	public void removeUserFromProject(final Long userId, final Long projectId) {
		projectDAO.removeUserFromProject(userId, projectId);
	}
}
