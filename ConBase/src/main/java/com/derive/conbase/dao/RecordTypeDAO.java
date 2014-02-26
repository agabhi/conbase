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

import com.derive.conbase.model.JqGridData;
import com.derive.conbase.model.Layer;
import com.derive.conbase.model.LayerAttribute;
import com.derive.conbase.model.RecordType;
import com.derive.conbase.model.RecordTypeAttribute;
import com.derive.conbase.model.Serial;
import com.derive.conbase.util.UserUtils;

@Repository
public class RecordTypeDAO extends DynamicDatabaseDAO<RecordType> {
	
	@Autowired
	private SerialDAO serialDAO;
	
	private RowMapper<RecordType> rowMapper = new RowMapper<RecordType>() {

		public RecordType mapRow(ResultSet rs, int rowNum) throws SQLException {
			RecordType recordType = new RecordType();
			recordType.setId(rs.getLong("id"));
			recordType.setName(rs.getString("name"));
			recordType.setType(rs.getShort("type"));
			Serial serial = new Serial();
			serial.setId(rs.getLong("serialId"));
			recordType.setSerial(serial);
			return recordType;
		}
	};
	
	private RowMapper<RecordTypeAttribute> recordTypeAttributeMapper = new RowMapper<RecordTypeAttribute>() {

		public RecordTypeAttribute mapRow(ResultSet rs, int rowNum) throws SQLException {
			RecordTypeAttribute recordTypeAttribute = new RecordTypeAttribute();
			recordTypeAttribute.setId(rs.getLong("id"));
			recordTypeAttribute.setName(rs.getString("name"));
			recordTypeAttribute.setType(rs.getInt("type"));
			recordTypeAttribute.setOptionsFromOptionsString(rs.getString("optionsString"));
			recordTypeAttribute.setValidationsFromValidationsString(rs.getString("validationsString"));
			return recordTypeAttribute;
		}
	};
	
	public RecordType findRecordTypeByName(String name) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.record_type where name = ?");
		List<RecordType> recordTypes = this.jdbcTemplate.query(sql,
				new Object[] { name }, rowMapper);
		return CollectionUtils.isNotEmpty(recordTypes) ? recordTypes.get(0) : null;
	}
	
	public RecordType findRecordTypeById(Long id) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.record_type where id = ?");
		List<RecordType> recordTypes = this.jdbcTemplate.query(sql,
				new Object[] { id }, rowMapper);
		return CollectionUtils.isNotEmpty(recordTypes) ? recordTypes.get(0) : null;
	}
	
	public RecordTypeAttribute findRecordTypeAttributeById(Long id) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.record_type_attribute where id = ?");
		List<RecordTypeAttribute> recordTypeAttributes = this.jdbcTemplate.query(sql,
				new Object[] { id }, recordTypeAttributeMapper);
		return CollectionUtils.isNotEmpty(recordTypeAttributes) ? recordTypeAttributes.get(0) : null;
	}
	
	public void updateRecordType(final RecordType recordType) {
		final String sql = sqlForDatabase("update {{projectdb}}.record_type set name = ?, type = ?, serialId = ? , modifiedOn = ?, modifiedBy = ? WHERE id = ?");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, recordType.getName());
				ps.setShort(2, recordType.getType());
				ps.setLong(3, recordType.getSerial().getId());
				ps.setDate(4, new java.sql.Date(new Date().getTime()));
				ps.setLong(5, UserUtils.getLoggedInUser().getId());
				ps.setLong(6, recordType.getId());
				return ps;
			}
		};
		
		this.jdbcTemplate.update(preparedStatementCreator);
		
		RecordType existingRecordType = findRecordTypeById(recordType.getId());
		existingRecordType.setCustomAttributes(findCustomAttributesByRecordTypeId(recordType.getId()));
		existingRecordType.setLayerAttributes(findLayerAttributesByRecordTypeId(recordType.getId()));
		
		List<RecordTypeAttribute> notFoundLayerAttributes = existingRecordType.getLayerAttributes();
		final String attributeSql = sqlForDatabase("insert into {{projectdb}}.record_type_attribute (recordTypeId, name, type, optionsString, validationsString) values (?, ?, ?, ?, ?)");
		final String updateAttributeSql = sqlForDatabase("update {{projectdb}}.record_type_attribute set name = ?, type = ?, optionsString = ?,validationsString = ? WHERE id = ? AND recordTypeId = ?");
		
		if (CollectionUtils.isNotEmpty(recordType.getLayerAttributes())) {
			for (final RecordTypeAttribute layerAttribute : recordType.getLayerAttributes()) {
				
				if (layerAttribute.getId() == null) {
					//if (existingRecordType.getLayerAttributes() == null || !existingRecordType.getLayerAttributes().contains(layerAttribute)) {
						PreparedStatementCreator psc = new PreparedStatementCreator() {
	
							@Override
							public PreparedStatement createPreparedStatement(
									Connection connection) throws SQLException {
								PreparedStatement ps = connection
										.prepareStatement(attributeSql.toString());
								ps.setLong(1, recordType.getId());
								ps.setString(2, layerAttribute.getName());
								ps.setInt(3, layerAttribute.getType());
								ps.setString(4, layerAttribute.getOptionsString());
								ps.setString(5, layerAttribute.getValidationsString());
								return ps;
							}
						};
						this.jdbcTemplate.update(psc);
					//} else {
						
					//}
	
				} else {
					PreparedStatementCreator psc = new PreparedStatementCreator() {
						
						@Override
						public PreparedStatement createPreparedStatement(
								Connection connection) throws SQLException {
							PreparedStatement ps = connection
									.prepareStatement(updateAttributeSql);
							ps.setString(1, layerAttribute.getName());
							ps.setInt(2, layerAttribute.getType());
							ps.setString(3, layerAttribute.getOptionsString());
							ps.setString(4, layerAttribute.getValidationsString());
							ps.setLong(5, layerAttribute.getId());
							ps.setLong(6, recordType.getId());
							return ps;
						}
					};
					this.jdbcTemplate.update(psc);
				}
				if (CollectionUtils.isNotEmpty(notFoundLayerAttributes)) {
					notFoundLayerAttributes.remove(layerAttribute);
				}
			}
		}
		
		List<RecordTypeAttribute> notFoundCustomAttributes = existingRecordType.getCustomAttributes();
		if (CollectionUtils.isNotEmpty(recordType.getCustomAttributes())) {
			for (final RecordTypeAttribute customAttribute : recordType.getCustomAttributes()) {
				
				if (customAttribute.getId() == null) {
					if (existingRecordType.getCustomAttributes() == null || !existingRecordType.getCustomAttributes().contains(customAttribute)) {
						PreparedStatementCreator psc = new PreparedStatementCreator() {
	
							@Override
							public PreparedStatement createPreparedStatement(
									Connection connection) throws SQLException {
								PreparedStatement ps = connection
										.prepareStatement(attributeSql.toString());
								ps.setLong(1, recordType.getId());
								ps.setString(2, customAttribute.getName());
								ps.setInt(3, customAttribute.getType());
								ps.setString(4, customAttribute.getOptionsString());
								ps.setString(5, customAttribute.getValidationsString());
								return ps;
							}
						};
						this.jdbcTemplate.update(psc);
					} //else {
					//}
	
				} else {
					PreparedStatementCreator psc = new PreparedStatementCreator() {
						
						@Override
						public PreparedStatement createPreparedStatement(
								Connection connection) throws SQLException {
							PreparedStatement ps = connection
									.prepareStatement(updateAttributeSql);
							ps.setString(1, customAttribute.getName());
							ps.setInt(2, customAttribute.getType());
							ps.setString(3, customAttribute.getOptionsString());
							ps.setString(4, customAttribute.getValidationsString());
							ps.setLong(5, customAttribute.getId());
							ps.setLong(6, recordType.getId());
							return ps;
						}
					};
					this.jdbcTemplate.update(psc);

				}
				if (CollectionUtils.isNotEmpty(notFoundCustomAttributes)) {
					notFoundCustomAttributes.remove(customAttribute);
				}
			}
		}
		
		final String deleteSql = sqlForDatabase("DELETE FROM {{projectdb}}.record_type_attribute where id = ?");
		if (CollectionUtils.isNotEmpty(notFoundCustomAttributes)) {
			for (RecordTypeAttribute recordTypeAttribute : notFoundCustomAttributes) {
				jdbcTemplate.update(deleteSql, new Object[] {recordTypeAttribute.getId()});
			}
			
		}

	}
	public void save(final RecordType recordType) {
		KeyHolder holder = new GeneratedKeyHolder();
		final String sql = sqlForDatabase("insert into {{projectdb}}.record_type (name, type, serialId, createdOn, createdBy) values (?, ?, ?, ?, ?)");
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, recordType.getName());
				ps.setShort(2, recordType.getType());
				ps.setLong(3, recordType.getSerial().getId());
				ps.setDate(4, new java.sql.Date(new Date().getTime()));
				ps.setLong(5, UserUtils.getLoggedInUser().getId());
				return ps;
			}
		};
		
		this.jdbcTemplate.update(preparedStatementCreator, holder);
		recordType.setId(holder.getKey().longValue());
		
		final String recordTypeAttributeSql = sqlForDatabase("insert into {{projectdb}}.record_type_attribute (recordTypeId, name, type, optionsString, validationsString) values (?, ?, ?, ?, ?)");
		for (final RecordTypeAttribute recordTypeAttribute : recordType.getCustomAttributes()) {
			if (recordTypeAttribute.getId() == null) {
				PreparedStatementCreator psc = new PreparedStatementCreator() {

					@Override
					public PreparedStatement createPreparedStatement(
							Connection connection) throws SQLException {
						PreparedStatement ps = connection
								.prepareStatement(recordTypeAttributeSql.toString());
						ps.setLong(1, recordType.getId());
						ps.setString(2, recordTypeAttribute.getName());
						ps.setInt(3, recordTypeAttribute.getType());
						ps.setString(4, recordTypeAttribute.getOptionsString());
						ps.setString(5, recordTypeAttribute.getValidationsString());
						return ps;
					}
				};
				this.jdbcTemplate.update(psc);
			}
		}
		for (final RecordTypeAttribute recordTypeAttribute : recordType.getLayerAttributes()) {
			if (recordTypeAttribute.getId() == null) {
				PreparedStatementCreator psc = new PreparedStatementCreator() {

					@Override
					public PreparedStatement createPreparedStatement(
							Connection connection) throws SQLException {
						PreparedStatement ps = connection
								.prepareStatement(recordTypeAttributeSql.toString());
						ps.setLong(1, recordType.getId());
						ps.setString(2, recordTypeAttribute.getName());
						ps.setInt(3, recordTypeAttribute.getType());
						ps.setString(4, recordTypeAttribute.getOptionsString());
						ps.setString(5, recordTypeAttribute.getValidationsString());
						return ps;
					}
				};
				this.jdbcTemplate.update(psc);
			}
		}
	}
	
	public JqGridData<RecordType> findRecordTypesByPage(int pageNo, int size) {
		int recordNo = (pageNo > 0 ? pageNo - 1 : 0) * size;
		final String sql;
		List<RecordType> recordTypes = null;
		if (size <= 0) {
			sql = sqlForDatabase("select * from {{projectdb}}.record_type ");
			recordTypes = this.jdbcTemplate.query(sql, rowMapper);
		} else {
			sql = sqlForDatabase("select * from {{projectdb}}.record_type limit ?, ?");
			recordTypes = this.jdbcTemplate.query(sql, new Object[] {
					recordNo, size }, rowMapper);
		}
		
		final String countSql = sqlForDatabase("select count(*) from {{projectdb}}.record_type");
		int count = this.jdbcTemplate.queryForInt(countSql);
		int totalPages = 1;
		if (size > 0) {
			totalPages = count / size + (count % size > 0 ? 1 : 0);
		}
		
		JqGridData<RecordType> gridData = new JqGridData<RecordType>(totalPages, pageNo,
				count, recordTypes);
		return gridData;
	}
	
	public List<RecordTypeAttribute> findCustomAttributesByRecordTypeId(long recordTypeId) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.record_type_attribute where recordTypeId = ? AND type NOT IN (6,7,8)");
		List<RecordTypeAttribute> customAttributes = this.jdbcTemplate.query(sql, new Object[] {recordTypeId}, recordTypeAttributeMapper);
		return customAttributes;
	}
	
	public List<RecordTypeAttribute> findLayerAttributesByRecordTypeId(long recordTypeId) {
		final String sql = sqlForDatabase("select * from {{projectdb}}.record_type_attribute where recordTypeId = ? AND type IN (6,7,8)");
		List<RecordTypeAttribute> customAttributes = this.jdbcTemplate.query(sql, new Object[] {recordTypeId}, recordTypeAttributeMapper);
		return customAttributes;
	}
}
