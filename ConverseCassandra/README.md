ConverseCass
============

<h2>Install Cassandra: </h2>
<em>short message storage application around Cassandra for a demo</em>

<h3>Install Cassandra on Ubuntu 12.04 (Precise) Server</h3>

<p><strong>1.</strong>Check that sun java 1.6.* or later is installed
<code>java –version</code>
</p>
if not installed, follow the instructions here to install java first (note that open-jdk doesn’t work well with Cassandra)

go to a directory on the local machine and run following
<code>git clone https://github.com/ameet123/ConverseCassandra.git</code>
Run the shell script<br>
<code>sh setupCassandra.sh `pwd`</code>
That's it, This should install and start Cassandra server on your local machine<br>

<h2>Install CassandraConverse Application: </h2>
</strong></p>
<p>1. connect to cassandra-cli from the server</p>
<code><cassandra-install>/bin/cassandra-cli connect localhost/9160</code>
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

<p>modify the config.properties with the IP address of appropriate Cassandra node</p>


<code>NODE=localhost</code>
<h3>Execution</h3>

cd into ConverseCassandra and simply run,
<code>ant</code>
This will build ConverseCass.jar under bin/
</p>
<p> to run,
Assuming that Cassandra instance is up
<code>java -jar bin/ConverseCass.jar</code>
</p>