package com.derive.conbase.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class RecordType {
	
	private Long id;
	private String name;
	private short type;
	private Serial serial;
	private List<RecordTypeAttribute> customAttributes;
	private List<RecordTypeAttribute> layerAttributes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RecordTypeAttribute> getCustomAttributes() {
		return customAttributes;
	}

	public void setCustomAttributes(List<RecordTypeAttribute> customAttributes) {
		this.customAttributes = customAttributes;
	}

	public List<RecordTypeAttribute> getLayerAttributes() {
		return layerAttributes;
	}

	public void setLayerAttributes(List<RecordTypeAttribute> layerAttributes) {
		this.layerAttributes = layerAttributes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public Serial getSerial() {
		return serial;
	}

	public void setSerial(Serial serial) {
		this.serial = serial;
	}
	
	public Map<Boolean, List<String>> validate() {
		Map<Boolean, List<String>> resultMap = new LinkedHashMap<Boolean, List<String>>();
		List<String> messages = new ArrayList<String>();
		boolean status = true;
		if (StringUtils.isBlank(name)) {
			status = false;
			messages.add("Name cannot be empty");
		}
		resultMap.put(status, messages);
		return resultMap;
	}
	
}
