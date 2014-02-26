package com.derive.conbase.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtil {
	private static Pattern pattern;

	static {
		pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	}

	/**
	 * The method is used to validate the email address
	 * 
	 * @param email
	 *            email address
	 * @return true on valid email,false on invalid email
	 */
	public static boolean validateEmail(String email) {
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
