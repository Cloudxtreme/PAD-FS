#! /bin/bash

#start all the servers
for i in {1..7}
do
	rm -r "/tmp/node"$i"_storage"
	java -jar target/pad-0.0.1-SNAPSHOT.jar /Users/dariobalinzo/git/pad/src/config1.xml name$i &> name$i.txt  &
done



