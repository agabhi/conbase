package com.derive.conbase.controllers;

import java.util.Arrays;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.derive.conbase.model.ConbaseOutput;
import com.derive.conbase.model.Serial;
import com.derive.conbase.service.SerialService;

@Controller
public class SerialController {
	@Autowired
	private SerialService serialService;

	@Transactional
	@RequestMapping(value = "/getSerials", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getSerials(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Serial> serials = serialService.getSerials();
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(serials);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/addSerial", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput addSerial(@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Serial serial = mapper.readValue(requestString, Serial.class);
		ConbaseOutput output = new ConbaseOutput();
		String name = serial.getName();
		if (StringUtils.isNotBlank(name)) {
			Serial existingSerial = serialService.getSerialByName(name);
			if (existingSerial == null) {
				serialService.addSerial(serial);
				output.setSuccess(true);
				output.setOutput(serial);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Serial of same name is already existing!"}));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(Arrays.asList(new String[] {"Serial name cannot be blank!"}));
		}

		return output;
	}
}
