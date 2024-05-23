#!/bin/sh
export JAVA_HOME=/usr/local/jdk1.8.0_231
export PATH=$PATH:$JAVA_HOME/bin
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
echo 'tiros starting running...'
chmod 777 tiros_manage.jar
java -Dfile.encoding=utf-8 -jar -Dloader.path=resources,lib tiros_manage.jar > out.log 2>&1 &
