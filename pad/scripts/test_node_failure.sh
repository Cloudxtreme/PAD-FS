#! /bin/bash
# Author: Dario Balinzo
# Testing Node Failure

#deploy a filesystem made up by 10 nodes, clean the old storages if present
for i in {1..10}
do
	rm -fr "/tmp/node"$i"_storage"
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name$i &>log/name$i.txt  &
done

#wait for ending of setup
sleep 2 

#using the file system without fault

java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2002 pippo asd
java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2002 asd asd
java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2002 pad asd
java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2001 pluto asd
java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2003 pluto1 asd


#printing all the values in the storage
for i in {1..10}
do
	echo "--------------------------------------------"
	echo "Storage"$i
	ls  "/tmp/node"$i"_storage/Storage"
	echo "Replica"$i
	ls  "/tmp/node"$i"_storage/Replica"
	echo "Processing"$i
	ls  "/tmp/node"$i"_storage/Processing"
	echo "--------------------------------------------"
done


#killing all matching process
pkill -f pad-0.0.1-SNAPSHOT 