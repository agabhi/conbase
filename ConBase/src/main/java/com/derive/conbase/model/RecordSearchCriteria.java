package com.derive.conbase.model;

import java.util.ArrayList;
import java.util.List;

public class RecordSearchCriteria {
	private List<SearchCondition> attributes;
	private List<SearchCondition> recordTypeLayerAttributes;
	private List<SearchCondition> recordTypeCustomAttributes;
	private List<SearchCondition> serials;
	private List<SearchCondition> layerIds;
	private List<SearchCondition> structureIds;
	private List<SearchCondition> structureItemIds;
	
	public List<SearchCondition> getLayerIds() {
		return layerIds;
	}
	public List<SearchCondition> getSerials() {
		return serials;
	}
	public List<SearchCondition> getStructureIds() {
		return structureIds;
	}
	public List<SearchCondition> getStructureItemIds() {
		return structureItemIds;
	}
	public void addSerial(String operation, String value) {
		if (serials == null) {
			serials = new ArrayList<RecordSearchCriteria.SearchCondition>();
		}
		serials.add(new SearchCondition(null, operation, value));
	}
	public void addLayerId(String operation, String value) {
		if (layerIds == null) {
			layerIds = new ArrayList<RecordSearchCriteria.SearchCondition>();
		}
		layerIds.add(new SearchCondition(null, operation, value));
	}
	public void addStructureId(String operation, String value) {
		if (structureIds == null) {
			structureIds = new ArrayList<RecordSearchCriteria.SearchCondition>();
		}
		structureIds.add(new SearchCondition(null, operation, value));
	}
	public void addStructureItemId(String operation, String value) {
		if (structureItemIds == null) {
			structureItemIds = new ArrayList<RecordSearchCriteria.SearchCondition>();
		}
		structureItemIds.add(new SearchCondition(null, operation, value));
	}
	public List<SearchCondition> getAttributes() {
		return attributes;
	}
	public List<SearchCondition> getRecordTypeLayerAttributes() {
		return recordTypeLayerAttributes;
	}
	public List<SearchCondition> getRecordTypeCustomAttributes() {
		return recordTypeCustomAttributes;
	}
	public void addAttribute(Long attributeId, String operation, String value) {
		if (attributes == null) {
			attributes = new ArrayList<RecordSearchCriteria.SearchCondition>();
		}
		attributes.add(new SearchCondition(attributeId, operation, value));
	}
	
	public void addRecordTypeLayerAttribute(Long attributeTypeId, String operation, String value) {
		if (recordTypeLayerAttributes == null) {
			recordTypeLayerAttributes = new ArrayList<RecordSearchCriteria.SearchCondition>();
		}
		recordTypeLayerAttributes.add(new SearchCondition(attributeTypeId, operation, value));
	}
	
	public void addRecordTypeCustomAttribute(Long attributeId, String operation, String value) {
		if (recordTypeCustomAttributes == null) {
			recordTypeCustomAttributes = new ArrayList<RecordSearchCriteria.SearchCondition>();
		}
		recordTypeCustomAttributes.add(new SearchCondition(attributeId, operation, value));
	}
	
	public class SearchCondition {
		private Long id;
		private String operation;
		private String value;
		
		
		public SearchCondition(Long id, String operation, String value) {
			super();
			this.id = id;
			this.operation = operation;
			this.value = value;
		}
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getOperation() {
			return operation;
		}
		public void setOperation(String operation) {
			this.operation = operation;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
		
	}
	
}
