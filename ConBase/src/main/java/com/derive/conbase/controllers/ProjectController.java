package com.derive.conbase.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.derive.conbase.model.ConbaseOutput;
import com.derive.conbase.model.Project;
import com.derive.conbase.model.User;
import com.derive.conbase.service.ProjectService;
import com.derive.conbase.service.UserService;
import com.derive.conbase.util.ElasticEmail;
import com.derive.conbase.util.EmailSender;
import com.derive.conbase.util.EmailUtil;

@Controller
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private UserService userService;
	
	@Transactional
	@RequestMapping(value = "/getProjectById", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getProjectById(@RequestParam Long projectId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Project project = projectService.getProjectById(projectId);
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(project);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/addProject", method = RequestMethod.POST)
	public  @ResponseBody ConbaseOutput  addProject(@RequestBody String requestString, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Project project = mapper.readValue(requestString, Project.class);
		
		User loggedInUser = ((com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		project.setUser(loggedInUser);
		
		List<Project> projects = projectService.getAllOwnedProjectsByUserId(loggedInUser.getId());
		boolean activeProjectFound = false;
		if (CollectionUtils.isNotEmpty(projects)) {
			for (Project existingProject : projects) {
				if (existingProject != null && existingProject.isActive() != null && existingProject.isActive()) {
					activeProjectFound = true;
				}
			}
		}
		if (activeProjectFound) {
			project.setActive(false);
		} else {
			project.setActive(true);
		}
		Map<Boolean, List<String>> validateMap = project.validate();
		if (validateMap.get(true) != null) {
			projectService.addProject(project);
			output.setSuccess(true);
		} else {
			output.setSuccess(false);
			output.setMessages(validateMap.get(false));
		}
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/getAllOwnedProjects", method = RequestMethod.POST)
	public  @ResponseBody ConbaseOutput  getAllOwnedProjects(@RequestBody String requestString, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		User loggedInUser = ((com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		List<Project> projects = projectService.getAllOwnedProjectsByUserId(loggedInUser.getId());
		output.setSuccess(true);
		output.setOutput(projects);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/getAllInvitedProjects", method = RequestMethod.POST)
	public  @ResponseBody ConbaseOutput  getAllInvitedProjects(@RequestBody String requestString, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		User loggedInUser = ((com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		List<Project> projects = projectService.getAllInvitedProjectsByUserId(loggedInUser.getId());
		output.setSuccess(true);
		output.setOutput(projects);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/removeUserFromProject", method = RequestMethod.POST)
	public  @ResponseBody ConbaseOutput  addUser(@RequestParam Long projectId, @RequestParam Long userId, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		projectService.removeUserFromProject(userId, projectId);
		output.setSuccess(true);
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/inviteUser", method = RequestMethod.POST)
	public  @ResponseBody ConbaseOutput  inviteUser(@RequestParam Long projectId, @RequestParam String email, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		User loggedInUser = ((com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		Project project = projectService.getProjectById(projectId);
		if (project.getUser().getId().equals(loggedInUser.getId())) {
			if (EmailUtil.validateEmail(email)) {
				if (project.getUser().getEmail().equals(email)) {
					output.setSuccess(false);
					output.setMessages(Arrays.asList(new String[] {"This user is already the owner of the project!"}));
				} else {
					boolean userAlreadyAdded = false;
					if (CollectionUtils.isNotEmpty(project.getInvitedUsers())) {
						for (User invitedUser : project.getInvitedUsers()) {
							if (invitedUser.getEmail().trim().equals(email.trim())) {
								userAlreadyAdded = true;
								break;
							}
						}
					}
					if (userAlreadyAdded) {
						output.setSuccess(false);
						output.setMessages(Arrays.asList(new String[] {"This user is already added to the project!"}));	
					} else {
						User user = userService.findUserByEmailId(email.trim());
						if (user != null) {
							projectService.addUserToProject(user.getId(), projectId);
							String subject = "infraCMS - You have been added to the project - "+project.getName()+".";
							StringBuffer message = new StringBuffer();
							message.append("<p>Dear User,</p>");
							message.append("<p style='margin-top:10px;'>You have been added to work on the project ").append(project.getName()+".</p>");
							message.append("<p>To work on the project, please go to your infraCMS home page and click on the Project->Go button.</p>");
							message.append("<p style='margin-top:10px;margin-bottom:0px;'>Best wishes,</p>");
							message.append("<p>infraCMS Team</p>");
							//new EmailSender().sendMail(new String[] {email}, subject, message.toString());
							ElasticEmail.sendElasticEmail("admin@infracms.com", "infraCMS - admin", subject, message.toString(), email);
							output.setOutput("complete");
						} else {
							projectService.addProjectForRegistration(email, projectId);
							String subject = "infraCMS - You have been added to the project - "+project.getName()+".";
							StringBuffer message = new StringBuffer();
							message.append("<p>Dear User,</p>");
							message.append("<p style='margin-top:10px;'>You have been added to work on the project ").append(project.getName()+".</p>");
							message.append("<p>To work on the project, you need to register on http://www.infracms.com. After registering, please go to your infraCMS home page and click on the Project->Go button.</p>");
							message.append("<p style='margin-top:10px;margin-bottom:0px;'>Best wishes,</p>");
							message.append("<p>infraCMS Team</p>");
							//new EmailSender().sendMail(new String[] {email}, subject, message.toString());
							ElasticEmail.sendElasticEmail("admin@infracms.com", "infraCMS - admin", subject, message.toString(), email);
							output.setOutput("pending");
						}
					}
				}
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Not a valid email!"}));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(Arrays.asList(new String[] {"Only the owner of the project can invite users!"}));
		}
		
		return output;
	}
	
	
}
