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

import com.derive.conbase.model.JqGridData;
import com.derive.conbase.model.Layer;
import com.derive.conbase.model.LayerAttribute;
import com.derive.conbase.model.Structure;
import com.derive.conbase.model.Structure;
import com.derive.conbase.util.UserUtils;

@Repository
public class StructureDAO extends DynamicDatabaseDAO<Structure> {
	
	private RowMapper<Structure> rowMapper = new RowMapper<Structure>() {

		public Structure mapRow(ResultSet rs, int rowNum) throws SQLException {
			Structure structure = new Structure();
			structure.setId(rs.getLong("id"));
			structure.setName(rs.getString("name"));
			return structure;
		}
	};
	
	public List<Structure> findActiveStructures() {
		final String sql = sqlForDatabase("select * from {{projectdb}}.structure where active = 1");
		List<Structure> structures = this.jdbcTemplate.query(sql, rowMapper);
		return structures;
	}
	
	public JqGridData<Structure> findStructuresByPage(int pageNo, int size) {
		int recordNo = (pageNo > 0 ? pageNo - 1 : 0) * size;
		final String sql;
		List<Structure> structures = null;
		if (size <= 0) {
			sql = sqlForDatabase("select * from {{projectdb}}.structure where active = 1");
			structures = this.jdbcTemplate.query(sql, new Object[] {}, rowMapper);
		} else {
			sql = sqlForDatabase("select * from {{projectdb}}.structure where active = 1 limit ?, ?");
			structures = this.jdbcTemplate.query(sql, new Object[] {
					recordNo, size }, rowMapper);
		}
		final String countSql = sqlForDatabase("select count(*) from {{projectdb}}.structure where active = 1");
		int count = this.jdbcTemplate.queryForInt(countSql);
		int totalPages = 1;
		if (size > 0) {
			totalPages = count / size + (count % size > 0 ? 1 : 0);
		}

		JqGridData<Structure> gridData = new JqGridData<Structure>(totalPages, pageNo,
				count, structures);
		return gridData;
	}
	
	public Structure findStructureById(long id) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.structure where id = ?");
		List<Structure> structures = this.jdbcTemplate.query(sql, new Object[] { id },
				rowMapper);
		return CollectionUtils.isNotEmpty(structures) ? structures.get(0) : null;
	}
	
	public Structure findActiveStructureByName(String name) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.structure where active = 1 and name = ?");
		List<Structure> structures = this.jdbcTemplate.query(sql,
				new Object[] { name }, rowMapper);
		return CollectionUtils.isNotEmpty(structures) ? structures.get(0) : null;
	}
	
	public void updateStructure(final Structure structure) {
		final String sql = sqlForDatabase("update {{projectdb}}.structure set name = ?, modifiedOn = ?, modifiedBy = ? where id = ?");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, structure.getName());
				ps.setDate(2, new java.sql.Date(new Date().getTime()));
				ps.setLong(3, UserUtils.getLoggedInUser().getId());
				ps.setLong(4, structure.getId());
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator);
	}

	public void save(final Structure structure) {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = sqlForDatabase("insert into {{projectdb}}.structure (name, active, createdOn, createdBy) values (?, ?, ?, ?)");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, structure.getName());
				ps.setBoolean(2, structure.isActive());
				ps.setDate(3, new java.sql.Date(new Date().getTime()));
				ps.setLong(4, UserUtils.getLoggedInUser().getId());
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator, holder);
		structure.setId(holder.getKey().longValue());
	}

	public void deactivate(final Long structureId) {
		final String sql = sqlForDatabase("update {{projectdb}}.structure set active = 0 where id = ?");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, structureId);
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator);
	}
	
}
