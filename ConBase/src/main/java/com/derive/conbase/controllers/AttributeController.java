package com.derive.conbase.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.bytecode.buildtime.Instrumenter.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.derive.conbase.model.Attribute;
import com.derive.conbase.model.ConbaseOutput;
import com.derive.conbase.model.Layer;
import com.derive.conbase.model.LayerAttribute;
import com.derive.conbase.service.AttributeService;
import com.derive.conbase.service.LayerService;

@Controller
public class AttributeController {
	
	@Autowired
	AttributeService attributeService;
	
	@Autowired
	LayerService layerService;
	
	@Transactional
	@RequestMapping(value = "/getAttributes", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ConbaseOutput getAttributes(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Attribute> attributes = attributeService.getAttributes();
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(attributes);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/getAttributesByType", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ConbaseOutput getAttributesByType(@RequestParam Short type, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Attribute> attributes = attributeService.getAttributesByType(type);
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(attributes);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/updateAttribute", method = RequestMethod.POST, consumes="application/json", produces="application/json")
	public @ResponseBody ConbaseOutput updateAttribute(@RequestBody String requestString, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Attribute attribute = mapper.readValue(requestString, Attribute.class);
		ConbaseOutput output = new ConbaseOutput();
		String name = attribute.getName();
		removeEmptyOptions(attribute);
		Map<Boolean, List<String>> validateMap = attribute.validate();
		if (validateMap.get(true) != null) {
			Attribute existingAttribute = attributeService.getAttributeByName(name);
			if (existingAttribute == null || existingAttribute.getName().trim().equals(attribute.getName().trim())) {
				attributeService.updateAttribute(attribute);
				List<LayerAttribute> layerAttributes = layerService.getLayerAttributesByAttributeId(attribute.getId());
				if (CollectionUtils.isNotEmpty(layerAttributes)) {
					for (LayerAttribute la : layerAttributes) {
						boolean toBeChanged = false;
						List<String> newOptions = new ArrayList<String>();
						if (la.getOptionsString() != null && !la.getAllowedValues().equals("All")) {
							List<String> options = la.getOptions();
							if (CollectionUtils.isNotEmpty(options) && CollectionUtils.isNotEmpty(attribute.getOptions())) {
								for (String option : options) {
									if (!attribute.getOptions().contains(option)) {
										toBeChanged = true;
									} else {
										newOptions.add(option);
									}
								}
							}
						}
						if (toBeChanged) {
							if (CollectionUtils.isEmpty(newOptions)) {
								la.setOptions(null);
								la.setAllowedValues("None");
							} else {
								la.setOptions(newOptions);
								la.setAllowedValues("Selected ("+newOptions.size()+")");
							}
							layerService.updateLayerAttribute(la);
						}
					}
				}
				output.setSuccess(true);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Attribute of same name is already existing!"}));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(validateMap.get(false));
		}
		
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/deleteAttribute", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput deleteAttribute(@RequestParam Long attributeId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		if (attributeId == null) {
			output.setSuccess(false);
			output.setMessages(Arrays.asList(new String[] {"No attribute supplied!"}));
		} else {
			attributeService.deleteAttribute(attributeId);
			output.setSuccess(true);
		}
		
		return output;
		
	}
	
	@Transactional
	@RequestMapping(value = "/addAttribute", method = RequestMethod.POST, consumes="application/json", produces="application/json")
	public @ResponseBody ConbaseOutput addAttribute(@RequestBody Attribute attribute, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		String name = attribute.getName();
		removeEmptyOptions(attribute);
		Map<Boolean, List<String>> validateMap = attribute.validate();
		if (validateMap.get(true) != null) {
			Attribute existingAttribute = attributeService.getAttributeByName(name);
			if (existingAttribute == null) {
				attributeService.addAttribute(attribute);
				output.setOutput(attribute);
				output.setSuccess(true);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Attribute of same name is already existing!"}));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(validateMap.get(false));
		}
		
		return output;
	}
	
	private void removeEmptyOptions(Attribute attribute) {
		List<String> options = attribute.getOptions();
		List<String> newOptions = null;
		if (CollectionUtils.isNotEmpty(options) ) {
			for (String option : options) {
				if (StringUtils.isNotBlank(option)) {
					if (newOptions == null) {
						newOptions = new ArrayList<String>();
					}
					newOptions.add(option);
				}
 			}
		}
		attribute.setOptions(newOptions);
	}
}
