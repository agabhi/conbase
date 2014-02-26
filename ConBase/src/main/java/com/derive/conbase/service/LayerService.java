package com.derive.conbase.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.derive.conbase.dao.AttributeDAO;
import com.derive.conbase.dao.LayerDAO;
import com.derive.conbase.model.JqGridData;
import com.derive.conbase.model.Layer;
import com.derive.conbase.model.LayerAttribute;
import com.derive.conbase.model.LayerAttributeConfig;
import com.derive.conbase.model.LayerChartEntry;

@Service
public class LayerService {

	@Autowired
	LayerDAO layerDAO;
	@Autowired
	AttributeDAO attributeDAO;

	public List<Layer> getActiveLayers() {
		return layerDAO.findActiveLayers();
	}
	
	public List<Layer> getActiveItems(Short type) {
		return layerDAO.findActiveItems(type);
	}

	public Layer getLayersWithAttributesByLayerId(long layerId) {
		Layer layer = layerDAO.findLayerById(layerId);
		layer.setLayerAttributes(layerDAO.findLayerAttributesByLayerId(layerId));
		layer.setLayerAttributeConfigs(layerDAO.findLayerAttributeConfigsByLayerId(layerId));
		return layer;
	}
	
	public List<LayerAttribute> getLayerAttributesByAttributeId(long attributeId) {
		return layerDAO.findLayerAttributesByAttributeId(attributeId);
	}

	public JqGridData<Layer> getLayersByPage(int pageNo, int size) {
		return layerDAO.findLayersByPage(pageNo, size);
	}
	
	public JqGridData<Layer> getItemsByPageByType(int pageNo, int size, short type) {
		return layerDAO.findItemsByPageByType(pageNo, size, type);
	}

	public Layer getActiveLayerByName(String name) {
		return layerDAO.findActiveLayerByName(name);
	}
	
	public List<Layer> getLayersByPartialName(String name) {
		return layerDAO.findLayersByPartialName(name);
	}

	public void addLayer(Layer layer) {
		layerDAO.save(layer);
	}
	
	public void updateLayer(Layer layer) {
		layerDAO.updateLayer(layer);
	}
	
	public void updateLayerAttribute(final LayerAttribute layerAttribute) {
		layerDAO.updateLayerAttribute(layerAttribute);
	}

	public LayerAttributeConfig getLayerAttributeConfigByValues(
			LayerAttributeConfig layerAttributeConfig) {
		return layerDAO.findLayerAttributeConfigByValues(layerAttributeConfig);
	}
	
	public LayerAttributeConfig createLayerAttributeConfigByValues(LayerAttributeConfig layerAttributeConfig) {
		return layerDAO.createLayerAttributeConfigByValues(layerAttributeConfig);
	}
	
	public void saveLayerChart(Long layerAttributeConfigId, List<LayerChartEntry> layerChartEntries) {
		layerDAO.saveLayerChart(layerAttributeConfigId, layerChartEntries);
	}
	
	public void deactivate(Long layerId) {
		layerDAO.deactivate(layerId);
	}
	
	public void deleteLayerChart(Long layerAttributeConfigId) {
		layerDAO.deleteLayerChart(layerAttributeConfigId);
	}
	
	public boolean validateLayerAttributeConfig(LayerAttributeConfig layerAttributeConfig) {
		Layer layer = layerAttributeConfig.getLayer();
		for (LayerAttribute layerAttribute : layer.getLayerAttributes()) {
			if (layerAttribute.isMandatory()) {
				Long attributeId = layerAttribute.getAttribute().getId();
				if (!layerAttributeConfig.getAttributeValueMap().containsKey(attributeId) || StringUtils.isBlank(layerAttributeConfig.getAttributeValueMap().get(attributeId))) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void cleanAndOrderLayerAttributeConfig(LayerAttributeConfig layerAttributeConfig) {
		if (layerAttributeConfig != null && layerAttributeConfig.getAttributeValueMap() != null && layerAttributeConfig.getAttributeValueMap().size() > 0) {
			LinkedHashMap<Long, String> attributeValueMap = layerAttributeConfig.getAttributeValueMap();
			LinkedHashMap<Long, String> newAttributeValueMap = layerAttributeConfig.getAttributeValueMap();
			List<Long> attributeIds = new ArrayList<Long>(attributeValueMap.keySet());
			Collections.sort(attributeIds);
			for (Long attributeId : attributeIds) {
				String value = attributeValueMap.get(attributeId);
				if (StringUtils.isNotBlank(value)) {
					newAttributeValueMap.put(attributeId, value);
				}
			}
			layerAttributeConfig.setAttributeValueMap(newAttributeValueMap);
		}
	}
	
	public List<LayerChartEntry> getLayerChartEntriesByConfigIdByFromByTo(long layerAttributeConfigId, int from, int to) {
		return layerDAO.findLayerChartEntriesByConfigIdByFromByTo(layerAttributeConfigId, from, to);
	}
	
	public List<LayerChartEntry> getLayerChartEntriesByConfigIdsByFromByTo(List<Long> layerAttributeConfigIds, int from, int to) {
		return layerDAO.findLayerChartEntriesByConfigIdsByFromByTo(layerAttributeConfigIds, from, to);
	}
	
	public List<LayerAttributeConfig> getLayerAttributeConfigsByConfigIds(List<Long> layerAttributeConfigIds) {
		return layerDAO.findLayerAttributeConfigsByConfigIds(layerAttributeConfigIds);
	}
}
