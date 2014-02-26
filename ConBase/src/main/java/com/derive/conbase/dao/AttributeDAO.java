package com.derive.conbase.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.derive.conbase.model.Attribute;
import com.derive.conbase.model.LayerAttributeConfig;

@Repository
public class AttributeDAO extends DynamicDatabaseDAO<Attribute> {
	
	@Autowired
	LayerDAO layerDAO;
	
	private RowMapper<Attribute> rowMapper = new RowMapper<Attribute>() {

		public Attribute mapRow(ResultSet rs, int rowNum) throws SQLException {
			Attribute attribute = new Attribute();
			attribute.setId(rs.getLong("id"));
			attribute.setName(rs.getString("name"));
			attribute.setType(rs.getShort("type"));
			attribute.setOptionsFromValue(rs.getString("value"));
			return attribute;
		}
	};

	public void save(Attribute attribute) {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = sqlForDatabase("insert into {{projectdb}}.attribute (name, type, value, active) values (?, ?, ?, ?)");
		final Attribute localAttribute = attribute;
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, localAttribute.getName());
				ps.setShort(2, localAttribute.getType());
				ps.setString(3, localAttribute.getValue());
				ps.setBoolean(4, true);
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator, holder);
		attribute.setId(holder.getKey().longValue());
		return;
	}
	
	public void update(Attribute attribute) {
		final String sql = sqlForDatabase("update {{projectdb}}.attribute SET name = ?, type = ?, value = ? WHERE id = ?");
		this.jdbcTemplate.update(sql, new Object[] {attribute.getName(), attribute.getType(), attribute.getValue(), attribute.getId()});
		return;
	}
	
	public void delete(Long attributeId) {
		final String sql = sqlForDatabase("update {{projectdb}}.attribute SET active = 0 WHERE id = ?");
		this.jdbcTemplate.update(sql, new Object[] {attributeId});
		final String layerAttributeSql = sqlForDatabase("DELETE FROM {{projectdb}}.layer_attribute WHERE attributeId = ?");
		this.jdbcTemplate.update(layerAttributeSql, new Object[] {attributeId});
		
		final String recordUpdateSql = sqlForDatabase("UPDATE {{projectdb}}.record SET layerAttributeConfigId = ? WHERE layerAttributeConfigId = ?");
		final String barchartLayerDeleteSql = sqlForDatabase("DELETE FROM {{projectdb}}.barchart_layer WHERE layerAttributeConfigId = ?");
		final String lacIdSql = sqlForDatabase("SELECT DISTINCT LACV.layerAttributeConfigId FROM {{projectdb}}.layer_attribute_config_values LACV WHERE LACV.attributeId = ?");
		final String lacUpdateSql = sqlForDatabase("UPDATE {{projectdb}}.layer_attribute_config SET attributeIds = ? WHERE id = ?");
		
		
		List<Long> lacIds = this.jdbcTemplate.queryForList(lacIdSql, new Object[] {attributeId}, Long.class);
		if (CollectionUtils.isNotEmpty(lacIds)) {
			List<LayerAttributeConfig> lacs = layerDAO.findLayerAttributeConfigsByConfigIds(lacIds);
			if (CollectionUtils.isNotEmpty(lacs)) {
				for (LayerAttributeConfig lac : lacs) {
					this.jdbcTemplate.update(barchartLayerDeleteSql, new Object[] {lac.getId()});
					LinkedHashMap<Long, String> attributeValueMap = lac.getAttributeValueMap();
					if (attributeValueMap != null) {
						attributeValueMap.remove(attributeId);
						LayerAttributeConfig duplicateLac = layerDAO.findLayerAttributeConfigByValues(lac);
						if (duplicateLac != null) {
							this.jdbcTemplate.update(recordUpdateSql, new Object[] {duplicateLac.getId(), lac.getId()});
							this.jdbcTemplate.update(sqlForDatabase("DELETE FROM {{projectdb}}.layer_attribute_config_values WHERE layerAttributeConfigId = "+lac.getId()));
							this.jdbcTemplate.update(sqlForDatabase("DELETE FROM {{projectdb}}.layer_attribute_config WHERE id = "+lac.getId()));
						} else {
							this.jdbcTemplate.update(lacUpdateSql, new Object[] {lac.getAttributeIds(), lac.getId()});
						}
					}
				}
				final String layerConfigValueDeleteSql = sqlForDatabase("DELETE FROM {{projectdb}}.layer_attribute_config_values WHERE layerAttributeConfigId IN ("+StringUtils.join(lacIds.toArray())+") AND attributeId = "+attributeId);
				this.jdbcTemplate.update(layerConfigValueDeleteSql);
			}
		}
		
		return;
	}

	public Attribute findAttributeByName(String name) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.attribute where active = 1 AND name = ?");
		List<Attribute> attributes = this.jdbcTemplate.query(sql,
				new Object[] { name }, rowMapper);
		return CollectionUtils.isNotEmpty(attributes) ? attributes.get(0) : null;
	}
	
	public List<Attribute> findAttributesByLayerId(long layerId) {
		final String sql = sqlForDatabase("select A.* from {{projectdb}}.layer_attribute LA inner join {{projectdb}}.attribute A ON LA.layerId = ? AND LA.attributeId = A.id");
		List<Attribute> attributes = this.jdbcTemplate.query(sql,
				new Object[] { layerId }, rowMapper);
		return attributes;
	}

	public List<Attribute> findAttributes() {
		final String sql = sqlForDatabase("select * from {{projectdb}}.attribute WHERE active = 1");
		List<Attribute> attributes = this.jdbcTemplate.query(sql, rowMapper);
		return attributes;
	}
	
	public List<Attribute> findAttributesByType(Short type) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.attribute where active = 1 AND type = ?");
		List<Attribute> attributes = this.jdbcTemplate.query(sql, new Object[] {type}, rowMapper);
		return attributes;
	}
}
