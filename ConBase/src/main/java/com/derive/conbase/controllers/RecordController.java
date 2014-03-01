package com.derive.conbase.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.derive.conbase.model.AddRecordModel;
import com.derive.conbase.model.ConbaseOutput;
import com.derive.conbase.model.LayerAttributeConfig;
import com.derive.conbase.model.Record;
import com.derive.conbase.model.RecordSearchCriteria;
import com.derive.conbase.model.RecordType;
import com.derive.conbase.model.RecordTypeAttribute;
import com.derive.conbase.model.RecordsGridModel;
import com.derive.conbase.service.AttributeService;
import com.derive.conbase.service.LayerService;
import com.derive.conbase.service.RecordService;
import com.derive.conbase.service.RecordTypeService;

@Controller
public class RecordController {

	@Autowired
	LayerService layerService;
	@Autowired
	RecordTypeService recordTypeService;
	@Autowired
	RecordService recordService;
	@Autowired
	AttributeService attributeService;

	@Transactional
	@RequestMapping(value = "/getRecordById", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getRecordById(@RequestParam Long recordId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Record record = recordService.getRecordById(recordId);
		record.setRecordAttributes(recordService.getRecordAttributesByRecordIds(Arrays.asList(new Long[] {record.getId()})));
		if (record.getLayerAttributeConfig() != null && record.getLayerAttributeConfig().getId() != null) {
			List<LayerAttributeConfig> layerAttributeConfigs = layerService.getLayerAttributeConfigsByConfigIds(Arrays.asList(new Long[] {record.getLayerAttributeConfig().getId()}));
			if (CollectionUtils.isNotEmpty(layerAttributeConfigs)) {
				record.setLayerAttributeConfig(layerAttributeConfigs.get(0));
			}
		}
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(record);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/getRecordsGrid", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getRecordsGrid(@RequestParam(required= false) Integer pageNumber, @RequestParam Long recordTypeId,
			@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		RecordsGridModel gridModel = new RecordsGridModel();
		RecordSearchCriteria search = null;
		if (StringUtils.isNotBlank(requestString)) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(
					DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);
			JsonFactory factory = mapper.getJsonFactory();
			JsonParser jp = factory.createJsonParser(requestString);
			JsonNode requestObj = mapper.readTree(jp);
			JsonNode conditions = requestObj.get("conditions");
			Iterator<JsonNode> conditionsIt = conditions.getElements();

			search = new RecordSearchCriteria();
			while (conditionsIt.hasNext()) {
				JsonNode condition = conditionsIt.next();
				if (condition.get("column") != null) {
					String column = condition.get("column").getValueAsText();
					String operation = condition.get("operation")
							.getValueAsText();
					String value = condition.get("value").getValueAsText();
					if (StringUtils.isNotBlank(column)
							&& StringUtils.isNotBlank(value)
							&& StringUtils.isNotBlank(operation)) {
						column = column.trim();
						if (StringUtils.isNotBlank(column)
								&& column.equals("layerId")) {
							search.addLayerId(operation, value);
						} else if (StringUtils.isNotBlank(column)
								&& column.equals("structureItemId")) {
							search.addStructureItemId(operation, value);
						} else if (StringUtils.isNotBlank(column)
								&& column.equals("structureId")) {
							search.addStructureId(operation, value);
						} else if (StringUtils.isNotBlank(column)
								&& column.equals("serial")) {
							search.addSerial(operation, value);
						} else if (column.startsWith("l_")) {
							Long attributeId = Long
									.valueOf(column.split("l_")[1]);
							search.addAttribute(attributeId, operation, value);
						} else if (column.startsWith("rl_")) {
							Long recordAttributeTypeId = Long.valueOf(column
									.split("rl_")[1]);
							search.addRecordTypeLayerAttribute(
									recordAttributeTypeId, operation, value);
						} else if (column.startsWith("rc_")) {
							Long recordAttributeTypeId = Long.valueOf(column
									.split("rc_")[1]);
							search.addRecordTypeCustomAttribute(
									recordAttributeTypeId, operation, value);
						}
					}
				}
			}
		}
		
		if (pageNumber == null || pageNumber.equals(0)) {
			pageNumber = 1;
		}
		gridModel.setCurrentPage(pageNumber);
		RecordType recordType = recordTypeService
				.getRecordTypeWithAttributesById(recordTypeId);
		List<RecordTypeAttribute> recordTypeAttributes = recordType
				.getCustomAttributes();
		gridModel.setRecordTypeAttributes(recordTypeAttributes);
		gridModel.setLayerAttributes(attributeService.getAttributesByType(recordType.getType()));
		gridModel.setRecords(recordService.getRecordsByPage(recordTypeId, pageNumber,
				50, search));
		gridModel.setTotalRecords(recordService.getRecordsCount(recordTypeId, search));
		if (CollectionUtils.isNotEmpty(gridModel.getRecords())) {
			List<Long> recordIds = new ArrayList<Long>();
			for (Record r : gridModel.getRecords()) {
				recordIds.add(r.getId());
			}
			gridModel.setRecordAttributes(recordService
					.getRecordAttributesByRecordIds(recordIds));
			
			Set<Long> layerConfigIds = new HashSet<Long>();
			for (Record r : gridModel.getRecords()) {
				if (r.getLayerAttributeConfig() != null) {
					layerConfigIds.add(r.getLayerAttributeConfig().getId());
				}
			}
			if (CollectionUtils.isNotEmpty(layerConfigIds)) {
				gridModel.setLayerAttributeConfigs(layerService
						.getLayerAttributeConfigsByConfigIds(new ArrayList<Long>(
								layerConfigIds)));
			}
		}
		output.setSuccess(true);
		output.setOutput(gridModel);
		return output;
	}

	@Transactional
	@RequestMapping(value = "/updateRecord", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput updateRecord(@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Record record = mapper.readValue(requestString,
				Record.class);
		LayerAttributeConfig lac = record.getLayerAttributeConfig();
		LinkedHashMap<Long, String> attributeValueMap = lac.getAttributeValueMap();
		LinkedHashMap<Long, String> sortedAttributeValueMap = null;
		if (MapUtils.isNotEmpty(attributeValueMap)) {
			Set<Long> keySet = attributeValueMap.keySet();
			List<Long> keyList = new ArrayList<Long>(keySet);
			Collections.sort(keyList);
			for (Long key : keyList) {
				String value = attributeValueMap.get(key);
				if (key != null && !key.equals(0) && value != null && StringUtils.isNotBlank(value)) {
					if (sortedAttributeValueMap == null) {
						sortedAttributeValueMap = new LinkedHashMap<Long, String>();
					}
					sortedAttributeValueMap.put(key, value);
				}
			}
			lac.setAttributeValueMap(sortedAttributeValueMap);
		}
		recordService.updateRecord(record);
		output.setSuccess(true);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/addRecord", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput addRecord(@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Record record = mapper.readValue(requestString,
				Record.class);
		LayerAttributeConfig lac = record.getLayerAttributeConfig();
		LinkedHashMap<Long, String> attributeValueMap = lac.getAttributeValueMap();
		LinkedHashMap<Long, String> sortedAttributeValueMap = null;
		if (MapUtils.isNotEmpty(attributeValueMap)) {
			Set<Long> keySet = attributeValueMap.keySet();
			List<Long> keyList = new ArrayList<Long>(keySet);
			Collections.sort(keyList);
			for (Long key : keyList) {
				String value = attributeValueMap.get(key);
				if (key != null && !key.equals(0) && value != null && StringUtils.isNotBlank(value)) {
					if (sortedAttributeValueMap == null) {
						sortedAttributeValueMap = new LinkedHashMap<Long, String>();
					}
					sortedAttributeValueMap.put(key, value);
				}
			}
			lac.setAttributeValueMap(sortedAttributeValueMap);
		}
		recordService.addRecord(record);
		output.setSuccess(true);
		return output;
	}

	private Record convertToRecord(AddRecordModel addRecordModel) {
		Record record = null;
		if (addRecordModel != null) {
			record = new Record();
			Map<Long, String> layerProperties = addRecordModel
					.getLayerProperties();
			
			if (addRecordModel.getLayer() != null) {
				LinkedHashMap<Long, String> attributeValueMap = null;
				if (MapUtils.isNotEmpty(layerProperties)) {
					List<Long> attributeIds = new ArrayList<Long>(
							layerProperties.keySet());
					Collections.sort(attributeIds);
					attributeValueMap = new LinkedHashMap<Long, String>();
					for (Long attributeId : attributeIds) {
						attributeValueMap.put(attributeId,
								layerProperties.get(attributeId));
					}
				}
				LayerAttributeConfig lac = new LayerAttributeConfig();
				lac.setLayer(addRecordModel.getLayer());
				lac.setAttributeValueMap(attributeValueMap);
				record.setLayerAttributeConfig(lac);
			}
			record.setStructure(addRecordModel.getStructure());
			record.setRecordType(addRecordModel.getRecordType());
			Map<Long, String> layerAttributes = addRecordModel
					.getLayerAttributes();
			if (MapUtils.isNotEmpty(layerAttributes)) {
				for (Entry<Long, String> layerAttribute : layerAttributes
						.entrySet()) {
					RecordTypeAttribute attribute = recordTypeService
							.getRecordTypeAttributeById(layerAttribute.getKey());
					if (attribute.getType() == 6) {
						record.setFrom(Long.valueOf(layerAttribute.getValue()));
					} else if (attribute.getType() == 7) {
						record.setTo(Long.valueOf(layerAttribute.getValue()));
					} else if (attribute.getType() == 8) {
						record.setLevel(Integer.valueOf(layerAttribute
								.getValue()));
					}
				}
			}
			Map<Long, String> customAttributes = addRecordModel
					.getCustomAttributes();
			if (MapUtils.isNotEmpty(addRecordModel.getCustomAttributes())) {
				Map<Long, String> attributeValuesMap = new HashMap<Long, String>();
				for (Entry<Long, String> customAttribute : customAttributes
						.entrySet()) {
					attributeValuesMap.put(customAttribute.getKey(),
							customAttribute.getValue());
				}
				record.setAttributeValuesMap(attributeValuesMap);
			}
		}
		return record;
	}
}