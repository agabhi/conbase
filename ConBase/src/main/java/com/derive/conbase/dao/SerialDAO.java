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

import com.derive.conbase.model.Serial;
import com.derive.conbase.util.UserUtils;

@Repository
public class SerialDAO extends DynamicDatabaseDAO<Serial> {
	private RowMapper<Serial> rowMapper = new RowMapper<Serial>() {

		public Serial mapRow(ResultSet rs, int rowNum) throws SQLException {
			Serial serial = new Serial();
			serial.setId(rs.getLong("id"));
			serial.setName(rs.getString("name"));
			serial.setValue(rs.getInt("value"));
			serial.setPrefix(rs.getString("prefix"));
			return serial;
		}
	};

	public void save(final Serial serial) {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = sqlForDatabase("insert into {{projectdb}}.serial (name, prefix, value, createdOn, createdBy) values (?, ?, ?, ?, ?)");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, serial.getName());
				ps.setString(2, serial.getPrefix());
				ps.setInt(3, 1);
				ps.setDate(4, new java.sql.Date(new Date().getTime()));
				ps.setLong(5, UserUtils.getLoggedInUser().getId());
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator, holder);
		serial.setId(holder.getKey().longValue());

		return;
	}
	
	public List<Serial> findSerials() {
		final String sql = sqlForDatabase("select * from {{projectdb}}.serial");
		List<Serial> serials = this.jdbcTemplate.query(sql, rowMapper);
		return serials;
	}
	
	public Serial findSerialByName(String name) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.serial where name = ?");
		List<Serial> serials = this.jdbcTemplate.query(sql,
				new Object[] { name }, rowMapper);
		return CollectionUtils.isNotEmpty(serials) ? serials.get(0) : null;
	}
	
	public Serial findSerialById(Long id) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.serial where id = ?");
		List<Serial> serials = this.jdbcTemplate.query(sql,
				new Object[] { id }, rowMapper);
		return CollectionUtils.isNotEmpty(serials) ? serials.get(0) : null;
	}
	
	public Serial findSerialByPrefix(String name) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.serial where prefix = ?");
		List<Serial> serials = this.jdbcTemplate.query(sql,
				new Object[] { name }, rowMapper);
		return CollectionUtils.isNotEmpty(serials) ? serials.get(0) : null;
	}
}
