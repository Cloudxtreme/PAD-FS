#! /bin/bash

#start all the servers
for i in {1..10}
do
	rm -fr "/tmp/node"$i"_storage"
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name$i &>log/name$i.txt  &
done



