package com.bigdata.training.cassandra;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.scale7.cassandra.pelops.Bytes;
import org.scale7.cassandra.pelops.UuidHelper;

import com.eaio.uuid.UUIDGen;
/**
 * utility methods
 * @author ameet
 *
 */
public class Utility {
	private static SimpleDateFormat milliFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public static Bytes getTimeUUIDinBytes(){
		return Bytes.fromUuid(getUniqueTimeUUIDinMillis());
	}
	
	public static java.util.UUID getUniqueTimeUUIDinMillis() {
	    return new java.util.UUID(UUIDGen.newTime(), UUIDGen.getClockSeqAndNode());
	}

	/**
	 * generates a random talker for the message to originate from
	 * @param key
	 * @return String
	 */
	public static String getTalker(String key){
		String[] talkerArray =  key.split(":");
		Random randomGenerator = new Random(); 
		int index = randomGenerator.nextInt(2);
		return talkerArray[index];
	}
	/**
	 * add HTML format time to message for display
	 * @param s
	 * @return String
	 */
	public static String formatTimeInHTML(String s){
		s = "<p style='color:red'> @ " + s + "</p>";
		return s;
	}
	/**
	 * format message with 140 characters in a justified fashion
	 * @param message
	 * @param signature
	 * @return String
	 */
	public static String formatMessage(String message, String signature){
		// format the message to 140 characters
		message = String.format("%-140s", message);
		// format the signature to 140 characters, right justified
		signature = String.format("%140s", signature);
		// now join them with a carriage return in middle
		return message + "\n" + signature ;
	}
	public static String formatMessage(String message){
		return String.format("%-140s", message);
	}
	public static String getByteTimeToString(Bytes timeInBytes){
		return  milliFmt.format( Utility.cassandraToDate(timeInBytes.toByteArray()) );
	}
	public static String getSignature(Bytes msgTimeBytes, String rowKey){
		// now from this, just for display, we want the millisecond time
		Date msgDate = Utility.cassandraToDate(msgTimeBytes.toByteArray());
		String msgTime = milliFmt.format(msgDate);
		// also from the key, get the first part, which is the talker
		String talker = rowKey.replaceAll(":.*", "");
		// pad it to a fixed length
		talker = String.format("%-10s", talker);
		// form a signature based on talker and message time
		String signature = " -- thus spake "+talker+" @ "+msgTime+".";
		return signature;
	}
	/**
	 * convert byte array from Cassandra into instance of Bytes
	 * which is then converted to java UUID which is then
	 * converted using UuidHelper to millisecond which is finally
	 * converted to Date object
	 * @param casCol
	 * @return Date
	 */
	public static Date cassandraToDate(byte[] casCol){
		Bytes bs = Bytes.fromByteArray(casCol);
		java.util.UUID colUID = bs.toUuid();
		return new Date(UuidHelper.millisFromTimeUuid(colUID));
	}
	/**
	 * To generate a random key comprised of two conversationists
	 * We have to keep trying till the two parts are different, just in case we 
	 * get the same person in two random calls. 
	 * @return String	first:second
	 */
	public static String createRandomKey(){
		String firstPart = Statics.getRandomUser();
		String secondPart = Statics.getRandomUser();
		while (firstPart.equals(secondPart)){
			secondPart = Statics.getRandomUser();
		}
		// now sort the strings so that we get the same row key regardless of
		// who is talking to whom
		// we don't need to check for equal strings since that was done above
		
		return getOrderedPair(firstPart, secondPart);
	}
	/**
	 * in order to get a meaningful key, generate an ordered pair out
	 * of 2 talkers so the messages belonging to them will 
	 * always go to this same row key
	 * @param s1
	 * @param s2
	 * @return String
	 */
	public static String getOrderedPair(String s1, String s2){
		String pair;
		int comparison = s1.compareToIgnoreCase(s2);
		if (comparison > 0){
			// first part is later in the English alphabet
			pair = s2 + ":" + s1;
		} else {
			// first part is later in the English alphabet
			pair = s1 + ":" + s2;
		}
		return pair;
	}
}