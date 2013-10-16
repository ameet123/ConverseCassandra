#wget --no-cookies --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com" http://download.oracle.com/otn-pub/java/jdk/6u41-b02/jdk-6u41-linux-x64.bin
#mv jdk* jdk-6u41-linux-x64.bin
#chmod 700 jdk-6u41-linux-x64.bin
#./jdk-6u41-linux-x64.bin
export JAVA_HOME=`pwd`/jdk1.6.0_41
export PATH=$JAVA_HOME/bin:$PATH
echo "JDK Installed in local directory: `which java`"
java -version

