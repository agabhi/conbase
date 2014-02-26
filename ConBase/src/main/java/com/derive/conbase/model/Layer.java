package com.derive.conbase.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class Layer {
	private Long id;
	private String name;
	private boolean active;
	private List<LayerAttribute> layerAttributes;
	private List<LayerAttributeConfig> layerAttributeConfigs;
	private Short type;
	
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
	
	public List<LayerAttribute> getLayerAttributes() {
		return layerAttributes;
	}
	public void setLayerAttributes(List<LayerAttribute> layerAttributes) {
		this.layerAttributes = layerAttributes;
	}
	public List<LayerAttributeConfig> getLayerAttributeConfigs() {
		return layerAttributeConfigs;
	}
	public void setLayerAttributeConfigs(
			List<LayerAttributeConfig> layerAttributeConfigs) {
		this.layerAttributeConfigs = layerAttributeConfigs;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Short getType() {
		return type;
	}
	public void setType(Short type) {
		this.type = type;
	}
	
	public Map<Boolean, List<String>> validate() {
		Map<Boolean, List<String>> resultMap = new LinkedHashMap<Boolean, List<String>>();
		List<String> messages = new ArrayList<String>();
		boolean status = true;
		if (StringUtils.isBlank(name)) {
			status = false;
			messages.add("Name cannot be empty");
		}
		if (CollectionUtils.isNotEmpty(layerAttributes)) {
			for (LayerAttribute layerAttribute : layerAttributes) {
				Map<Boolean, List<String>> validateMap = layerAttribute.validate();
				if (validateMap.get(true) == null) {
					status = false;
					messages.add("All attributes should have at least one value allowed.");
					break;
				}
			}
		}
		resultMap.put(status, messages);
		return resultMap;
	}
}
