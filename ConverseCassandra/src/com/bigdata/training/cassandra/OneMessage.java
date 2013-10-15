package com.bigdata.training.cassandra;

/**
 * Class that is mainly a data structure
 * with 3 elements: message, time and talker
 * @author ameet
 *
 */
public class OneMessage {
	private String message;
	private String messageTime;
	private String talker;
	public void setMessage(String msg){
		this.message = msg;
	}
	public String getMessage(){
		return message;
	}
	public void setMessageTime(String msgTime){
		this.messageTime = msgTime;
	}
	public String getMessageTime(){
		return messageTime;
	}
	public void setTalker(String talker){
		this.talker = talker;
	}
	public String getTalker(){
		return talker;
	}	
}