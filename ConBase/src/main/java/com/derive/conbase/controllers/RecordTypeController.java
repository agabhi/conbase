package com.derive.conbase.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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

import com.derive.conbase.model.ConbaseOutput;
import com.derive.conbase.model.JqGridData;
import com.derive.conbase.model.RecordType;
import com.derive.conbase.service.RecordTypeService;

@Controller
public class RecordTypeController {
	@Autowired
	RecordTypeService recordTypeService;
	
	@Transactional
	@RequestMapping(value = "/getRecordTypeById", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getRecordTypeById(@RequestParam Long recordTypeId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RecordType recordType = recordTypeService.getRecordTypeWithAttributesById(recordTypeId);
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(recordType);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/getJqRecordTypes", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	JqGridData<RecordType> getJqRecordTypes(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JqGridData<RecordType> gridData = recordTypeService.getRecordTypesByPage(1, 0);
		return gridData;
	}
	
	@Transactional
	@RequestMapping(value = "/addRecordType", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput addRecordType(@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		RecordType recordType = mapper.readValue(requestString, RecordType.class);
		ConbaseOutput output = new ConbaseOutput();
		String name = recordType.getName();
		if (StringUtils.isNotBlank(name)) {
			RecordType existingRecordType = recordTypeService.getRecordTypeByName(name);
			if (existingRecordType == null) {
				recordTypeService.addRecordType(recordType);
				output.setSuccess(true);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Record type of same name is already existing!"}));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(Arrays.asList(new String[] {"Record type name is empty!"}));
		}

		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/updateRecordType", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput updateLayer(@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		RecordType recordType = mapper.readValue(requestString, RecordType.class);
		ConbaseOutput output = new ConbaseOutput();
		Map<Boolean, List<String>> validateMap = recordType.validate();
		if (validateMap.get(true) != null) {
			RecordType existingRecordType = recordTypeService.getRecordTypeByName(recordType.getName());
			if (existingRecordType == null || existingRecordType.getId().equals(existingRecordType.getId())) {
				recordTypeService.updateRecordType(recordType);
				output.setSuccess(true);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Record type of same name is already existing!"}));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(validateMap.get(false));
		}
		return output;
	}
}
