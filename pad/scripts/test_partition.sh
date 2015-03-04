#! /bin/bash
# Author: Dario Balinzo
# Testing Network partition

#deploy a filesystem made up by 10 nodes, clean the old storages if present
echo "PAD-TEST: cleaning folders and starting nodes"
for i in {1..10}
do
	rm -fr "/tmp/node"$i"_storage"
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name$i &>log/name$i.txt  &
done

#wait for ending of setup
echo "PAD-TEST: waiting nodes startup"
sleep 2 

#using the file system without fault
echo "PAD-TEST: using the file system  (putting stuff) without fault"
java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2000 pippo asd
java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2001 pad asd
sleep 1

#to create a network partition  A=[1,2,3] vs B=[4,5,6,7,8,9,10]
# first A is execute with B stopped
# then  B is execute and A stopped
# in the end the fault is "repaired" and all process are executed
# pad stored in 10
# pippo stored in 3

echo "PAD-TEST: simulating partition A=[1,2,3] can go, B=[4,5,6,7,8,9,10] stopped"
pkill -f name4
pkill -f name5
pkill -f name6
pkill -f name7
pkill -f name8
pkill -f name9
pkill -f name10

echo "PAD-TEST: is possible to update pippo:"
out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2000 pippo newvalue)
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
echo "PAD-TEST: is possible to update pad:"
out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2001 pad newvalue)
expected="PAD-CLIENT: put pad , newvalue"
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
sleep 1
echo "PAD-TEST: Storage3 with new value of pippo"
out=$(ls  "/tmp/node3_storage/Storage")
expected="pippo.2v0v0v"
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
echo "PAD-TEST: Replicas with old value of pippo"
out=$(ls  "/tmp/node4_storage/Replica")
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
echo "PAD-TEST: Replica1 with new value of pad"
out=$(ls  "/tmp/node1_storage/Replica")
expected="pad.1v1v0v"
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
echo "PAD-TEST: Storage10 with old value of pippo"
out=$(ls  "/tmp/node10_storage/Storage")
expected="pad.1v0v0v"
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
echo "PAD-TEST: simulating partition A=[1,2,3] stopped, B=[4,5,6,7,8,9,10] can go"
pkill -f pad-0.0.1-SNAPSHOT 
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name4 &>log/name4.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name5 &>log/name5.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name6 &>log/name6.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name7 &>log/name7.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name8 &>log/name8.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name9 &>log/name9.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name10 &>log/name10.txt  &
sleep 3

echo "PAD-TEST: is possible to update pad:"
out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2007 pad value)
expected="PAD-CLIENT: put pad , value"
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
#killing all matching process
pkill -f pad-0.0.1-SNAPSHOT 

echo "PAD-TEST: simulating partition resolving"
for i in {1..10}
do
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name$i &>log/name$i.txt  &
done

echo "PAD-TEST: waiting synch"
sleep 13
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

out=$(ls  -C "/tmp/node10_storage/Storage")
java -jar ../target/pad-0.0.1-SNAPSHOT.jar get //localhost:2004 pad > out
echo "PAD-CLIENT: get pad = value" > tmp1
echo "PAD-CLIENT: get pad = newvalue" >> tmp1
echo "PAD-CLIENT: get pad = newvalue" > tmp2
echo "PAD-CLIENT: get pad = value" >> tmp2

if  ! diff out tmp2 && ! diff out tmp1; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE: out file is not equals to tmp1 or tmp2"
	exit 1
fi

rm -f tmp1 tmp2 out

echo ""
echo ""
echo ""
echo "TEST 3 OK"
echo ""
echo ""
echo ""

echo "PAD-TEST: stopping node 10, responsabile of pad"
pkill -f name10
echo "PAD-TEST: trying to delete all versions of pad"
java -jar ../target/pad-0.0.1-SNAPSHOT.jar delete //localhost:2004 pad
sleep 1
echo "PAD-TEST: calling method is finished delete"

out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar finished //localhost:2004 pad)
expected="PAD-CLIENT: deleting completed false"
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
echo "PAD-TEST: restarting node 10 and waiting synch"
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name10 &>log/name10.txt  &
sleep 13
out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar finished //localhost:2000 pad)
expected="PAD-CLIENT: deleting completed true"
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
echo "TEST 4 OK"
echo ""
echo ""
echo ""


#killing all matching process
pkill -f pad-0.0.1-SNAPSHOT 