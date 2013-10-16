#!/bin/bash

TOP_DIR=$1
CASS_DISTRO=dsc-cassandra-1.2.3-bin.tar.gz
CASSANDRA_BASE=$TOP_DIR/Cassandra
CASSANDRA_BASE_STR=`echo $CASSANDRA_BASE|sed 's/\//\\\\\//g'` 
CASSANDRA_HOME=$CASSANDRA_BASE/dsc-cassandra-1.2.3
CASS_CONF=${CASSANDRA_HOME}/conf/cassandra.yaml
CASS_CONF_TMP=${CASSANDRA_HOME}/conf/cassandra.yaml.tmp
CASS_LOG=${CASSANDRA_HOME}/conf/log4j-server.properties
CASS_LOG_TMP=${CASSANDRA_HOME}/conf/log4j-server.properties.tmp
DATA_DIR_STR="\/var\/lib\/cassandra\/data"
COMMIT_DIR_STR="\/var\/lib\/cassandra\/commitlog"
CACHE_DIR_STR="\/var\/lib\/cassandra\/saved_caches"
CASS_LOG_STR="\/var\/log\/cassandra\/system.log"
CASS_PID=$CASSANDRA_BASE/logs/cass.pid
CASS_OUT=$CASSANDRA_BASE/logs/cassandra.out

if [ "${TOP_DIR}x" = "x" ]
then 
  echo "Usage: 	$0 <top level dir>"
  echo "	$0" '`pwd`'
  exit 1
fi

echo Cassandra will be set up in $TOP_DIR
mkdir $CASSANDRA_BASE
cd ${CASSANDRA_BASE}

echo "Downloading Cassandra 1.2.3"
wget http://downloads.datastax.com/community/${CASS_DISTRO}
echo "Extracting distribution"

tar -xzf ${CASS_DISTRO}

echo Creating directories for data, log, caches, commitlog
# create directories
mkdir $CASSANDRA_BASE/commitlog
mkdir $CASSANDRA_BASE/data
mkdir $CASSANDRA_BASE/saved_caches
mkdir $CASSANDRA_BASE/logs

echo Modifying configuration in cassandra.yaml
sed -e 's/^initial_token\:/initial_token\: 0/' \
    -e "s/${DATA_DIR_STR}/${CASSANDRA_BASE_STR}\/data/" \
    -e "s/${COMMIT_DIR_STR}/${CASSANDRA_BASE_STR}\/commitlog/" \
    -e "s/${CACHE_DIR_STR}/${CASSANDRA_BASE_STR}\/saved_caches/" \
 $CASS_CONF > $CASS_CONF_TMP
mv $CASS_CONF_TMP $CASS_CONF

echo Modify log4j-server.log to set appropriate log location
sed "s/${CASS_LOG_STR}/${CASSANDRA_BASE_STR}\/logs\/system.log/" $CASS_LOG > $CASS_LOG_TMP
mv $CASS_LOG_TMP $CASS_LOG

echo "**** Starting Cassandra ****"
$CASSANDRA_HOME/bin/cassandra -p ${CASS_PID} >$CASS_OUT 2>&1 &
