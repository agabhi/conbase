package com.derive.conbase.model;

import java.util.List;

public class RecordsGridModel {
	private List<Attribute> layerAttributes;
	private List<RecordTypeAttribute> recordTypeAttributes;
	private List<LayerAttributeConfig> layerAttributeConfigs;
	private List<RecordAttribute> recordAttributes;
	private List<Record> records;
	private Integer currentPage;
	private Long totalRecords;
	
	public List<Attribute> getLayerAttributes() {
		return layerAttributes;
	}
	public void setLayerAttributes(List<Attribute> layerAttributes) {
		this.layerAttributes = layerAttributes;
	}
	public List<RecordTypeAttribute> getRecordTypeAttributes() {
		return recordTypeAttributes;
	}
	public void setRecordTypeAttributes(
			List<RecordTypeAttribute> recordTypeAttributes) {
		this.recordTypeAttributes = recordTypeAttributes;
	}
	public List<LayerAttributeConfig> getLayerAttributeConfigs() {
		return layerAttributeConfigs;
	}
	public void setLayerAttributeConfigs(
			List<LayerAttributeConfig> layerAttributeConfigs) {
		this.layerAttributeConfigs = layerAttributeConfigs;
	}
	public List<RecordAttribute> getRecordAttributes() {
		return recordAttributes;
	}
	public void setRecordAttributes(List<RecordAttribute> recordAttributes) {
		this.recordAttributes = recordAttributes;
	}
	public List<Record> getRecords() {
		return records;
	}
	public void setRecords(List<Record> records) {
		this.records = records;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}
	
}
