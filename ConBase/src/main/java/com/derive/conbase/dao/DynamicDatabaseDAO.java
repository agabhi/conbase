package com.derive.conbase.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.derive.conbase.exception.NoProjectSelectedException;

/**
 * Base JPA implementation for all DAOs
 * 
 * @param <T> generic type for the persistent entity
 */
@Transactional
public abstract class DynamicDatabaseDAO<T> {
		
	protected JdbcTemplate jdbcTemplate;
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
	
	public String sqlForDatabase(String sql) {
		if (sql != null) {
			com.derive.conbase.security.User securityUser = (com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (securityUser != null && securityUser.getCurrentProject() != null && securityUser.getCurrentProject().getId() != null) {
				return sql.replaceAll("\\{\\{projectdb\\}\\}", "condb_"+securityUser.getCurrentProject().getId());
			} else {
				throw new NoProjectSelectedException(); 
			}
		}
		return sql;
	}
}

