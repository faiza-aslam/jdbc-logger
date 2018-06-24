package com.examples.logging.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class EventMessageProperties {
	
	private static Properties properties = new Properties();
	
	static {
		InputStream is = EventMessageProperties.class.getClassLoader().getResourceAsStream("event-log-message.properties");
		if(is != null) {
			try {
				properties.load(is);
			} catch (IOException e) {
				throw new RuntimeException("Unable to load event message properties file", e);
			}
		}
	}
	
	public static String getProperty(String key) {		
		if(key!=null){
			return properties.getProperty(key);
		} else 
			return "";
	}
	
	public static String getFormattedMessageByKey(String key, Object... args) {
		String value = getProperty(key);
		return MessageFormat.format(value, args);
	} 
}
