package com.derive.conbase.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SessionManagementInterceptor extends HandlerInterceptorAdapter {
	 
	private static final Logger logger = Logger.getLogger(SessionManagementInterceptor.class);

	private static final String SESSION_VALID = "SESSION_VALID";

	/**
	 * 
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
		HttpSession session = request.getSession(false);
		boolean isSessionValid = true;
		if ( session == null ) {
			isSessionValid = false;
		}
		request.setAttribute(SESSION_VALID, isSessionValid);
		return true;
	}
 
	//after the handler is executed
	public void postHandle(HttpServletRequest request, HttpServletResponse response, 
		Object handler, ModelAndView modelAndView) throws Exception {
		
		boolean isSessionvalid = (Boolean)request.getAttribute(SESSION_VALID);
		logger.debug("isSessionvalid = " + isSessionvalid + " for the handler = " + handler);

		if ( isSessionvalid ) {
			String userAddress = request.getRemoteAddr();
			logger.debug("userAddress  = " + userAddress);
			
		}else {
			//redirect to the session timeout page
		}
	}

}
