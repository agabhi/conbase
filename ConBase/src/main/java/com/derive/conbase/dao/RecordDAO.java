package com.derive.conbase.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.derive.conbase.model.LayerAttributeConfig;
import com.derive.conbase.model.Record;
import com.derive.conbase.model.RecordAttribute;
import com.derive.conbase.model.RecordSearchCriteria;
import com.derive.conbase.model.Structure;
import com.derive.conbase.model.RecordSearchCriteria.SearchCondition;
import com.derive.conbase.model.RecordTypeAttribute;
import com.derive.conbase.util.UserUtils;

@Repository
public class RecordDAO extends DynamicDatabaseDAO<Record> {

	@Autowired
	LayerDAO layerDAO;

	private RowMapper<Record> rowMapper = new RowMapper<Record>() {

		public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
			Record record = new Record();
			record.setId(rs.getLong("recordId"));
			if (rs.getLong("structureId") != 0)
			{
				Structure structure = new Structure();
				structure.setId(rs.getLong("structureId"));
				record.setStructure(structure);
			}
			record.setId(rs.getLong("recordId"));
			record.setSerial(rs.getLong("serial"));
			record.setFrom(rs.getLong("fromLength"));
			record.setTo(rs.getLong("toLength"));
			record.setLevel(rs.getInt("layerLevel"));
			LayerAttributeConfig layerAttributeConfig = new LayerAttributeConfig();
			layerAttributeConfig.setId(rs.getLong("layerAttributeConfigId"));
			record.setLayerAttributeConfig(layerAttributeConfig);
			return record;
		}
	};
	
	private RowMapper<RecordAttribute> recordAttributeMapper = new RowMapper<RecordAttribute>() {

		public RecordAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
			RecordAttribute recordAttribute = new RecordAttribute();
			recordAttribute.setRecordId(rs.getLong("recordId"));
			recordAttribute.setRecordTypeAttributeId(rs.getLong("recordTypeAttributeId"));
			recordAttribute.setValue(rs.getString("value"));
			return recordAttribute;
		}
	};

	public void updateRecord(final Record record) {
		if (record.getRecordType().getType() == 1 || record.getRecordType().getType() == 2) {
			LayerAttributeConfig lac = layerDAO
					.findLayerAttributeConfigByValues(record
							.getLayerAttributeConfig());
			if (lac == null) {
				record.setLayerAttributeConfig(layerDAO
						.createLayerAttributeConfigByValues(record
								.getLayerAttributeConfig()));
			} else {
				record.setLayerAttributeConfig(lac);
			}
		}
		final String namedSql = sqlForDatabase("UPDATE {{projectdb}}.record SET layerAttributeConfigId = :layerAttributeConfigId, structureId = :structureId, fromLength = :fromLength, toLength = :toLength, layerLevel = :layerLevel, modifiedOn = :modifiedOn, modifiedBy = :modifiedBy WHERE id = :recordId");
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("layerAttributeConfigId", record.getLayerAttributeConfig() == null ? null : record.getLayerAttributeConfig().getId());
		params.addValue("structureId", record.getStructure() == null ? null : record.getStructure().getId());
		params.addValue("fromLength", record.getFrom());
		params.addValue("toLength", record.getTo());
		params.addValue("layerLevel", record.getLevel());
		params.addValue("modifiedOn", new java.sql.Date(new Date().getTime()));
		params.addValue("modifiedBy", UserUtils.getLoggedInUser().getId());
		params.addValue("recordId", record.getId());
		namedParameterJdbcTemplate.update(namedSql, params);
		
		final String deleteSql = sqlForDatabase("DELETE FROM {{projectdb}}.record_attribute WHERE recordId = ?");
		jdbcTemplate.update(deleteSql, new Object[] {record.getId()});
		
		if (MapUtils.isNotEmpty(record.getAttributeValuesMap())) {
			final String attributeSql = sqlForDatabase("insert into {{projectdb}}.record_attribute (recordId, recordTypeAttributeId, value) values (?, ?, ?)");
			for (final Entry<Long, String> entry : record
					.getAttributeValuesMap().entrySet()) {
				final Long recordTypeAttributeId = entry.getKey();
				if (recordTypeAttributeId != null) {

					PreparedStatementCreator psc = new PreparedStatementCreator() {

						@Override
						public PreparedStatement createPreparedStatement(
								Connection connection) throws SQLException {
							PreparedStatement ps = connection
									.prepareStatement(attributeSql.toString());
							ps.setLong(1, record.getId());
							ps.setLong(2, recordTypeAttributeId);
							ps.setString(3, entry.getValue());
							return ps;
						}
					};
					this.jdbcTemplate.update(psc);
				}
			}
		}
	}
	
	public void addRecord(final Record record) {
		if (record.getRecordType().getType() == 1 || record.getRecordType().getType() == 2) {
			LayerAttributeConfig lac = layerDAO
					.findLayerAttributeConfigByValues(record
							.getLayerAttributeConfig());
			if (lac == null) {
				record.setLayerAttributeConfig(layerDAO
						.createLayerAttributeConfigByValues(record
								.getLayerAttributeConfig()));
			} else {
				record.setLayerAttributeConfig(lac);
			}
		}
		final String serialSql = sqlForDatabase("SELECT value FROM {{projectdb}}.serial WHERE id = ? FOR UPDATE");
		final Long serial = this.jdbcTemplate.queryForLong(serialSql, new Object[] {record.getRecordType().getSerial().getId()});

		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = sqlForDatabase("insert into {{projectdb}}.record (recordTypeId, layerAttributeConfigId, structureId, fromLength, toLength, layerLevel, serial, createdOn, createdBy) values (?, ?, ?, ?, ?, ?, ?, ?)");
		final String namedSql = sqlForDatabase("insert into {{projectdb}}.record (recordTypeId, layerAttributeConfigId, structureId, fromLength, toLength, layerLevel, serial, createdOn, createdBy) values (:recordTypeId, :layerAttributeConfigId, :structureId, :fromLength, :toLength, :layerLevel, :serial, :createdOn, :createdBy)");
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("recordTypeId", record.getRecordType().getId());
		params.addValue("layerAttributeConfigId", record.getLayerAttributeConfig() == null ? null : record.getLayerAttributeConfig().getId());
		params.addValue("structureId", record.getStructure() == null ? null : record.getStructure().getId());
		params.addValue("fromLength", record.getFrom());
		params.addValue("toLength", record.getTo());
		params.addValue("layerLevel", record.getLevel());
		params.addValue("serial", serial);
		params.addValue("createdOn", new java.sql.Date(new Date().getTime()));
		params.addValue("createdBy", UserUtils.getLoggedInUser().getId());
		namedParameterJdbcTemplate.update(namedSql, params, holder);
		    
		//this.jdbcTemplate.update(sql, new Object[]{record.getRecordType().getId(), record.getLayerAttributeConfig().getId(), record.getStructure().getId(), record.getFrom(), record.getTo(), record.getLevel(), serial, new java.sql.Date(new Date().getTime()), 1 }, holder);
		record.setId(holder.getKey().longValue());

		if (MapUtils.isNotEmpty(record.getAttributeValuesMap())) {
			final String attributeSql = sqlForDatabase("insert into {{projectdb}}.record_attribute (recordId, recordTypeAttributeId, value) values (?, ?, ?)");
			for (final Entry<Long, String> entry : record
					.getAttributeValuesMap().entrySet()) {
				final Long recordTypeAttributeId = entry.getKey();
				if (recordTypeAttributeId != null) {

					PreparedStatementCreator psc = new PreparedStatementCreator() {

						@Override
						public PreparedStatement createPreparedStatement(
								Connection connection) throws SQLException {
							PreparedStatement ps = connection
									.prepareStatement(attributeSql.toString());
							ps.setLong(1, record.getId());
							ps.setLong(2, recordTypeAttributeId);
							ps.setString(3, entry.getValue());
							return ps;
						}
					};
					this.jdbcTemplate.update(psc);
				}
			}
		}
		final String serialUpdateSql = sqlForDatabase("update {{projectdb}}.serial set value = "+(serial +1)+ " where id = "+record.getRecordType().getSerial().getId());
		this.jdbcTemplate.update(serialUpdateSql);
	}
	
	public Record findRecordById(long recordId) {
		final String sql = sqlForDatabase("select R.id AS recordId, R.structureId AS structureId, R.serial AS serial, R.fromLength AS fromLength, R.toLength AS toLength, R.layerLevel as layerLevel, " +
				"R.layerAttributeConfigId from {{projectdb}}.record R WHERE R.id ="+recordId);
		Record record = this.jdbcTemplate.queryForObject(sql, rowMapper);
		return record;
	}
	
	public Long findRecordsCount(long recordTypeId, RecordSearchCriteria search) {
		HashMap<String, String> operationMap = new HashMap<String, String>();
		operationMap.put("equal to", " = ");
		operationMap.put("not equal to", " <> ");
		operationMap.put("greater than", " > ");
		operationMap.put("less than", " < ");
		operationMap.put("starts with", " like ");
		operationMap.put("ends with", " like ");
		operationMap.put("contains", " like ");
		String sql = sqlForDatabase("select COUNT(DISTINCT R.id) "+
				"from {{projectdb}}.record R" +
				" LEFT JOIN {{projectdb}}.record_attribute RA ON R.id = RA.recordId" +
				" LEFT JOIN {{projectdb}}.layer_attribute_config LAC ON LAC.id = R.layerAttributeConfigId" +
				" LEFT JOIN {{projectdb}}.layer_attribute_config_values LACV ON LAC.id = LACV.layerAttributeConfigId" +
				" WHERE recordTypeId = ? ");
		StringBuffer whereConditions =  new StringBuffer();
		if (search != null) {
			if (CollectionUtils.isNotEmpty(search.getLayerIds())) {
				List<SearchCondition> searchConditions = search.getLayerIds();
				for (SearchCondition searchCondition : searchConditions) {
					whereConditions.append(" AND LAC.layerId ").append(operationMap.get(searchCondition.getOperation())).append(" ").append(searchCondition.getValue());
				}
			}
			if (CollectionUtils.isNotEmpty(search.getStructureItemIds())) {
				List<SearchCondition> searchConditions = search.getStructureItemIds();
				for (SearchCondition searchCondition : searchConditions) {
					whereConditions.append(" AND LAC.layerId ").append(operationMap.get(searchCondition.getOperation())).append(" ").append(searchCondition.getValue());
				}
			}
			if (CollectionUtils.isNotEmpty(search.getStructureIds())) {
				List<SearchCondition> searchConditions = search.getStructureIds();
				for (SearchCondition searchCondition : searchConditions) {
					whereConditions.append(" AND R.structureId ").append(operationMap.get(searchCondition.getOperation())).append(" ").append(searchCondition.getValue());
				}
			}
			if (CollectionUtils.isNotEmpty(search.getSerials())) {
				List<SearchCondition> searchConditions = search.getSerials();
				for (SearchCondition searchCondition : searchConditions) {
					if (!StringUtils.isNumeric(searchCondition.getValue().trim())) {
						return null;
					}
					whereConditions.append(" AND R.serial ").append(operationMap.get(searchCondition.getOperation())).append(" ").append(searchCondition.getValue());
				}
			}
			if (CollectionUtils.isNotEmpty(search.getAttributes())) {
				List<SearchCondition> searchConditions = search.getAttributes();
				for (SearchCondition searchCondition : searchConditions) {
					if (searchCondition.getOperation().equals("starts with")) {
						whereConditions.append(" AND LACV.attributeId = "+searchCondition.getId()).append(" AND LACV.value LIKE '%"+searchCondition.getValue()+"'");
					} else if (searchCondition.getOperation().equals("ends with")) {
						whereConditions.append(" AND LACV.attributeId = "+searchCondition.getId()).append(" AND LACV.value LIKE '"+searchCondition.getValue()+"%'");
					} else if (searchCondition.getOperation().equals("contains")) {
						whereConditions.append(" AND LACV.attributeId = "+searchCondition.getId()).append(" AND LACV.value LIKE '%"+searchCondition.getValue()+"%'");
					} else {
						whereConditions.append(" AND LACV.attributeId = "+searchCondition.getId()).append(" AND LACV.value ").append(operationMap.get(searchCondition.getOperation())).append(" '"+searchCondition.getValue()+"'");
					}
				}
			}
			if (CollectionUtils.isNotEmpty(search.getRecordTypeCustomAttributes())) {
				List<SearchCondition> searchConditions = search.getRecordTypeCustomAttributes();
				for (SearchCondition searchCondition : searchConditions) {
					if (searchCondition.getOperation().equals("starts with")) {
						whereConditions.append(" AND RA.recordTypeAttributeId = "+searchCondition.getId()).append(" AND RA.value LIKE '%"+searchCondition.getValue()+"'");
					} else if (searchCondition.getOperation().equals("ends with")) {
						whereConditions.append(" AND RA.recordTypeAttributeId = "+searchCondition.getId()).append(" AND RA.value LIKE '"+searchCondition.getValue()+"%'");
					} else if (searchCondition.getOperation().equals("contains")) {
						whereConditions.append(" AND RA.recordTypeAttributeId = "+searchCondition.getId()).append(" AND RA.value LIKE '%"+searchCondition.getValue()+"%'");
					} else {
						whereConditions.append(" AND RA.recordTypeAttributeId = "+searchCondition.getId()).append(" AND RA.value ").append(operationMap.get(searchCondition.getOperation())).append(" '"+searchCondition.getValue()+"'");
					}
				}
			}
			if (CollectionUtils.isNotEmpty(search.getRecordTypeLayerAttributes())) {
				List<SearchCondition> searchConditions = search.getRecordTypeLayerAttributes();
				for (SearchCondition searchCondition : searchConditions) {
					if (searchCondition.getId().equals(new Long("6"))) {
						if (StringUtils.isNumeric(searchCondition.getValue().trim())) {
							whereConditions.append(" AND R.fromLength ").append(operationMap.get(searchCondition.getOperation())).append(" '"+searchCondition.getValue()+"'");
						} else {
							return null;
						}
					}
					if (searchCondition.getId().equals(new Long("7"))) {
						if (StringUtils.isNumeric(searchCondition.getValue().trim())) {
							whereConditions.append(" AND R.toLength ").append(operationMap.get(searchCondition.getOperation())).append(" '"+searchCondition.getValue()+"'");
						} else {
							return null;
						}
					}
					if (searchCondition.getId().equals(new Long("8"))) {
						if (StringUtils.isNumeric(searchCondition.getValue().trim())) {
							whereConditions.append(" AND R.layerLevel ").append(operationMap.get(searchCondition.getOperation())).append(" '"+searchCondition.getValue()+"'");
						} else {
							return null;
						}
					}
				}
			}
		}
		
		sql = sql + whereConditions.toString();
		Long count = this.jdbcTemplate.queryForLong(sql, new Object[] {recordTypeId});
		return count;
	}
	public List<Record> findRecordsByPage(long recordTypeId, int pageNo, int size, RecordSearchCriteria search) {
		HashMap<String, String> operationMap = new HashMap<String, String>();
		operationMap.put("equal to", " = ");
		operationMap.put("not equal to", " <> ");
		operationMap.put("greater than", " > ");
		operationMap.put("less than", " < ");
		operationMap.put("starts with", " like ");
		operationMap.put("ends with", " like ");
		operationMap.put("contains", " like ");
		int recordNo = (pageNo > 0 ? pageNo - 1 : 0) * size;
		if (size <= 0) {
			size = 10;
		}
		String sql = sqlForDatabase("select DISTINCT R.id AS recordId, R.structureId, R.serial AS serial, R.fromLength AS fromLength, R.toLength AS toLength, R.layerLevel as layerLevel, " +
				"R.layerAttributeConfigId "+
				"from {{projectdb}}.record R" +
				" LEFT JOIN {{projectdb}}.record_attribute RA ON R.id = RA.recordId" +
				" LEFT JOIN {{projectdb}}.layer_attribute_config LAC ON LAC.id = R.layerAttributeConfigId" +
				" LEFT JOIN {{projectdb}}.layer_attribute_config_values LACV ON LAC.id = LACV.layerAttributeConfigId" +
				" WHERE recordTypeId = ? ");
		StringBuffer whereConditions =  new StringBuffer();
		if (search != null) {
			if (CollectionUtils.isNotEmpty(search.getLayerIds())) {
				List<SearchCondition> searchConditions = search.getLayerIds();
				for (SearchCondition searchCondition : searchConditions) {
					whereConditions.append(" AND LAC.layerId ").append(operationMap.get(searchCondition.getOperation())).append(" ").append(searchCondition.getValue());
				}
			}
			if (CollectionUtils.isNotEmpty(search.getStructureItemIds())) {
				List<SearchCondition> searchConditions = search.getStructureItemIds();
				for (SearchCondition searchCondition : searchConditions) {
					whereConditions.append(" AND LAC.layerId ").append(operationMap.get(searchCondition.getOperation())).append(" ").append(searchCondition.getValue());
				}
			}
			if (CollectionUtils.isNotEmpty(search.getStructureIds())) {
				List<SearchCondition> searchConditions = search.getStructureIds();
				for (SearchCondition searchCondition : searchConditions) {
					whereConditions.append(" AND R.structureId ").append(operationMap.get(searchCondition.getOperation())).append(" ").append(searchCondition.getValue());
				}
			}
			if (CollectionUtils.isNotEmpty(search.getSerials())) {
				List<SearchCondition> searchConditions = search.getSerials();
				for (SearchCondition searchCondition : searchConditions) {
					if (!StringUtils.isNumeric(searchCondition.getValue().trim())) {
						return null;
					}
					whereConditions.append(" AND R.serial ").append(operationMap.get(searchCondition.getOperation())).append(" ").append(searchCondition.getValue());
				}
			}
			if (CollectionUtils.isNotEmpty(search.getAttributes())) {
				List<SearchCondition> searchConditions = search.getAttributes();
				for (SearchCondition searchCondition : searchConditions) {
					if (searchCondition.getOperation().equals("starts with")) {
						whereConditions.append(" AND LACV.attributeId = "+searchCondition.getId()).append(" AND LACV.value LIKE '%"+searchCondition.getValue()+"'");
					} else if (searchCondition.getOperation().equals("ends with")) {
						whereConditions.append(" AND LACV.attributeId = "+searchCondition.getId()).append(" AND LACV.value LIKE '"+searchCondition.getValue()+"%'");
					} else if (searchCondition.getOperation().equals("contains")) {
						whereConditions.append(" AND LACV.attributeId = "+searchCondition.getId()).append(" AND LACV.value LIKE '%"+searchCondition.getValue()+"%'");
					} else {
						whereConditions.append(" AND LACV.attributeId = "+searchCondition.getId()).append(" AND LACV.value ").append(operationMap.get(searchCondition.getOperation())).append(" '"+searchCondition.getValue()+"'");
					}
				}
			}
			if (CollectionUtils.isNotEmpty(search.getRecordTypeCustomAttributes())) {
				List<SearchCondition> searchConditions = search.getRecordTypeCustomAttributes();
				for (SearchCondition searchCondition : searchConditions) {
					if (searchCondition.getOperation().equals("starts with")) {
						whereConditions.append(" AND RA.recordTypeAttributeId = "+searchCondition.getId()).append(" AND RA.value LIKE '%"+searchCondition.getValue()+"'");
					} else if (searchCondition.getOperation().equals("ends with")) {
						whereConditions.append(" AND RA.recordTypeAttributeId = "+searchCondition.getId()).append(" AND RA.value LIKE '"+searchCondition.getValue()+"%'");
					} else if (searchCondition.getOperation().equals("contains")) {
						whereConditions.append(" AND RA.recordTypeAttributeId = "+searchCondition.getId()).append(" AND RA.value LIKE '%"+searchCondition.getValue()+"%'");
					} else {
						whereConditions.append(" AND RA.recordTypeAttributeId = "+searchCondition.getId()).append(" AND RA.value ").append(operationMap.get(searchCondition.getOperation())).append(" '"+searchCondition.getValue()+"'");
					}
				}
			}
			if (CollectionUtils.isNotEmpty(search.getRecordTypeLayerAttributes())) {
				List<SearchCondition> searchConditions = search.getRecordTypeLayerAttributes();
				for (SearchCondition searchCondition : searchConditions) {
					if (searchCondition.getId().equals(new Long("6"))) {
						if (StringUtils.isNumeric(searchCondition.getValue().trim())) {
							whereConditions.append(" AND R.fromLength ").append(operationMap.get(searchCondition.getOperation())).append(" '"+searchCondition.getValue()+"'");
						} else {
							return null;
						}
					}
					if (searchCondition.getId().equals(new Long("7"))) {
						if (StringUtils.isNumeric(searchCondition.getValue().trim())) {
							whereConditions.append(" AND R.toLength ").append(operationMap.get(searchCondition.getOperation())).append(" '"+searchCondition.getValue()+"'");
						} else {
							return null;
						}
					}
					if (searchCondition.getId().equals(new Long("8"))) {
						if (StringUtils.isNumeric(searchCondition.getValue().trim())) {
							whereConditions.append(" AND R.layerLevel ").append(operationMap.get(searchCondition.getOperation())).append(" '"+searchCondition.getValue()+"'");
						} else {
							return null;
						}
					}
				}
			}
		}
		
		sql = sql + whereConditions.toString() + " ORDER BY R.id " + " limit ?, ?";
		List<Record> records = this.jdbcTemplate.query(sql, new Object[] {recordTypeId,
				recordNo, size }, rowMapper);
		return records;
	}
	
	public List<RecordAttribute> findRecordAttributesByRecordIds(List<Long> recordIds) {
		List<RecordAttribute> recordAttributes = null;
		if (CollectionUtils.isNotEmpty(recordIds)) {
			final String recordAttributeSql = sqlForDatabase("select RA.recordId AS recordId, RA.recordTypeAttributeId, RA.value from {{projectdb}}.record_attribute AS RA WHERE RA.recordId IN ("+StringUtils.join(recordIds.toArray(), ",")+") ");
			recordAttributes = this.jdbcTemplate.query(recordAttributeSql, new Object[] {}, recordAttributeMapper);
		}
		return recordAttributes;
	}
	
	public List<RecordAttribute> findRecordAttributesByRecordIdsByRecordTypeAttributeId(List<Long> recordIds, Long recordTypeAttributeId) {
		List<RecordAttribute> recordAttributes = null;
		if (CollectionUtils.isNotEmpty(recordIds)) {
			final String recordAttributeSql = sqlForDatabase("select RA.recordId AS recordId, RA.recordTypeAttributeId, RA.value from {{projectdb}}.record_attribute AS RA WHERE RA.recordTypeAttributeId = ? AND RA.recordId IN ("+StringUtils.join(recordIds.toArray(), ",")+") ");
			recordAttributes = this.jdbcTemplate.query(recordAttributeSql, new Object[] {recordTypeAttributeId}, recordAttributeMapper);
		}
		return recordAttributes;
	}
	
	public List<Record> findRecordsByRecordTypeIdByConfigIdsByFromByTo(Long recordTypeId, List<Long> layerAttributeConfigIds, int from, int to) {
		final String sql = sqlForDatabase("select R.id AS recordId, R.structureId AS structureId, R.serial AS serial, R.fromLength AS fromLength, R.toLength AS toLength, R.layerLevel as layerLevel, " +
				"R.layerAttributeConfigId "+
				"from {{projectdb}}.record R WHERE R.recordTypeId = ? AND R.layerAttributeConfigId in ("+StringUtils.join(layerAttributeConfigIds.toArray(), ",")+") AND ((R.fromLength >= ? AND R.fromLength < ?) OR (R.toLength > ? AND R.toLength <= ?)) ");
		List<Record> records = this.jdbcTemplate.query(sql,
				new Object[] { recordTypeId, from, to, from, to },
				rowMapper);
		return records;
	}
}