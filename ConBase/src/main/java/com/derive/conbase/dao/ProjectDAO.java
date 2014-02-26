package com.derive.conbase.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.derive.conbase.model.Project;
import com.derive.conbase.model.User;

@Repository
public class ProjectDAO extends ConbaseDatabaseDAO<Project> {
	
	private RowMapper<Project> rowMapper = new RowMapper<Project>() {

		public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
			Project project = new Project();
			project.setId(rs.getLong("id"));
			project.setName(rs.getString("name"));
			project.setDescription(rs.getString("description"));
			project.setActive(rs.getBoolean("active"));
			project.setCreatedOn(rs.getDate("createdOn"));
			return project;
		}
	};
	
	private RowMapper<Project> projectWithUserMapper = new RowMapper<Project>() {

		public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
			Project project = new Project();
			project.setId(rs.getLong("id"));
			project.setName(rs.getString("name"));
			project.setDescription(rs.getString("description"));
			project.setActive(rs.getBoolean("active"));
			project.setCreatedOn(rs.getDate("createdOn"));
			
			User user = new User();
			user.setId(rs.getLong("userId"));
			user.setFullName(rs.getString("fullName"));
			user.setEmail(rs.getString("email"));
			project.setUser(user);
			
			return project;
		}
	};
	
	private RowMapper<User> projectUserMapper = new RowMapper<User>() {

		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getLong("userId"));
			user.setFullName(rs.getString("fullName"));
			user.setEmail(rs.getString("email"));
			return user;
		}
	};
	
	public void addProject(final Project project) {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "insert into project (name, description, userId, active, createdOn) values (?, ?, ?, ?, ?)";
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, project.getName());
				ps.setString(2, project.getDescription());
				ps.setLong(3, project.getUser().getId());
				ps.setBoolean(4, project.isActive());
				ps.setDate(5, new java.sql.Date(new Date().getTime()));
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator, holder);
		project.setId(holder.getKey().longValue());

	}
	
	public List<Project> getAllOwnedProjectsByUserId(long userId) {
		final String sql = "select * from project where userId = ?";
		List<Project> projects = this.jdbcTemplate.query(sql, new Object[] {userId}, rowMapper);
		return projects;
	}
	
	public List<Project> getAllInvitedProjectsByUserId(long userId) {
		final String sql = "select P.* from project_user PU inner join project P ON P.id = PU.projectId AND PU.userId = ?";
		List<Project> projects = this.jdbcTemplate.query(sql, new Object[] {userId}, rowMapper);
		return projects;
	}
	
	public Project findProjectById(long projectId) {
		final String sql = "select P.*, U.fullName, U.email from project P inner join user U ON P.userId = U.id AND P.id = ?";
		List<Project> projects = this.jdbcTemplate.query(sql, new Object[] {projectId}, projectWithUserMapper);
		Project project = CollectionUtils.isNotEmpty(projects) ? projects.get(0) : null;
		if (project != null) {
			final String projectUsersSql = "select U.fullName, U.email, U.id AS userId from project_user PU inner join user U ON PU.userId = U.id AND PU.projectId = ?";
			List<User> invitedUsers = this.jdbcTemplate.query(projectUsersSql, new Object[] {projectId}, projectUserMapper);
			project.setInvitedUsers(invitedUsers);
		}
		return project;
	}
	
	public void addUserToProject(final Long userId, final Long projectId) {
		final String sql = "insert into project_user (projectId, userId, createdOn) values (?, ?, ?)";
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, projectId);
				ps.setLong(2, userId);
				ps.setDate(3, new java.sql.Date(new Date().getTime()));
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator);
	}
	
	public void removeUserFromProject(final Long userId, final Long projectId) {
		final String sql = "delete from project_user WHERE projectId = ? AND userId = ?";
		this.jdbcTemplate.update(sql, new Object[] {projectId, userId});
	}
}
