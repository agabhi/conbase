package com.derive.conbase.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class Barchart {
	
	private Long id;
	private String name;
	private RecordType recordType;
	private List<LayerAttributeConfig> layerAttributeConfigs;
	private RecordTypeAttribute recordTypeAttribute;
	private User user;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public RecordType getRecordType() {
		return recordType;
	}
	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}
	public List<LayerAttributeConfig> getLayerAttributeConfigs() {
		return layerAttributeConfigs;
	}
	public void setLayerAttributeConfigs(
			List<LayerAttributeConfig> layerAttributeConfigs) {
		this.layerAttributeConfigs = layerAttributeConfigs;
	}
	public RecordTypeAttribute getRecordTypeAttribute() {
		return recordTypeAttribute;
	}
	public void setRecordTypeAttribute(RecordTypeAttribute recordTypeAttribute) {
		this.recordTypeAttribute = recordTypeAttribute;
	}
	
	public Map<Boolean, List<String>> validate() {
		Map<Boolean, List<String>> resultMap = new LinkedHashMap<Boolean, List<String>>();
		List<String> messages = new ArrayList<String>();
		boolean status = true;
		if (StringUtils.isBlank(name)) {
			status = false;
			messages.add("Name cannot be empty");
		}
		if (recordType == null || recordType.getId() == null) {
			status = false;
			messages.add("No record type found.");
		}
		if (recordTypeAttribute == null || recordTypeAttribute.getId() == null) {
			status = false;
			messages.add("No attribute found to create the barchart.");
		}
		if (CollectionUtils.isEmpty(layerAttributeConfigs)) {
			status = false;
			messages.add("At least one layer should be present in barchart.");
		}
		resultMap.put(status, messages);
		return resultMap;
	}
	
}
