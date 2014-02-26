package com.derive.conbase.util;

import java.security.Security;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.derive.conbase.logging.ConbaseLogger;

public class EmailSender {
	
	protected static ConbaseLogger logger = ConbaseLogger.getLogger(EmailSender.class);
	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_PORT = "465";
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private static final String SMTP_AUTH_USER = "agabhi@gmail.com";
	private static final String SMTP_AUTH_PWD  = "Abh1#123";

	private static final String emailFromAddress = "agabhi@gmail.com";
	private static final String displayName = "Conbase";

	public EmailSender() {
	}

	public void sendMail(String[] recipientList,String emailSubjectTxt,
			String emailMsgTxt) throws Exception {
		logger.debug("-------------------- processTask inside sendMail");
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		
		sendSSLMessage( recipientList, emailSubjectTxt, emailMsgTxt, emailFromAddress);
		logger.debug("############ Sucessfully Sent mail to All Users");
	}
	
	private void sendSSLMessage(String recipients[], String subject,
			String message, String from) throws Exception {
		boolean debug = true;
			Session session = getSession();
			session.setDebug(debug);

			logger.debug("############ session" + session);
			Message msg = new MimeMessage(session);
			InternetAddress addressFrom = new InternetAddress(from);
			msg.setFrom(new InternetAddress(emailFromAddress, displayName));

			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			// Setting the Subject and Content Type
			msg.setSubject(subject);
			msg.setContent(message, "text/html");
			logger.debug("############ created the message");
			Transport.send(msg);
	}

	private Session getSession() {
		Authenticator authenticator = new Authenticator();

		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getInstance(props, authenticator);
		return session;
	}

	private class Authenticator extends javax.mail.Authenticator {
		private PasswordAuthentication authentication;

		public Authenticator() {
			String username = SMTP_AUTH_USER;
			String password = SMTP_AUTH_PWD;
			authentication = new PasswordAuthentication(username, password);
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}
}
