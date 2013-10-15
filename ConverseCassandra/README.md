ConverseCass
============

<h2>Install Cassandra: </h2>
<em>short message storage application around Cassandra for a demo</em>

<h3>Install Cassandra on Ubuntu 12.04 (Precise) Server</h3>

<p><strong>1.</strong>Check that sun java 1.6.* or later is installed
<code>java –version</code>
</p>
if not installed, follow the instructions here to install java first (note that open-jdk doesn’t work well with Cassandra)

<p>2. Create a folder called “Cassandra”, and cd to it</p>

<pre><code>
mkdir Cassandra<br>
cd Cassandra
</code></pre>

<p>3. Download Cassandra from Datastax</p>


<code>wget “http://downloads.datastax.com/community/dsc-cassandra-1.2.3-bin.tar.gz”</code>

<p>4.	Untar dsc-cassandra-1.2.3-bin.tar.gz file</p>
<code>tar –xzvf dsc-cassandra-1.2.3-bin.tar.gz</code>
<p>5.	Use the following Python script to generate a list of tokens (one for each node). Replace X with the number of nodes.</p>
<code>python -c 'print [((2**64 / X) * i) - 2**63 for i in range(X)]'</code>
<p>6. Note all the values down somewhere</p>
<p>7. Cd to $CASSANDRA_HOME/conf and edit cassandra.yaml  (if $CASSANDRA_HOME hasn’t been set   it using “export CASSANDRA_HOME=/path/to/cassandra/install”)</p>
<p>a.       Modify initial_token: <one of the values from python script output> </p>
<p>b.      Modify "seed_provider":   "seeds": < comma separated IP addresses of 2 of the 5 machines </p>
<p>c.        Modify "listen_address": <IP address of the machine> (can use command “if config” to get this information)</p>
<p>d.       Modify "rpc_address":  0.0.0.0 </p>
<p>10.   Run cassandra as $CASSANDRA_HOME/bin/cassandra -p <path to PID file></p>
<p>11.   Start it from the seed nodes first and then go to the other nodes </p>
<p>12. Once all nodes have been started, run $CASSANDRA_HOME/bin/nodetool ring to check cluster status</p>

<em>Default partitioning in 1.2.3 is Murmur3 which uses tokens in the range of 2^64</em>
 
<h2>Install CassandraConverse Application: </h2>
</strong></p>
<p>1. connect to cassandra-cli from the server</p>
<code><cassandra-install>/bin/cassandra-cli connect localhost/9160</code>
<p>2. Schema Creation:</p>
<pre><code>
 create keyspace training_ks<br>
  with placement_strategy = 'SimpleStrategy' <br>
  and strategy_options = {replication_factor : 1}<br>
  and durable_writes = true;<br>
</code></pre>
<pre><code>
create column family chat_conversation_comp<br>
with column_type = 'Standard'<br>
and comparator = 'CompositeType(TimeUUIDType,UTF8Type)'<br>
and default_validation_class = 'UTF8Type'<br>
and key_validation_class = 'UTF8Type'<br>
</code></pre>

<p>modify the config.properties with the IP address of appropriate Cassandra node</p>


<code>NODE=localhost</code>
<h3>Execution</h3>
<p> go to a directory on the local machine and run following
<code>git clone https://github.com/ameet123/ConverseCassandra.git</code>
All the files required will be downloaded
then cd into ConverseCassandra and simply run,
<code>ant</code>
This will build ConverseCass.jar under bin/
</p>
<p> to run,
Assuming that Cassandra instance is up
<code>java -jar bin/ConverseCass.jar</code>
</p>