package com.derive.conbase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.derive.conbase.dao.UserDAO;
import com.derive.conbase.model.User;

@Service(value= "userService")
@Transactional
public class UserService
{
	@Autowired
	private UserDAO userDAO;
	
	public void register(User user) {
		userDAO.register(user);
	}
	
	public User findUserByEmailId(String emailId) {
		return userDAO.findUserByEmailId(emailId);
	}
	
	public User getUserByConfirmationIdentifier(String confirmationIdentifier) {
		return userDAO.findUserByConfirmationIdentifier(confirmationIdentifier);
	}
	
	public void confirmUser(Long userId) {
		userDAO.confirmUser(userId);
	}
}