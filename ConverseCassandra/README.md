ConverseCass
============

<h2>Install Cassandra: </h2>
<em>short message storage application around Cassandra for a demo</em>

<p>Check that sun java 1.6.* or later is installed
<code>java –version</code>
</p>

go to a directory on the local machine and run following<br>
<code>git clone https://github.com/ameet123/ConverseCassandra.git</code><br>
<br>
<i>Optional:<br>
To install Oracle JDK, you may run the script<br>
<code>sh installJDK.sh</code>
</i>
<br>
<br>
To download and install Cassandra, Run the shell script<br>
<pre><code>sh setupCassandra.sh `pwd`</code></pre>
That's it, This should install and start Cassandra server on your local machine<br>
you can check that by<br>
<code>${CASSANDRA_HOME}/bin/nodetool ring</code>

<h2>Install CassandraConverse Application: </h2>
</strong></p>

<h3>Execution</h3>

cd into ConverseCassandra and simply run,
<code>ant</code><br>
This will build ConverseCass.jar under bin/
<p> to run,
Assuming that Cassandra instance is up, <br>
<code>java -jar bin/ConverseCass.jar</code>
</p>

<h3>Schema Creation</h3>
schema for the project can be created as follows<br>
<code>Cassandra/dsc-cassandra-1.2.3/bin/cassandra-cli -f createSchema.cli</code><br>
<br><br>
The details of schema creation are as follows - 
<br>
<b><i> Following are already done by the above script</b></i>
<br>
<p>1. connect to cassandra-cli from the server</p>
<code>${CASSANDRA_HOME}/bin/cassandra-cli connect localhost/9160</code>
<p>2. Schema Creation:</p>
<pre><code>create keyspace training_ks
  with placement_strategy = 'SimpleStrategy'
  and strategy_options = {replication_factor : 1}
  and durable_writes = true;
</code></pre>
<pre><code>create column family chat_conversation_comp
with column_type = 'Standard'
and comparator = 'CompositeType(TimeUUIDType,UTF8Type)'
and default_validation_class = 'UTF8Type'
and key_validation_class = 'UTF8Type'
</code></pre>
