package com.derive.conbase.controllers;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.derive.conbase.model.Attribute;
import com.derive.conbase.model.ConbaseOutput;
import com.derive.conbase.service.AttributeService;

@Controller
public class RecordTypeAttributeController {
	
	@Autowired
	AttributeService attributeService;
	
	@Transactional
	@RequestMapping(value = "/addRecordTypeAttribute", method = RequestMethod.POST, consumes="application/json", produces="application/json")
	public @ResponseBody ConbaseOutput addAttribute(@RequestBody Attribute attribute, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		String name = attribute.getName();
		if (StringUtils.isNotBlank(name)) {
			Attribute existingAttribute = attributeService.getAttributeByName(name);
			if (existingAttribute == null) {
				attributeService.addAttribute(attribute);
				output.setSuccess(true);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Attribute of same name is already existing!"}));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(Arrays.asList(new String[] {"Attribute name is empty!"}));
		}
		
		return output;
	}
}
