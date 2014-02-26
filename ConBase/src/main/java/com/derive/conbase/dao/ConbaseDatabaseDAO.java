package com.derive.conbase.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConbaseDatabaseDAO<T> {
	
protected JdbcTemplate jdbcTemplate;

@Autowired
public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
}

public String getDatabase() {
	return "condb_1";
}

public String sqlForDatabase(String sql) {
	if (sql != null) {
		return sql.replaceAll("{{projectdb}}", getDatabase());
	}
	return sql;
}
}

