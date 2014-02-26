package com.derive.conbase.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Record {
	private Long id;
	private RecordType recordType;
	private LayerAttributeConfig layerAttributeConfig;
	private Structure structure;
	private Long from;
	private Long to;
	private Integer level;
	private User modifiedBy;
	private User createdBy;
	private Date createdOn;
	private Date modifiedOn;
	private Long serial;
	private Map<Long, String> attributeValuesMap;
	private List<RecordAttribute> recordAttributes;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public RecordType getRecordType() {
		return recordType;
	}
	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}
	public LayerAttributeConfig getLayerAttributeConfig() {
		return layerAttributeConfig;
	}
	public void setLayerAttributeConfig(LayerAttributeConfig layerAttributeConfig) {
		this.layerAttributeConfig = layerAttributeConfig;
	}
	public Long getFrom() {
		return from;
	}
	public void setFrom(Long from) {
		this.from = from;
	}
	public Long getTo() {
		return to;
	}
	public void setTo(Long to) {
		this.to = to;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public User getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Date getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public Map<Long, String> getAttributeValuesMap() {
		return attributeValuesMap;
	}
	public void setAttributeValuesMap(
			Map<Long, String> attributeValuesMap) {
		this.attributeValuesMap = attributeValuesMap;
	}
	public Long getSerial() {
		return serial;
	}
	public void setSerial(Long serial) {
		this.serial = serial;
	}
	public Structure getStructure() {
		return structure;
	}
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	public List<RecordAttribute> getRecordAttributes() {
		return recordAttributes;
	}
	public void setRecordAttributes(List<RecordAttribute> recordAttributes) {
		this.recordAttributes = recordAttributes;
	}
	
	
	
}
