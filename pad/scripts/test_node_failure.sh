#! /bin/bash
# Author: Dario Balinzo
# Testing Node Failure

#killing all matching process (already running instance are closed)
pkill -f pad-0.0.1-SNAPSHOT 

#deploy a filesystem made up by 10 nodes, clean the old storages if present
echo "PAD-TEST: cleaning folders and starting nodes"
for i in {1..10}
do
	rm -fr "/tmp/node"$i"_storage"
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name$i &>log/name$i.txt  &
done

#wait for ending of setup
echo "PAD-TEST: waiting nodes startup"
sleep 4 

#using the file system without fault
echo "PAD-TEST: using the file system  (putting stuff) without fault"
java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2000 pippo asd
sleep 2


echo "PAD-TEST: killing node name3, the node is responsable of key pippo"
pkill -f name3
echo "PAD-TEST: is possible to get pippo from one of the replica:"
out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar get //localhost:2007 pippo)
expected="PAD-CLIENT: get pippo = asd"
echo $out
if [ "$expected" != "$out" ]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
echo "PAD-TEST: is possible to update pippo:"
out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2007 pippo newvalue)
expected="PAD-CLIENT: put pippo , newvalue"
echo $out
if [ "$expected" != "$out" ]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
sleep 3
echo "PAD-TEST: Storage3 with old value of pippo"
out=$(ls  "/tmp/node3_storage/Storage")
expected="pippo.1v0v0v"
echo $out
if [ "$expected" != "$out" ]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
echo "PAD-TEST: Replicas with new value of pippo"
out=$(ls  "/tmp/node4_storage/Replica")
expected="pippo.1v1v0v"
echo $out
if [ "$expected" != "$out" ]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
ls  "/tmp/node5_storage/Replica"
sleep 3
echo "PAD-TEST: restarting node 3"
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name3 &>log/name3.txt  &
echo "PAD-TEST waiting synch (every 10sec there is a trial of synch)"
sleep 15 #node startup and synch
echo "PAD-TEST: Storage3 with updated value of pippo"
ls  "/tmp/node3_storage/Storage"
echo "PAD-TEST: getting last value of pippo from node3"
out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar get //localhost:2004 pippo)
expected="PAD-CLIENT: get pippo = newvalue"
echo $out
if [ "$expected" != "$out" ]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
echo ""
echo ""
echo ""
echo "TEST1 OK"
echo ""
echo ""
echo ""

echo "PAD-TEST: killing node name4, the node is replica node for key pippo"
pkill -f name4
echo "PAD-TEST: is possible to get pippo from one of the responsabile node:"
out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar get //localhost:2007 pippo)
expected="PAD-CLIENT: get pippo = newvalue"
echo $out
if [ "$expected" != "$out" ]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
echo "PAD-TEST: is possible to update pippo:"
out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2007 pippo newvalue1)
expected="PAD-CLIENT: put pippo , newvalue1"
echo $out
if [ "$expected" != "$out" ]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
sleep 3
echo "PAD-TEST: Replica4 with old value of pippo"
out=$(ls  "/tmp/node4_storage/Replica")
expected="pippo.1v1v0v"
echo $out
if [ "$expected" != "$out" ]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
echo "PAD-TEST: Storages with new value of pippo"
out=$(ls  "/tmp/node3_storage/Storage")
expected="pippo.2v1v0v"
echo $out
if [ "$expected" != "$out" ]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
ls  "/tmp/node5_storage/Replica"
sleep 3
echo "PAD-TEST: restarting node 4"
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name4 &>log/name4.txt  &
echo "PAD-TEST waiting synch (every 10sec there is a trial of synch)"
sleep 14 #node startup and synch
echo "PAD-TEST: Storage3 with updated value of pippo"
ls  "/tmp/node3_storage/Storage"
echo "PAD-TEST: getting last value of pippo from node3"
out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar get //localhost:2004 pippo)
expected="PAD-CLIENT: get pippo = newvalue1"
echo $out
if [ "$expected" != "$out" ]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
echo ""
echo ""
echo ""
echo "TEST2 OK"
echo ""
echo ""
echo ""



#killing all matching process
pkill -f pad-0.0.1-SNAPSHOT 