package com.derive.conbase.controllers.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ViewController {
	@Transactional
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String addUser(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return "home";
	}
	
	@Transactional
	@RequestMapping(value = "/structure-design", method = RequestMethod.GET)
	public String structureDesign(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return "structureDesign";
	}
	
	@Transactional
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return "login";
	}
	
	@Transactional
	@RequestMapping(value = "/pricing", method = RequestMethod.GET)
	public String pricing(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return "pricing";
	}
	
	@Transactional
	@RequestMapping(value = "/structures", method = RequestMethod.GET)
	public String structures(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return "structures";
	}
	
	@Transactional
	@RequestMapping(value = "/layers", method = RequestMethod.GET)
	public String layers(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return "layers";
	}
	
	@Transactional
	@RequestMapping(value = "/recordTypes", method = RequestMethod.GET)
	public String records(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return "recordTypes";
	}
	
	@Transactional
	@RequestMapping(value = "/userHome", method = RequestMethod.GET)
	public String addRecord(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return "userHome";
	}
}
