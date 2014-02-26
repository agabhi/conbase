package com.derive.conbase.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.derive.conbase.model.Layer;
import com.derive.conbase.model.Structure;
import com.derive.conbase.service.StructureService;

@Controller
public class StructureController {
	
	@Autowired
	StructureService structureService;
	
	@Transactional
	@RequestMapping(value = "/getStructures", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getStructures(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Structure> structures = structureService.getActiveStructures();
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(structures);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/getJqStructures", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	JqGridData<Structure> getJqStructures(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JqGridData<Structure> gridData = structureService.getStructuresByPage(1, 0);
		return gridData;
	}
	
	@Transactional
	@RequestMapping(value = "/getStructureById", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getStructureById(@RequestParam Long structureId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Structure structure = structureService.getStructureById(structureId);
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(structure);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/addStructure", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput addStructure(@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Structure structure = mapper.readValue(requestString, Structure.class);
		structure.setActive(true);
		ConbaseOutput output = new ConbaseOutput();
		Map<Boolean, List<String>> validateMap = structure.validate();
		if (validateMap.get(true) != null) {
			Structure existingStructure = structureService.getActiveStructureByName(structure.getName());
			if (existingStructure == null) {
				structureService.addStructure(structure);
				output.setSuccess(true);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Structure of same name is already existing!"}));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(validateMap.get(false));
		}

		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/updateStructure", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput updateStructure(@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Structure structure = mapper.readValue(requestString, Structure.class);
		ConbaseOutput output = new ConbaseOutput();
		Map<Boolean, List<String>> validateMap = structure.validate();
		if (validateMap.get(true) != null) {
			Structure existingStructure = structureService.getActiveStructureByName(structure.getName());
			if (existingStructure == null || existingStructure.getId().equals(structure.getId())) {
				structureService.updateStructure(structure);
				output.setSuccess(true);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Structure of same name is already existing!"}));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(validateMap.get(false));
		}
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/deactivateStructure", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput deactivateStructure(@RequestParam Long structureId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		if (structureId == null) {
			output.setSuccess(false);
			output.setMessages(Arrays.asList(new String[] {"No structure supplied!"}));
		} else {
			structureService.deactivate(structureId);
			output.setSuccess(true);
		}
		
		return output;
		
	}
}
