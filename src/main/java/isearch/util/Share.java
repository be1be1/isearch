package isearch.util;

import java.io.IOException;
import java.util.Properties;

public class Share {
	
	final static String PROPERTY_FILE = "project.properties";
	
	public static String getProjectProperty(String key) throws IOException{
		Properties props = new Properties();
		props.load(Share.class.getClassLoader().getResourceAsStream(PROPERTY_FILE));
		String value = props.getProperty(key);
		return value;
	}
	
}
