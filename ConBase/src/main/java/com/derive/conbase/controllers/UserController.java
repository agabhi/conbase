package com.derive.conbase.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.derive.conbase.logging.ConbaseLogger;
import com.derive.conbase.model.ConbaseOutput;
import com.derive.conbase.model.Project;
import com.derive.conbase.model.User;
import com.derive.conbase.service.ProjectService;
import com.derive.conbase.service.UserService;
import com.derive.conbase.util.ElasticEmail;
import com.derive.conbase.util.EmailSender;

@Controller
public class UserController {
	
	protected static final ConbaseLogger logger = ConbaseLogger.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;
	
	  
	
	@Transactional
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public  @ResponseBody ConbaseOutput  addUser(@RequestBody String requestString, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		final User user = mapper.readValue(requestString, User.class);
		
		Map<Boolean, List<String>> validateMap = user.validate();
		if (validateMap.get(true) != null) {
			User existingUser = userService.findUserByEmailId(user.getEmail());
			if (existingUser != null) {
				output.setSuccess(false);
				output.setMessages(Arrays.asList(new String[] {"Email already exists!"}));
			} else {
				String uuid = UUID.randomUUID().toString();
				user.setConfirmationIdentifier(uuid);
				
				userService.register(user);
				output.setSuccess(true);
				
				final StringBuffer message = new StringBuffer("");
				
				message.append("Dear ").append(user.getFullName()).append(",<p style='margin-top:10px;'>Congratulations! You have successfully registered for infraCMS. Please activate your account by clicking on the link below. If the link is not working, you can copy the below url address in your browser.</p>");
				message.append("<p style='margin-top:10px;'><a href='http://www.infracms.com/confirmuser?id="+user.getConfirmationIdentifier()+"'>Click here to activate</a></p>");
				message.append("<p>http://www.infracms.com/confirmuser?id=").append(user.getConfirmationIdentifier()).append("</p>");
				message.append("<p style='margin-top:10px;'>You can create a free trial project now and start managing your records online. For any queries or support, please feel free to contact +91-7895988251. We also provide one time data setup support also.</p>");
				message.append("<p style='margin-top:10px;margin-bottom:0px;'>Best Wishes,</p><p>infraCMS Team</p>");
				
				
				new Thread(new Runnable() {
				    public void run() {
				    	try {
							//new EmailSender().sendMail(new String[] {user.getEmail()}, "Welcome to infraCMS!!",message.toString());
				    		//ElasticEmail.sendElasticEmail("admin@infracms.com", "infraCMS - admin", "Welcome to infraCMS!!", message.toString(), user.getEmail());
						} catch (Exception e) {
							logger.error("Error occured while sending email to user " + user.getEmail());
						}
				    }
				}).start();
				
			}
		} else {
			output.setSuccess(false);
			output.setMessages(validateMap.get(false));
		}
		return output;
	}
	
	@Transactional
	@RequestMapping(value = "/confirmuser", method = RequestMethod.GET)
	public String confirmUser(@RequestParam String id, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User existingUser = userService.getUserByConfirmationIdentifier(id);
		if (existingUser != null) {
			userService.confirmUser(existingUser.getId());
		} 
		return "userConfirm";
	}
	
	@Transactional
	@RequestMapping(value = "/changeCurrentProject", method = RequestMethod.POST)
	public @ResponseBody ConbaseOutput changeCurrentProject (@RequestParam Long projectId) {
		ConbaseOutput output = new ConbaseOutput();
		com.derive.conbase.security.User user = (com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Project project = projectService.getProjectById(projectId);
		user.setCurrentProject(project);
		output.setSuccess(true);
		return output;
	}
}