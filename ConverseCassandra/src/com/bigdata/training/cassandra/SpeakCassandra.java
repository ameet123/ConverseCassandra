package com.bigdata.training.cassandra;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.log4j.Logger;
import org.scale7.cassandra.pelops.Bytes;
import org.scale7.cassandra.pelops.Cluster;
import org.scale7.cassandra.pelops.ColumnFamilyManager;
import org.scale7.cassandra.pelops.Mutator;
import org.scale7.cassandra.pelops.Pelops;
import org.scale7.cassandra.pelops.Selector;
import org.scale7.cassandra.pelops.types.CompositeType;

import com.bigdata.training.chatbox.ChatBox;
/**
 * Main class for cassandra interactions
 * loads lines from a book as messages into cassandra database
 * and via a swing application demonstrates the capability of cassandra
 * to fetch records in a chronologically sorted fashion
 * @author ameet
 *
 */
public class SpeakCassandra {
	/**
	 * KeySpace name stored for further operations
	 */
	private String KS;
	/**
	 * Pool name is used only for Pelops operations and
	 * to tie to a particular instance of operations
	 */
	private String POOL_NAME = "artPool";
	/**
	 * mutator object for performing writes
	 */
	private Mutator ksMutator;
	/**
	 * selector object for reads
	 */
	private Selector ksSelector;
	/**
	 * a cluster object to operate on a specific cassandra cluster
	 */
	private Cluster artCluster;
	/**
	 * record as checkpoint the start column to be used in slice range to be used
	 * in the slice predicate. This will be updated by every call of getNext()
	 */
	private byte[] startColumn;
	/**
	 * finish column, this will mostly be empty byte array, needed by slice range
	 */
	private byte[] finishColumn;
	/**
	 * to access logging framework
	 */
	private Logger log;

	/**
	 * constructor creates the "cluster" and pool
	 * and then using this creates "mutator" & "selector"
	 * this is done for the "keyspace" name passed as parameter
	 * @param keySpace
	 */
	public SpeakCassandra(String keySpace){
		log = Logger.getLogger(SpeakCassandra.class.getName());
		
		KS = keySpace;
		
		artCluster = new Cluster(Statics.NODE, Statics.PORT);		
		// create a pool based on the cluster and keyspace passed
		Pelops.addPool(POOL_NAME, artCluster, KS);	
		// get a mutator, i.e. changer
		ksMutator = Pelops.createMutator(POOL_NAME);
		// get a selector, (self-explanatory :) )
		ksSelector = Pelops.createSelector(POOL_NAME);
		// initialize the start Column and finish column
		startColumn = new byte[] {};
		finishColumn = new byte[] {};
	}
	/**
	 * using ColumnFamilyManager truncates the table
	 * @param columnFamily	String name
	 */
	public void deleteColumnFamily(String columnFamily){
		ColumnFamilyManager CFManager = new  ColumnFamilyManager(artCluster, KS);
		try {
			CFManager.truncateColumnFamily(columnFamily);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * takes a file and persists it to specified CF with requested # of lines
	 * This method uses a composite Type column family
	 * the definition is:
	 * 			<i>create column family chat_conversation_comp
  	 * 			with column_type = 'Standard'
  	 * 			and comparator = 'CompositeType(TimeUUIDType,UTF8Type)'
  	 * 			and default_validation_class = 'UTF8Type'
  	 * 			and key_validation_class = 'UTF8Type'</i>
	 * @param columnFamily
	 * @param chatScript the file containing the conversations.
	 * @param limit	how many rows to write
	 */
	public void persistConversationsToComp(String columnFamily, String chatScript, int limit){
		try {	
			/**
			 * integer counter used for limiting the number of rows based on limit
			 * number passed
			 */
			int i = 1;
			// first get the whole file as an Array
			ArrayList<String> chatMessages = ReadData.readWholeFile(chatScript);
			// now iterate through the array and process messages
			for (String message: chatMessages){
				// get the random key which specifies the participants between whom the 
				// conversation is taking place
				String rowKey = Utility.createRandomKey();
				// get the talker
				String talker = Utility.getTalker(rowKey);				

				// format message, gets it into 140 characters
				message = Utility.formatMessage(message);
				
				/**
				 * build the composite Column comprised of two parts
				 * Builder class is nested inside CompositeType
				 * Builder class enables the user to "build" the composite type
				 * each component in the composite column is made up of: \<length of value>\<value\><end-of-component>
				 * end-of-component byte is 0.
				 * Based on this: \<component1\>\<component2\>
				 * each add method returns a "build" object and the "build" method returns
				 * Bytes format composite Type.
				 */
				CompositeType.Builder compBuilder = CompositeType.Builder.newBuilder();	
				// every "add<>" method returns a Builder object, so we just go on
				// chaining the columns as components in the final Composite Type.
				// The final build actually "formats" the components with end-of-component bytes and 
				// at the start prefixing the length of value
				Bytes compCol = compBuilder.addUuid(Utility.getUniqueTimeUUIDinMillis()).addUTF8(talker).build();
				Column myCol = ksMutator.newColumn(compCol, message);
				
				// Now we are ready to write the column
				ksMutator.writeColumn(columnFamily, rowKey, myCol);
				// actually execute the command
				ksMutator.execute(Statics.CL);
				if (i == limit){
					// stored the requested # of rows, get out
					break;
				}
				i++;	// increment the counter
			}
		} catch (FileNotFoundException e) {		
			log.error("Error writing to CF:"+e);
		}		
	}	
	/**
	 * write conversations to regular column
	 * @param columnFamily
	 * @param chatScript
	 * @param limit
	 */
	public void persistConversations(String columnFamily, String chatScript, int limit){
		try {			
			int i = 1;
			// first get the whole file as an Array
			ArrayList<String> chatMessages = ReadData.readWholeFile(chatScript);
			// now iterate through the array and process messages
			for (String message: chatMessages){
				// get the random key which specifies the participants between whom the 
				// conversation is taking place
				String rowKey = Utility.createRandomKey();				

				// format message
				message = Utility.formatMessage(message);

				// first create the column for message
				// name of the column is TimeUUID, value = message
				Column myCol = ksMutator.newColumn(Utility.getTimeUUIDinBytes(), message);
				
				// Now we are ready to write the column
				ksMutator.writeColumn(columnFamily, rowKey, myCol);
				// actually execute the command
				ksMutator.execute(Statics.CL);
				if (i == limit){
					// stored the requested # of rows, get out
					break;
				}
				i++;	// increment the counter
			}
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		}		
	}	
	/**
	 * read from a regular column family.
	 * @param columnFamily
	 * @param from
	 * @param to
	 */
	public void getConversation(String columnFamily, String from, String to){		
		// construct the row key
		String rowKey = from + ":" + to;
		// read data
		int columnCount = ksSelector.getColumnCount(columnFamily, rowKey, Statics.CL);
		System.out.println("# of Messages between:"+ from + "<=>"+ to+ ":" + columnCount);
		List<Column> colList = ksSelector.getColumnsFromRow(columnFamily, rowKey, false, Statics.CL);
		for (Column c:colList){
			System.out.println(  Selector.getColumnStringValue(c));
		}
	}
	/**
	 * get one message at a time using slice predicate which uses
	 * slice range. 
	 * @param columnFamily
	 * @param from
	 * @param to
	 * @return	OneMessage class with 3 vars: msg, time, talker
	 */
	public OneMessage getNext(String columnFamily, String from, String to){
		// construct the row key by making an ordered pair
		String rowKey = Utility.getOrderedPair(from, to);
		/** 
		 * since we only want to get one column, create a predicate
		 * Predicate just means something that is common to a set of rows - such as a "where" clause
		 */		
		SlicePredicate colPredicate = new SlicePredicate();
		/**
		 * in order to define correctly the predicate, we need to set the range of "records"/columns
		 * to return. by itself, the slice predicate is more of a framework/skeleton 
		 * which can receive a slice of records
		 */
		SliceRange colRange = new SliceRange();
		// now we can set properties of this SliceRange
		// The count of columns is set to 2, so that we can get the next column
		// stored in the start column.
		colRange.setCount(2).setStart(startColumn).setFinish(finishColumn).setReversed(false);
		colPredicate.setSlice_range(colRange);
		
		/**
		 * to store composite columns parsed
		 */
		List<byte[]> bytes = null;
		String talker = null;
		String messageTime = null;
		String chatMsg = null;
		// get one column from the rowKey
		List<Column> colList  = ksSelector.getColumnsFromRow(columnFamily, rowKey, colPredicate, Statics.CL); 
		for(int i=0;i<colList.size();i++){   
			Column c = colList.get(i);
			// record the column for next start
			// and don't print this column
			if (i==colList.size()-1){
				startColumn = c.getName();
				continue;
			}			
			bytes = CompositeType.parse(c.getName());
			// this talker can be compared to "from" if needed.
			talker = Bytes.toUTF8(bytes.get(1));			
			messageTime = Utility.getByteTimeToString(Bytes.fromByteArray(bytes.get(0)));
			chatMsg = Selector.getColumnStringValue(c).trim();
			// debug
			log.debug("raw:"+chatMsg);
			log.debug("HTML:"+"<html>"+chatMsg.trim()+"<br/></html>");
		}
		// Now get the instance of class for storage of all these 3 attributes
		OneMessage output = new OneMessage();
		output.setMessage(chatMsg);
		output.setMessageTime(messageTime);
		output.setTalker(talker);
		
		return output;
	}
	/**
	 * fetch columns for a particular conversations
	 * go through the list of composite columns and print the results
	 * @param columnFamily
	 * @param from
	 * @param to
	 */
	public void getCompConversation(String columnFamily, String from, String to){
		// construct the row key by making an ordered pair
		String rowKey = Utility.getOrderedPair(from, to);
		
		// read data
		int columnCount = ksSelector.getColumnCount(columnFamily, rowKey, Statics.CL);
		log.debug("# of Messages between:"+ from + "<=>"+ to+ ":" + columnCount);
		
		/**
		 * to store composite columns parsed
		 */
		List<byte[]> bytes = null;
		String talker = null;
		String messageTime = null;
		
		List<Column> colList = ksSelector.getColumnsFromRow(columnFamily, rowKey, false, Statics.CL);
		for (Column c:colList){
			bytes = CompositeType.parse(c.getName());
			// this talker can be compared to "from" if needed.
			talker = Bytes.toUTF8(bytes.get(1));			
			messageTime = Utility.getByteTimeToString(Bytes.fromByteArray(bytes.get(0)));
			// debug
			log.debug("NEXT:talker "+talker+" said:"+ Selector.getColumnStringValue(c).trim()+"\n\t\t\t\t\t\t -- @ "+messageTime);
		}
	}
	/**
	 * shut down the connection to the cluster
	 */
	public void close(){
		Pelops.shutdown();
	}

	/**
	 * 1. create an instance of SpeakCassandra for a particular KeySpace
	 * 2. drop and recreate column family
	 * 3. persist data to composite CF
	 * 4. read data from composite CF
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		SpeakCassandra cs = new SpeakCassandra(Statics.KEYSPACE);

		cs.log.info("starting "+cs.getClass().getName()+" ...");		 
		/**
		 * to truncate table
		 * cs.deleteColumnFamily(Statics.CONVERSATION_CF);
		 */	
		cs.deleteColumnFamily(Statics.CONVERSATION_CF);
		/**
		 * to store data to table
		 * cs.persistConversationsToComp(Statics.CONVERSATION_CF, Statics.DATAFILE, 300);
		 */
		cs.persistConversationsToComp(Statics.CONVERSATION_CF, Statics.DATAFILE, Statics.ROWLIMIT);
		/**
		 * get all records of the conversation
		 * cs.getCompConversation(Statics.CONVERSATION_CF, "jane", "maria");
		 */
		

		// get one message at a time
		/**
		cs.getNext(Statics.CONVERSATION_CF, "jane", "maria");
		cs.getNext(Statics.CONVERSATION_CF, "jane", "maria");
		cs.getNext(Statics.CONVERSATION_CF, "jane", "maria");
		cs.getNext(Statics.CONVERSATION_CF, "jane", "maria");
		**/
		cs.close();
		new ChatBox();
		
	}
}