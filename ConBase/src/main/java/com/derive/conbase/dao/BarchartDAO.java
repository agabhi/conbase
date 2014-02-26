package com.derive.conbase.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.derive.conbase.model.Barchart;
import com.derive.conbase.model.Layer;
import com.derive.conbase.model.LayerAttributeConfig;
import com.derive.conbase.model.RecordType;
import com.derive.conbase.model.RecordTypeAttribute;
import com.derive.conbase.model.User;
import com.derive.conbase.util.UserUtils;

@Repository
public class BarchartDAO extends DynamicDatabaseDAO<Barchart> 
{

	@Autowired
	LayerDAO layerDAO;
	
	private RowMapper<Barchart> rowMapper = new RowMapper<Barchart>() {

		public Barchart mapRow(ResultSet rs, int rowNum) throws SQLException {
			Barchart barchart = new Barchart();
			barchart.setId(rs.getLong("id"));
			barchart.setName(rs.getString("name"));
			
			User user = new User();
			user.setId(rs.getLong("userId"));
			barchart.setUser(user);
			
			RecordType recordType = new RecordType();
			recordType.setId(rs.getLong("recordTypeId"));
			barchart.setRecordType(recordType);
			
			RecordTypeAttribute recordTypeAttribute = new RecordTypeAttribute();
			recordTypeAttribute.setId(rs.getLong("recordTypeAttributeId"));
			barchart.setRecordTypeAttribute(recordTypeAttribute);
			return barchart;
		}
	};
	
	private RowMapper<Barchart> rowWithRecordTypeAttributeMapper = new RowMapper<Barchart>() {

		public Barchart mapRow(ResultSet rs, int rowNum) throws SQLException {
			Barchart barchart = new Barchart();
			barchart.setId(rs.getLong("id"));
			barchart.setName(rs.getString("name"));
			
			User user = new User();
			user.setId(rs.getLong("userId"));
			barchart.setUser(user);
			
			RecordTypeAttribute recordTypeAttribute = new RecordTypeAttribute();
			recordTypeAttribute.setId(rs.getLong("recordTypeAttributeId"));
			recordTypeAttribute.setName(rs.getString("recordTypeAttributeName"));
			barchart.setRecordTypeAttribute(recordTypeAttribute);
			return barchart;
		}
	};
	
	private RowMapper<LayerAttributeConfig> lacRowMapper = new RowMapper<LayerAttributeConfig>() {

		public LayerAttributeConfig mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			LayerAttributeConfig lac = new LayerAttributeConfig();
			lac.setId(rs.getLong("layerAttributeConfigId"));
			return lac;
		}
	};
	
	public Barchart createBarchart(final Barchart barchart) {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = sqlForDatabase("insert into {{projectdb}}.barchart (name, recordTypeId, recordTypeAttributeId, userId) values (?, ?, ?, ?)");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, barchart.getName());
				ps.setLong(2, barchart.getRecordType().getId());
				ps.setLong(3, barchart.getRecordTypeAttribute().getId());
				ps.setLong(4, UserUtils.getLoggedInUser().getId());
				return ps;
			}
		};
		
		this.jdbcTemplate.update(preparedStatementCreator, holder);
		barchart.setId(holder.getKey().longValue());
		
		if (CollectionUtils.isNotEmpty(barchart.getLayerAttributeConfigs())) {
			final String barchartLayerSql = sqlForDatabase("insert into {{projectdb}}.barchart_layer (barchartId, layerAttributeConfigId, orderIndex) values (?, ?, ?)");
			int orderIndex = 0;
			for (LayerAttributeConfig layerAttributeConfig : barchart.getLayerAttributeConfigs()) {
				++orderIndex;
				final int orderIndexObj = orderIndex;
				LayerAttributeConfig lac = layerDAO
						.findLayerAttributeConfigByValues(layerAttributeConfig);
				if (lac == null) {
					lac = layerDAO.createLayerAttributeConfigByValues(layerAttributeConfig);
				}
				final LayerAttributeConfig lacClone = lac;
				
				PreparedStatementCreator psc = new PreparedStatementCreator() {

					@Override
					public PreparedStatement createPreparedStatement(
							Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement(barchartLayerSql.toString());
						ps.setLong(1, barchart.getId());
						ps.setLong(2, lacClone.getId());
						ps.setInt(3, orderIndexObj);
						return ps;
					}
				};
				
				this.jdbcTemplate.update(psc);
			}
		}
		
		return barchart;
	}
	
	public Barchart findBarchartByRecordTypeAndNameAndUserId(Long recordTypeId, String name, Long userId) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.barchart where recordTypeId = ? AND name = ? AND userId = ?");
		List<Barchart> barcharts = this.jdbcTemplate.query(sql,
				new Object[] { recordTypeId, name, userId }, rowMapper);
		return CollectionUtils.isNotEmpty(barcharts) ? barcharts.get(0) : null;
	}
	
	public Barchart findBarchartByNameAndUserId(String name, Long userId) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.barchart where name = ? AND userId = ?");
		List<Barchart> barcharts = this.jdbcTemplate.query(sql,
				new Object[] { name, userId }, rowMapper);
		return CollectionUtils.isNotEmpty(barcharts) ? barcharts.get(0) : null;
	}
	
	public Barchart findBarchartByIdAndUserId(Long id, Long userId) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.barchart B " +
										  " WHERE B.id = ? AND userId = ?");
		List<Barchart> barcharts = this.jdbcTemplate.query(sql,
				new Object[] { id, userId }, rowMapper);
		Barchart barchart = CollectionUtils.isNotEmpty(barcharts) ? barcharts.get(0) : null;
		if (barchart != null) {
			barchart.setLayerAttributeConfigs(findLayerAttributeConfigIdsById(barchart.getId()));
		}
		return barchart;
	}
	
	public List<LayerAttributeConfig> findLayerAttributeConfigIdsById(Long id) {
		final String sql = sqlForDatabase("select layerAttributeConfigId from {{projectdb}}.barchart_layer BL WHERE BL.barchartId = ? ORDER BY orderIndex");
		List<LayerAttributeConfig> layerAttributeConfigIds = this.jdbcTemplate.query(sql, new Object[] {id}, lacRowMapper);
		return layerAttributeConfigIds;
	}
	
	
	public List<Barchart> findBarchartsByRecordTypeIdAndUserId(Long recordTypeId, Long userId) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.barchart where userid = ? AND recordTypeId = ?");
		List<Barchart> barcharts = this.jdbcTemplate.query(sql,
				new Object[] { userId, recordTypeId }, rowMapper);
		return barcharts;
	}
}
