package com.derive.conbase.security;

import java.util.TimeZone;

public class ConbaseContextLoader extends
		org.springframework.web.context.ContextLoaderListener {

	public ConbaseContextLoader() {
		System.out.println("Starting application..");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
}
