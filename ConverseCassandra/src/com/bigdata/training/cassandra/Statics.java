package com.bigdata.training.cassandra;

import java.util.Properties;
import java.util.Random;

import org.apache.cassandra.thrift.ConsistencyLevel;

public class Statics {
	
	/** Get properties object and use it for necessary parameters **/
	public static final Properties myProps = LoadProperty.getProperties();
	
	static final String NODE =  myProps.getProperty("NODE");
	static final int PORT = Integer.parseInt(myProps.getProperty("PORT"));
	static final ConsistencyLevel CL = ConsistencyLevel.ONE;
	static final String DATAFILE = myProps.getProperty("DATAFILE"); 
	static final String USERS = myProps.getProperty("USERS");
	static final String[] USER_ARRAY = USERS.split(",");
	public static final String CONVERSATION_CF = myProps.getProperty("CONVERSATION_CF");
	public static final String KEYSPACE = myProps.getProperty("KEYSPACE");
	public static final int ROWLIMIT = Integer.parseInt(myProps.getProperty("ROWLIMIT"));
	/**
	 * Get a random user from user array
	 * @return String
	 */
	static String getRandomUser(){
		Random randomGenerator = new Random(); 
		int index = randomGenerator.nextInt(USER_ARRAY.length);
		return USER_ARRAY[index];
	}
}
