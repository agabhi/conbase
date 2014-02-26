package com.derive.conbase.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

public class LayerAttributeConfig {
	private Long id;
	private Layer layer;
	private LinkedHashMap<Long, String> attributeValueMap;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public LinkedHashMap<Long, String> getAttributeValueMap() {
		return attributeValueMap;
	}

	public void setAttributeValueMap(
			LinkedHashMap<Long, String> attributeValueMap) {
		this.attributeValueMap = attributeValueMap;
	}

	public String getAttributeIds() {
		String attributeIds = null;
		if (MapUtils.isNotEmpty(attributeValueMap)) {
			for (Map.Entry<Long, String> entry : attributeValueMap.entrySet()) {
				Long attributeId = entry.getKey();
				if (attributeId != null) {
					if (attributeIds != null) {
						attributeIds += "," + attributeId;
					} else {
						attributeIds = "" + attributeId;
					}
				}
			}
		}
		return attributeIds;
	}
	
	public String getValueString() {
		String values = null;
		if (MapUtils.isNotEmpty(attributeValueMap)) {
			for (Map.Entry<Long, String> entry : attributeValueMap.entrySet()) {
				Long attributeId = entry.getKey();
				String value = entry.getValue();
				if (attributeId != null) {
					if (values != null) {
						values += "{::}" + value;
					} else {
						values = "" + value;
					}
				}
			}
		}
		return values;
	}
	
	public void setLayerFromLayerId(Long layerId) {
		Layer layer = new Layer();
		layer.setId(layerId);
		this.layer = layer;
	}
	
	public void setAttributeValueMapFromStrings(String attributeIds, String values) {
		LinkedHashMap<Long, String> attributeValueMap = null;
		if (attributeIds != null && attributeIds.trim().length() > 0) {
			attributeValueMap = new LinkedHashMap<Long, String>();
			String[] attributes = attributeIds.split(",");
			String[] valuess = values.split("\\{::\\}");
			for (int i = 0; i < attributes.length; ++i) {
				attributeValueMap.put(Long.valueOf(attributes[i]), valuess[i]);
			}
		}
		this.attributeValueMap = attributeValueMap;
	}

}
