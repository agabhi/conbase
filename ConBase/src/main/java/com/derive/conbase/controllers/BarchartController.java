package com.derive.conbase.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
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

import com.derive.conbase.model.Barchart;
import com.derive.conbase.model.BarchartData;
import com.derive.conbase.model.ConbaseOutput;
import com.derive.conbase.model.JqGridData;
import com.derive.conbase.model.LayerAttributeConfig;
import com.derive.conbase.model.LayerChartEntry;
import com.derive.conbase.model.Record;
import com.derive.conbase.model.RecordAttribute;
import com.derive.conbase.service.BarchartService;
import com.derive.conbase.service.LayerService;
import com.derive.conbase.service.RecordService;
import com.derive.conbase.util.UserUtils;

@Controller
public class BarchartController {
	
	@Autowired
	BarchartService barchartService;
	
	@Autowired
	LayerService layerService;
	
	@Autowired
	RecordService recordService;
	
	@Transactional
	@RequestMapping(value = "/getBarchartsByRecordTypeId", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getBarchartsByRecordTypeId(@RequestParam Long recordTypeId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<Barchart> barcharts = barchartService.getBarchartsByRecordTypeIdAndUserId(recordTypeId, UserUtils.getLoggedInUser().getId());
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(barcharts);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/getJqBarcharts", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	JqGridData<Barchart> getJqBarcharts(@RequestParam Long recordTypeId, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long userId = UserUtils.getLoggedInUser().getId();
		List<Barchart> barcharts = barchartService.getBarchartsByRecordTypeIdAndUserId(recordTypeId, userId);
		int records = 0;
		int page = 1;
		int total = 0;
		if (CollectionUtils.isNotEmpty(barcharts)) {
			records = barcharts.size();
			total = 1;
		}
		JqGridData<Barchart> gridData = new JqGridData<Barchart>(total, page, records, barcharts);
		return gridData;
	}
	
	@Transactional
	@RequestMapping(value = "/getBarchartInfoById", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ConbaseOutput getBarchartInfoById(@RequestParam Long barchartId, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		Barchart barchart = barchartService.findBarchartWithDetailsByIdAndUserId(barchartId, UserUtils.getLoggedInUser().getId());
		output.setSuccess(true);
		output.setOutput(barchart);
		return output;
	}	
	
	@Transactional
	@RequestMapping(value = "/createBarchart", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput createBarchart(@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Barchart barchart = mapper.readValue(requestString, Barchart.class);
		ConbaseOutput output = new ConbaseOutput();
		Map<Boolean, List<String>> validateMap = barchart.validate();
		if (validateMap.get(true) != null) {
			Barchart existingBarchart = barchartService.getBarchartByRecordTypeAndNameAndUserId(barchart.getRecordType().getId(), barchart.getName(), UserUtils.getLoggedInUser().getId());
			if (existingBarchart == null) {
				barchartService.createBarchart(barchart);
				output.setSuccess(true);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Barchart of same name is already existing!"}));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(validateMap.get(false));
		}
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/getBarchartDataByFromByTo", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getBarchartDataByFromByTo(@RequestParam Long barchartId, @RequestParam Integer from, @RequestParam Integer to, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BarchartData barchartData = new BarchartData();
		Barchart barchart = barchartService.findBarchartByIdAndUserId(barchartId, UserUtils.getLoggedInUser().getId());
		Set<Long> layerConfigIds = new HashSet<Long>();
		for (LayerAttributeConfig lac : barchart.getLayerAttributeConfigs()) {
			if (lac != null && lac.getId() != null) {
				layerConfigIds.add(lac.getId());
			}
		}
		List<Record> records = recordService.getRecordsByRecordTypeIdByConfigIdsByFromByTo(barchart.getRecordType().getId(), new ArrayList<Long>(layerConfigIds), from, to);
		barchartData.setRecords(records);
		List<Long> recordIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(records)) {
			for (Record r : records) {
				recordIds.add(r.getId());
			}
			List<RecordAttribute> recordAttributes = recordService.getRecordAttributesByRecordIdsByRecordTypeAttributeId(recordIds, barchart.getRecordTypeAttribute().getId());
			barchartData.setRecordAttributes(recordAttributes);
		}
		List<LayerChartEntry> entries = layerService.getLayerChartEntriesByConfigIdsByFromByTo(new ArrayList<Long>(layerConfigIds), from, to);
		barchartData.setLayerChartEntries(entries);
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(barchartData);
		return output;
	}
}
