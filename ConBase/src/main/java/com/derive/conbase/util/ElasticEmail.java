package com.derive.conbase.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ElasticEmail {

	private static String userName = "9de34372-27f4-443a-be2c-b6d3295e0ae4";
	private static String apiKey = "9de34372-27f4-443a-be2c-b6d3295e0ae4";

	public static String sendElasticEmail(String from, String fromName,
			String subject, String body, String to) throws Exception {

		// Construct the data
		String data = "userName=" + URLEncoder.encode(userName, "UTF-8");
		data += "&api_key=" + URLEncoder.encode(apiKey, "UTF-8");
		data += "&from=" + URLEncoder.encode(from, "UTF-8");
		data += "&from_name=" + URLEncoder.encode(fromName, "UTF-8");
		data += "&subject=" + URLEncoder.encode(subject, "UTF-8");
		data += "&body_html=" + URLEncoder.encode(body, "UTF-8");
		data += "&to=" + URLEncoder.encode(to, "UTF-8");

		// Send data
		URL url = new URL("https://api.elasticemail.com/mailer/send");
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String result = rd.readLine();
		wr.close();
		rd.close();

		return result;

	}

}
