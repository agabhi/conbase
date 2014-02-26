package com.derive.conbase.model;

import java.util.Map;

public class AddRecordModel {
	private RecordType recordType;
	private Layer layer;
	private Structure structure;
	private Map<Long, String> layerProperties;
	private Map<Long, String> layerAttributes;
	private Map<Long, String> customAttributes;
	public RecordType getRecordType() {
		return recordType;
	}
	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}
	public Layer getLayer() {
		return layer;
	}
	public void setLayer(Layer layer) {
		this.layer = layer;
	}
	public Map<Long, String> getLayerProperties() {
		return layerProperties;
	}
	public void setLayerProperties(Map<Long, String> layerProperties) {
		this.layerProperties = layerProperties;
	}
	public Map<Long, String> getLayerAttributes() {
		return layerAttributes;
	}
	public void setLayerAttributes(Map<Long, String> layerAttributes) {
		this.layerAttributes = layerAttributes;
	}
	public Map<Long, String> getCustomAttributes() {
		return customAttributes;
	}
	public void setCustomAttributes(Map<Long, String> customAttributes) {
		this.customAttributes = customAttributes;
	}
	public Structure getStructure() {
		return structure;
	}
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	
	
}
