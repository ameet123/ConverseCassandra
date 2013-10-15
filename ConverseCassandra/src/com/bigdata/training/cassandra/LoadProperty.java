package com.bigdata.training.cassandra;

import java.io.IOException;
import java.util.Properties;

/**
 * load a property file "config.properties" from the classpath, catch errors
 * @author ameet
 *
 */
public class LoadProperty {	
	/**
	 * read property file "config.properties" and return a Properties object
	 * @return Properties
	 */
	public static Properties getProperties()  {
		Properties prop = new Properties();
		try {
			prop.load(LoadProperty.class.getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			System.out.println("Some Error");
		}
		return prop;
	}	
}
