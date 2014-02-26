package com.derive.conbase.model;

import java.util.Date;
import java.util.List;

public class ViewRecordModel {
	private long recordId;
	private Long from;
	private Long to;
	private Integer level;
	private String modifiedBy;
	private String createdBy;
	private Date createdOn;
	private Date modifiedOn;
	
	private List<String> recordValues;
	
	private String layerName;
	private List<String> layerValues;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
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
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
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
	public List<String> getRecordValues() {
		return recordValues;
	}
	public void setRecordValues(List<String> recordValues) {
		this.recordValues = recordValues;
	}
	public String getLayerName() {
		return layerName;
	}
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	public List<String> getLayerValues() {
		return layerValues;
	}
	public void setLayerValues(List<String> layerValues) {
		this.layerValues = layerValues;
	}
	
	
}
