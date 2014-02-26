package com.derive.conbase.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.derive.conbase.model.Attribute;
import com.derive.conbase.model.JqGridData;
import com.derive.conbase.model.Layer;
import com.derive.conbase.model.LayerAttribute;
import com.derive.conbase.model.LayerAttributeConfig;
import com.derive.conbase.model.LayerChartEntry;
import com.derive.conbase.util.UserUtils;

@Repository
public class LayerDAO extends DynamicDatabaseDAO<Layer> {

	private RowMapper<Layer> rowMapper = new RowMapper<Layer>() {

		public Layer mapRow(ResultSet rs, int rowNum) throws SQLException {
			Layer layer = new Layer();
			layer.setId(rs.getLong("id"));
			layer.setName(rs.getString("name"));
			return layer;
		}
	};

	private RowMapper<LayerChartEntry> layerChartEntryMapper = new RowMapper<LayerChartEntry>() {

		public LayerChartEntry mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			LayerChartEntry entry = new LayerChartEntry();
			entry.setLevel(rs.getInt("noOfLayers"));
			entry.setFrom(rs.getInt("fromLength"));
			entry.setTo(rs.getInt("toLength"));
			return entry;
		}
	};

	private RowMapper<LayerAttribute> layerAttributeMapper = new RowMapper<LayerAttribute>() {

		public LayerAttribute mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			LayerAttribute layerAttribute = new LayerAttribute();
			layerAttribute.setId(rs.getLong("id"));
			layerAttribute.setAllowedValues(rs.getString("allowedValues"));
			layerAttribute.setOptionsFromOptionsString(rs
					.getString("optionsString"));
			layerAttribute.setMandatory(rs.getBoolean("mandatory"));

			Attribute attribute = new Attribute();
			layerAttribute.setAttribute(attribute);
			attribute.setId(rs.getLong("attribute_id"));
			attribute.setName(rs.getString("attribute_name"));
			attribute.setOptionsFromValue(rs.getString("attribute_value"));
			return layerAttribute;
		}
	};

	private RowMapper<LayerAttributeConfig> lacRowMapper = new RowMapper<LayerAttributeConfig>() {

		public LayerAttributeConfig mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			LayerAttributeConfig lac = new LayerAttributeConfig();
			lac.setId(rs.getLong("id"));
			Layer layer = new Layer();
			layer.setId((rs.getLong("layerId")));
			lac.setLayer(layer);
			lac.setAttributeValueMapFromStrings(rs.getString("attributeIds"),
					rs.getString("valueString"));
			return lac;
		}
	};

	public void updateLayerAttribute(final LayerAttribute layerAttribute) {
		final String updateAttributeSql = sqlForDatabase("update {{projectdb}}.layer_attribute set allowedValues = ?, optionsString = ?, mandatory = ? WHERE id = ?");
		this.jdbcTemplate.update(updateAttributeSql, new Object[] {layerAttribute.getAllowedValues(), layerAttribute.getOptionsString(), layerAttribute.isMandatory(), layerAttribute.getId()});
	}
	public void updateLayer(final Layer layer) {
		final String sql = sqlForDatabase("update {{projectdb}}.layer set name = ?, modifiedOn = ?, modifiedBy = ? where id = ?");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, layer.getName());
				ps.setDate(2, new java.sql.Date(new Date().getTime()));
				ps.setLong(3, UserUtils.getLoggedInUser().getId());
				ps.setLong(4, layer.getId());
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator);

		Layer existingLayer = findLayerById(layer.getId());
		existingLayer.setLayerAttributes(findLayerAttributesByLayerId(layer
				.getId()));
		existingLayer
				.setLayerAttributeConfigs(findLayerAttributeConfigsByLayerId(layer
						.getId()));
		List<LayerAttribute> notFoundAttributes = existingLayer
				.getLayerAttributes();
		final String attributeSql = sqlForDatabase("insert into {{projectdb}}.layer_attribute (layerId, attributeId, allowedValues, optionsString, mandatory) values (?, ?, ?, ?, ?)");
		final String updateAttributeSql = sqlForDatabase("update {{projectdb}}.layer_attribute set allowedValues = ?, optionsString = ?, mandatory = ? WHERE layerId = ? AND attributeId = ?");
		if (CollectionUtils.isNotEmpty(layer.getLayerAttributes())) {
			for (final LayerAttribute layerAttribute : layer
					.getLayerAttributes()) {

				if (layerAttribute.getAttribute().getId() != null) {
					if (existingLayer.getLayerAttributes() == null
							|| !existingLayer.getLayerAttributes().contains(
									layerAttribute)) {
						PreparedStatementCreator psc = new PreparedStatementCreator() {

							@Override
							public PreparedStatement createPreparedStatement(
									Connection connection) throws SQLException {
								PreparedStatement ps = connection
										.prepareStatement(attributeSql
												.toString());
								ps.setLong(1, layer.getId());
								ps.setLong(2, layerAttribute.getAttribute()
										.getId());
								ps.setString(3,
										layerAttribute.getAllowedValues());
								ps.setString(4,
										layerAttribute.getOptionsString());
								ps.setBoolean(5, layerAttribute.isMandatory());
								return ps;
							}
						};
						this.jdbcTemplate.update(psc);
					} else {
						PreparedStatementCreator psc = new PreparedStatementCreator() {

							@Override
							public PreparedStatement createPreparedStatement(
									Connection connection) throws SQLException {
								PreparedStatement ps = connection
										.prepareStatement(updateAttributeSql);
								ps.setString(1,
										layerAttribute.getAllowedValues());
								ps.setString(2,
										layerAttribute.getOptionsString());
								ps.setBoolean(3, layerAttribute.isMandatory());
								ps.setLong(4, layer.getId());
								ps.setLong(5, layerAttribute.getAttribute()
										.getId());
								return ps;
							}
						};
						this.jdbcTemplate.update(psc);
					}

				}
				if (CollectionUtils.isNotEmpty(notFoundAttributes)) {
					notFoundAttributes.remove(layerAttribute);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(notFoundAttributes)) {
			final String layerAttDeleteSql = sqlForDatabase("DELETE FROM {{projectdb}}.layer_attribute WHERE layerId = ? AND attributeId = ?");
			final String recordUpdateSql = sqlForDatabase("UPDATE {{projectdb}}.record SET layerAttributeConfigId = ? WHERE layerAttributeConfigId = ?");
			final String barchartLayerDeleteSql = sqlForDatabase("DELETE FROM {{projectdb}}.barchart_layer WHERE layerAttributeConfigId = ?");
			final String lacIdSql = sqlForDatabase("SELECT DISTINCT LAC.id FROM {{projectdb}}.layer_attribute_config LAC inner join {{projectdb}}.layer_attribute_config_values LACV ON LAC.layerId = ? AND LACV.attributeId = ?");
			final String lacUpdateSql = sqlForDatabase("UPDATE {{projectdb}}.layer_attribute_config SET attributeIds = ? WHERE id = ?");
			
			for (LayerAttribute la : notFoundAttributes) {
				this.jdbcTemplate.update(layerAttDeleteSql, new Object[] {layer.getId(), la.getAttribute().getId()});
				
				//find layer Attribute Config for these attributeIds
				List<Long> lacIds = this.jdbcTemplate.queryForList(lacIdSql, new Object[] {layer.getId(), la.getAttribute().getId()}, Long.class);
				if (CollectionUtils.isNotEmpty(lacIds)) {
					List<LayerAttributeConfig> lacs = findLayerAttributeConfigsByConfigIds(lacIds);
					if (CollectionUtils.isNotEmpty(lacs)) {
						for (LayerAttributeConfig lac : lacs) {
							this.jdbcTemplate.update(barchartLayerDeleteSql, new Object[] {lac.getId()});
							LinkedHashMap<Long, String> attributeValueMap = lac.getAttributeValueMap();
							if (attributeValueMap != null) {
								attributeValueMap.remove(la.getAttribute().getId());
								LayerAttributeConfig duplicateLac = findLayerAttributeConfigByValues(lac);
								if (duplicateLac != null) {
									this.jdbcTemplate.update(recordUpdateSql, new Object[] {duplicateLac.getId(), lac.getId()});
									this.jdbcTemplate.update(sqlForDatabase("DELETE FROM {{projectdb}}.layer_attribute_config_values WHERE layerAttributeConfigId = "+lac.getId()));
									this.jdbcTemplate.update(sqlForDatabase("DELETE FROM {{projectdb}}.layer_attribute_config WHERE id = "+lac.getId()));
								} else {
									this.jdbcTemplate.update(lacUpdateSql, new Object[] {lac.getAttributeIds(), lac.getId()});
								}
							}
						}
						final String layerConfigValueDeleteSql = sqlForDatabase("DELETE FROM {{projectdb}}.layer_attribute_config_values WHERE layerAttributeConfigId IN ("+StringUtils.join(lacIds.toArray())+") AND attributeId = "+la.getAttribute().getId());
						this.jdbcTemplate.update(layerConfigValueDeleteSql);
					}
				}
			}
			
		}
		return;
	}

	public void save(final Layer layer) {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = sqlForDatabase("insert into {{projectdb}}.layer (name, type, active, createdOn, createdBy) values (?, ?, ?, ?, ?)");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, layer.getName());
				ps.setShort(2, layer.getType());
				ps.setBoolean(3, layer.isActive());
				ps.setDate(4, new java.sql.Date(new Date().getTime()));
				ps.setLong(5, UserUtils.getLoggedInUser().getId());
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator, holder);
		layer.setId(holder.getKey().longValue());

		final String attributeSql = sqlForDatabase("insert into {{projectdb}}.layer_attribute (layerId, attributeId, allowedValues, optionsString, mandatory) values (?, ?, ?, ?, ?)");
		if (CollectionUtils.isNotEmpty(layer.getLayerAttributes())) {
			for (final LayerAttribute layerAttribute : layer
					.getLayerAttributes()) {
				if (layerAttribute.getAttribute() != null
						&& layerAttribute.getAttribute().getId() != null) {
					PreparedStatementCreator psc = new PreparedStatementCreator() {

						@Override
						public PreparedStatement createPreparedStatement(
								Connection connection) throws SQLException {
							PreparedStatement ps = connection
									.prepareStatement(attributeSql.toString());
							ps.setLong(1, layer.getId());
							ps.setLong(2, layerAttribute.getAttribute().getId());
							ps.setString(3, layerAttribute.getAllowedValues());
							ps.setString(4, layerAttribute.getOptionsString());
							ps.setBoolean(5, layerAttribute.isMandatory());
							return ps;
						}
					};
					this.jdbcTemplate.update(psc);
				}
			}
		}
		return;
	}

	public void deactivate(final Long layerId) {
		final String sql = sqlForDatabase("update {{projectdb}}.layer set active = 0 where id = ?");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, layerId);
				return ps;
			}
		};

		this.jdbcTemplate.update(preparedStatementCreator);
	}

	public Layer findActiveLayerByName(String name) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.layer where active = 1 and name = ?");
		List<Layer> layers = this.jdbcTemplate.query(sql,
				new Object[] { name }, rowMapper);
		return CollectionUtils.isNotEmpty(layers) ? layers.get(0) : null;
	}

	public List<Layer> findLayersByPartialName(String name) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.layer where name like '%"
				+ name + "%'");
		List<Layer> layers = this.jdbcTemplate.query(sql, rowMapper);
		return layers;
	}

	public Layer findLayerById(long id) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.layer where id = ?");
		List<Layer> layers = this.jdbcTemplate.query(sql, new Object[] { id },
				rowMapper);
		return CollectionUtils.isNotEmpty(layers) ? layers.get(0) : null;
	}

	public List<Layer> findActiveLayers() {
		final String sql = sqlForDatabase("select * from {{projectdb}}.layer where active = 1");
		List<Layer> layers = this.jdbcTemplate.query(sql, rowMapper);
		return layers;
	}
	
	public List<Layer> findActiveItems(Short type) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.layer where active = 1 && type = ?");
		List<Layer> layers = this.jdbcTemplate.query(sql, new Object[] {type}, rowMapper);
		return layers;
	}

	public JqGridData<Layer> findLayersByPage(int pageNo, int size) {
		int recordNo = (pageNo > 0 ? pageNo - 1 : 0) * size;
		final String sql;
		List<Layer> layers = null;
		if (size <= 0) {
			sql = sqlForDatabase("select * from {{projectdb}}.layer where active = 1");
			layers = this.jdbcTemplate.query(sql, new Object[] {}, rowMapper);
		} else {
			sql = sqlForDatabase("select * from {{projectdb}}.layer where active = 1 limit ?, ?");
			layers = this.jdbcTemplate.query(sql, new Object[] {
					recordNo, size }, rowMapper);
		}
		final String countSql = sqlForDatabase("select count(*) from {{projectdb}}.layer where active = 1 AND type = ?");
		int count = this.jdbcTemplate.queryForInt(countSql);
		int totalPages = 1;
		if (size > 0) {
			totalPages = count / size + (count % size > 0 ? 1 : 0);
		}

		JqGridData<Layer> gridData = new JqGridData<Layer>(totalPages, pageNo,
				count, layers);
		return gridData;
	}
	
	public JqGridData<Layer> findItemsByPageByType(int pageNo, int size, short type) {
		int recordNo = (pageNo > 0 ? pageNo - 1 : 0) * size;
		final String sql;
		List<Layer> layers = null;
		if (size <= 0) {
			sql = sqlForDatabase("select * from {{projectdb}}.layer where active = 1 AND type = ?");
			layers = this.jdbcTemplate.query(sql, new Object[] {
					type}, rowMapper);
		} else {
			sql = sqlForDatabase("select * from {{projectdb}}.layer where active = 1 AND type = ? limit ?, ?");
			layers = this.jdbcTemplate.query(sql, new Object[] {
					type, recordNo, size }, rowMapper);
		}
		final String countSql = sqlForDatabase("select count(*) from {{projectdb}}.layer where active = 1 AND type = "+type);
		int count = this.jdbcTemplate.queryForInt(countSql);
		int totalPages = 1;
		if (size > 0) {
			totalPages = count / size + (count % size > 0 ? 1 : 0);
		}

		JqGridData<Layer> gridData = new JqGridData<Layer>(totalPages, pageNo,
				count, layers);
		return gridData;
	}

	public LayerAttributeConfig findLayerAttributeConfigByValues(
			LayerAttributeConfig layerAttributeConfig) {
		List<LayerAttributeConfig> lacs = null;
		if (StringUtils.isBlank(layerAttributeConfig.getAttributeIds())) {
			String sql = sqlForDatabase("select LAC.id, LAC.layerId, NULL AS attributeIds,  NULL AS valueString from {{projectdb}}.layer_attribute_config LAC where LAC.layerId = ? and LAC.attributeIds IS NULL ");
			lacs = this.jdbcTemplate.query(sql,
					new Object[] { layerAttributeConfig.getLayer().getId()}, lacRowMapper);
		} else {
			String sql = sqlForDatabase("select LAC.id, LAC.layerId, GROUP_CONCAT(LACV.attributeId SEPARATOR ',') AS attributeIds,  GROUP_CONCAT(LACV.value SEPARATOR '{::}') AS valueString from {{projectdb}}.layer_attribute_config LAC inner join {{projectdb}}.layer_attribute_config_values LACV on LAC.id = LACV.layerAttributeConfigId where LAC.layerId = ? and LAC.attributeIds = ? AND ");
			if (MapUtils.isNotEmpty(layerAttributeConfig.getAttributeValueMap())) {
				String extendedSql = "";
				for (Entry<Long, String> entry : layerAttributeConfig
						.getAttributeValueMap().entrySet()) {
					if (!extendedSql.isEmpty()) {
						extendedSql += " OR ";
					}
					extendedSql += "(LACV.attributeId = " + entry.getKey()
							+ " AND LACV.value = \"" + entry.getValue() + "\")";
				}
				extendedSql = "("
						+ extendedSql
						+ ") GROUP by LAC.id, LAC.layerId HAVING COUNT(*) = "+layerAttributeConfig.getAttributeValueMap().size()+" ORDER BY LAC.id, LACV.attributeId";
				if (extendedSql.isEmpty()) {
					extendedSql= " 1 = 1 ";
				}
				sql += extendedSql;
			}
			lacs = this.jdbcTemplate.query(sql,
					new Object[] { layerAttributeConfig.getLayer().getId(),
							layerAttributeConfig.getAttributeIds()}, lacRowMapper);
		}
		return CollectionUtils.isNotEmpty(lacs) ? lacs.get(0) : null;
	}

	public LayerAttributeConfig createLayerAttributeConfigByValues(
			final LayerAttributeConfig layerAttributeConfig) {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = sqlForDatabase("insert into {{projectdb}}.layer_attribute_config (layerId, attributeIds) values (?, ?)");
		final String lacvSql = sqlForDatabase("insert into {{projectdb}}.layer_attribute_config_values (layerAttributeConfigId, attributeId, value) values (?, ?, ?)");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, layerAttributeConfig.getLayer().getId());
				ps.setString(2, layerAttributeConfig.getAttributeIds());
				return ps;
			}
		};

		
		this.jdbcTemplate.update(preparedStatementCreator, holder);
		layerAttributeConfig.setId(holder.getKey().longValue());
		
		if (MapUtils.isNotEmpty(layerAttributeConfig.getAttributeValueMap())) {
			for (final Entry<Long, String> entry : layerAttributeConfig
					.getAttributeValueMap().entrySet()) {
				PreparedStatementCreator psc = new PreparedStatementCreator() {

					@Override
					public PreparedStatement createPreparedStatement(
							Connection connection) throws SQLException {
						PreparedStatement ps = connection
								.prepareStatement(lacvSql.toString());
						ps.setLong(1, layerAttributeConfig.getId());
						ps.setLong(2, entry.getKey());
						ps.setString(3, entry.getValue());
						return ps;
					}

				};
				this.jdbcTemplate.update(psc);
			}
		}
		
		return layerAttributeConfig;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteLayerChart(Long layerAttributeConfigId) {
		String sql = sqlForDatabase("delete from {{projectdb}}.layer_chart_entry where layerAttributeConfigId  = "
				+ layerAttributeConfigId);
		this.jdbcTemplate.update(sql);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveLayerChart(Long layerAttributeConfigId,
			List<LayerChartEntry> layerChartEntries) {
		StringBuffer buffer = new StringBuffer();
		String sql = sqlForDatabase("insert into {{projectdb}}.layer_chart_entry (layerAttributeConfigId, fromLength, toLength, noOfLayers) VALUES ");
		// buffer.append(sql);
		for (LayerChartEntry entry : layerChartEntries) {
			if (buffer.length() > 0) {
				buffer.append(",");
			}
			buffer.append("(").append(layerAttributeConfigId).append(",")
					.append(entry.getFrom()).append(",").append(entry.getTo())
					.append(",").append(entry.getLevel()).append(")");
		}
		this.jdbcTemplate.update(sql + buffer.toString());
	}

	public List<LayerAttributeConfig> findLayerAttributeConfigsByLayerId(
			long layerId) {
		final String sql = sqlForDatabase("select LAC.id, LAC.layerId, GROUP_CONCAT(LACV.attributeId SEPARATOR ',') AS attributeIds,  GROUP_CONCAT(LACV.value SEPARATOR '{::}') AS valueString  from {{projectdb}}.layer_attribute_config LAC inner join {{projectdb}}.layer L ON LAC.layerId = ? AND LAC.layerId = L.id inner join {{projectdb}}.layer_attribute_config_values AS LACV on LAC.id = LACV.layerAttributeConfigId GROUP BY LAC.id ORDER BY LAC.id, LACV.attributeId ");
		List<LayerAttributeConfig> layerAttributeConfigs = this.jdbcTemplate
				.query(sql, new Object[] { layerId }, lacRowMapper);
		return layerAttributeConfigs;
	}
	
	public List<LayerAttributeConfig> findLayerAttributeConfigsByConfigIds(List<Long> layerAttributeConfigIds) {
		final String sql = sqlForDatabase("select LAC.id, LAC.layerId, GROUP_CONCAT(LACV.attributeId SEPARATOR ',') AS attributeIds,  " +
				"GROUP_CONCAT(LACV.value SEPARATOR '{::}') AS valueString  " +
				"from {{projectdb}}.layer_attribute_config LAC " +
				"inner join {{projectdb}}.layer L ON LAC.layerId = L.id " +
				"LEFT join {{projectdb}}.layer_attribute_config_values AS LACV on LAC.id = LACV.layerAttributeConfigId " +
				"WHERE LAC.id in ("+StringUtils.join(layerAttributeConfigIds.toArray(), ",")+") " +
				"GROUP BY LAC.id " +
				"ORDER BY LAC.id, LACV.attributeId ");
		List<LayerAttributeConfig> layerAttributeConfigs = this.jdbcTemplate
				.query(sql, new Object[] { }, lacRowMapper);
		return layerAttributeConfigs;
	}
	

	public List<LayerAttribute> findLayerAttributesByLayerId(long layerId) {
		final String sql = sqlForDatabase("select LA.*, A.id as attribute_id, A.name as attribute_name, A.value as attribute_value from {{projectdb}}.layer_attribute LA inner join {{projectdb}}.attribute A ON LA.layerId = ? AND LA.attributeId = A.id");
		List<LayerAttribute> layerAttributes = this.jdbcTemplate.query(sql,
				new Object[] { layerId }, layerAttributeMapper);
		return layerAttributes;
	}
	
	public List<LayerAttribute> findLayerAttributesByAttributeId(long attributeId) {
		final String sql = sqlForDatabase("select LA.*, A.id as attribute_id, A.name as attribute_name, A.value as attribute_value from {{projectdb}}.layer_attribute LA inner join {{projectdb}}.attribute A ON LA.attributeId = ? AND LA.attributeId = A.id");
		List<LayerAttribute> layerAttributes = this.jdbcTemplate.query(sql,
				new Object[] { attributeId }, layerAttributeMapper);
		return layerAttributes;
	}

	public List<LayerChartEntry> findLayerChartEntriesByConfigIdByFromByTo(
			long layerAttributeConfigId, int from, int to) {
		final String sql = sqlForDatabase("select LCE.* from {{projectdb}}.layer_chart_entry LCE where LCE.layerAttributeConfigId = ? AND ((LCE.fromLength >= ? AND LCE.fromLength < ?) OR (LCE.toLength > ? AND LCE.toLength <= ?))");
		List<LayerChartEntry> entries = this.jdbcTemplate.query(sql,
				new Object[] { layerAttributeConfigId, from, to, from, to },
				layerChartEntryMapper);
		return entries;
	}
	
	public List<LayerChartEntry> findLayerChartEntriesByConfigIdsByFromByTo(List<Long> layerAttributeConfigIds, int from, int to) {
		final String sql = sqlForDatabase("select LCE.* from {{projectdb}}.layer_chart_entry LCE where LCE.layerAttributeConfigId IN ("+StringUtils.join(layerAttributeConfigIds.toArray(), ",")+") AND ((LCE.fromLength >= ? AND LCE.fromLength < ?) OR (LCE.toLength >= ? AND LCE.toLength < ?))");
		List<LayerChartEntry> entries = this.jdbcTemplate.query(sql,
				new Object[] { from, to, from, to },
				layerChartEntryMapper);
		return entries;
	}
}
