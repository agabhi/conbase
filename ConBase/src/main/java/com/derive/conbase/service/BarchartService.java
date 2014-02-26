package com.derive.conbase.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.derive.conbase.dao.BarchartDAO;
import com.derive.conbase.dao.LayerDAO;
import com.derive.conbase.dao.RecordTypeDAO;
import com.derive.conbase.model.Barchart;
import com.derive.conbase.model.LayerAttributeConfig;

@Service
public class BarchartService {
	
	@Autowired
	BarchartDAO barchartDAO;
	@Autowired
	LayerDAO layerDAO;
	@Autowired
	RecordTypeDAO recordTypeDAO;
	
	public Barchart findBarchartByIdAndUserId(Long id, Long userId) {
		Barchart barchart = barchartDAO.findBarchartByIdAndUserId(id, userId);
		return barchart;
	}
	
	public Barchart findBarchartWithDetailsByIdAndUserId(Long id, Long userId) {
		Barchart barchart = barchartDAO.findBarchartByIdAndUserId(id, userId);
		if (barchart != null && barchart.getRecordTypeAttribute() != null && barchart.getRecordTypeAttribute().getId() != null) {
			barchart.setRecordTypeAttribute(recordTypeDAO.findRecordTypeAttributeById(barchart.getRecordTypeAttribute().getId()));
		}
		if (barchart != null && CollectionUtils.isNotEmpty(barchart.getLayerAttributeConfigs())) {
			Set<Long> layerConfigIds = new HashSet<Long>();
			for (LayerAttributeConfig lac : barchart.getLayerAttributeConfigs()) {
				if (lac != null && lac.getId() != null) {
					layerConfigIds.add(lac.getId());
				}
			}
			barchart.setLayerAttributeConfigs(layerDAO.findLayerAttributeConfigsByConfigIds(new ArrayList<Long>(layerConfigIds)));
		}
		return barchart;
	}
	
	public Barchart getBarchartByNameAndUserId(String name, Long userId) {
		return barchartDAO.findBarchartByNameAndUserId(name, userId);
	}
	public List<Barchart> getBarchartsByRecordTypeIdAndUserId(Long recordTypeId, Long userId) {
		return barchartDAO.findBarchartsByRecordTypeIdAndUserId(recordTypeId, userId);
	}
	public Barchart createBarchart(final Barchart barchart) {
		return barchartDAO.createBarchart(barchart);
	}
	
	public Barchart getBarchartByRecordTypeAndNameAndUserId(Long recordTypeId, String name, Long userId) {
		return barchartDAO.findBarchartByRecordTypeAndNameAndUserId(recordTypeId, name, userId);
	}
}
