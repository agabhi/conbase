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
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.derive.conbase.model.User;

@Repository
public class UserDAO extends ConbaseDatabaseDAO<User> {

	public void register(final User user) {
		
		PasswordEncoder encoder = new Md5PasswordEncoder();
		user.setPassword(encoder.encodePassword(user.getPassword(),null));
		
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = "insert into user (fullName, email, password, active, confirmationIdentifier, createdOn) values (?, ?, ?, ?, ?, ?)";
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, user.getFullName());
				ps.setString(2, user.getEmail());
				ps.setString(3, user.getPassword());
				ps.setBoolean(4, false);
				ps.setString(5, user.getConfirmationIdentifier());
				ps.setDate(6, new java.sql.Date(new Date().getTime()));
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator, holder);
		user.setId(holder.getKey().longValue());
		
	}
	
	public User findUserByEmailId(String emailId) {
		List<User> users = this.jdbcTemplate.query(
				"select * from user where email = ?",
				new Object[] { emailId }, new RowMapper<User>() {
					public User mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						User user = new User();
						user.setId(rs.getLong("id"));
						user.setEmail(rs.getString("email"));
						user.setPassword(rs.getString("password"));
						user.setFullName(rs.getString("fullName"));
						return user;
					}
				});
		return CollectionUtils.isNotEmpty(users) ? users.get(0) : null;
	}
	
	public User findUserByConfirmationIdentifier(String confirmationIdentifier) {
		List<User> users = this.jdbcTemplate.query(
				"select * from user where confirmationIdentifier = ?",
				new Object[] { confirmationIdentifier }, new RowMapper<User>() {
					public User mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						User user = new User();
						user.setId(rs.getLong("id"));
						user.setEmail(rs.getString("email"));
						user.setFullName(rs.getString("fullName"));
						user.setPassword(rs.getString("password"));
						return user;
					}
				});
		return CollectionUtils.isNotEmpty(users) ? users.get(0) : null;
	}
	
	public void confirmUser(Long userId) {
		final String sql = "update user set active = 1 where id = ?";
		this.jdbcTemplate.update(sql, new Object[] {userId});
	}
}
