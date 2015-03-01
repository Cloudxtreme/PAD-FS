#! /bin/bash
# Author: Dario Balinzo
# Testing many insertion

#deploy a filesystem made up by 10 nodes, clean the old storages if present
echo "PAD-TEST: cleaning folders and starting nodes"
for i in {1..10}
do
	rm -fr "/tmp/node"$i"_storage"
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name$i &>log/name$i.txt  &
done

#wait for ending of setup
echo "PAD-TEST: waiting nodes startup"
sleep 3 

#using the file system without fault
echo "PAD-TEST: using the file system  (putting stuff) without fault"
for i in {1..100}
do
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2001 pad$i asd
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2006 pippo$i asd
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2008 abc$i asd
done
sleep 15

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

echo "PAD-TEST: insertion"

for i in {1..100}
do
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2001 pad$i asd
done
sleep 15

echo "PAD-TEST: simulating partition A=[1,2,3] stopped, B=[4,5,6,7,8,9,10] can go"
pkill -f pad-0.0.1-SNAPSHOT 
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name4 &>log/name4.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name5 &>log/name5.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name6 &>log/name6.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name7 &>log/name7.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name8 &>log/name8.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name9 &>log/name9.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name10 &>log/name10.txt  &
sleep 4

for i in {1..100}
do
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2008 abc$i asd
done
sleep 15
#killing all matching process
pkill -f pad-0.0.1-SNAPSHOT 

echo "PAD-TEST: simulating partition resolving"
for i in {1..10}
do
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name$i &>log/name$i.txt  &
done

echo "PAD-TEST: waiting synch"
sleep 20
out=$(find /tmp/node1_storage/ -type f | wc -l)
expected=91
echo $out
if [[ $expected -ne $out ]]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
out=$(find /tmp/node2_storage/ -type f | wc -l)
expected=90
echo $out
if [[ $expected -ne $out ]]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
out=$(find /tmp/node3_storage/ -type f | wc -l)
expected=92
echo $out
if [[ $expected -ne $out ]]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
out=$(find /tmp/node4_storage/ -type f | wc -l)
expected=90
echo $out
if [[ $expected -ne $out ]]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
out=$(find /tmp/node5_storage/ -type f | wc -l)
expected=89
echo $out
if [[ $expected -ne $out ]]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
out=$(find /tmp/node6_storage/ -type f | wc -l)
expected=90
echo $out
if [[ $expected -ne $out ]]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
out=$(find /tmp/node7_storage/ -type f | wc -l)
expected=89
echo $out
if [[ $expected -ne $out ]]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
out=$(find /tmp/node8_storage/ -type f | wc -l)
expected=90
echo $out
if [[ $expected -ne $out ]]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
out=$(find /tmp/node9_storage/ -type f | wc -l)
expected=91
echo $out
if [[ $expected -ne $out ]]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi
out=$(find /tmp/node10_storage/ -type f | wc -l)
expected=95
echo $out
if [[ $expected -ne $out ]]; then
	#killing all matching process
	pkill -f pad-0.0.1-SNAPSHOT
	echo "PAD-TEST: FAILURE EXPECTED:"
	echo $expected
	echo "OBTAINED:"
	echo $out
	exit 1
fi


pkill -f pad-0.0.1-SNAPSHOT 
for i in {1..10}
do
	rm -fr "/tmp/node"$i"_storage"
done


echo ""
echo ""
echo ""
echo "TEST 5 OK"
echo ""
echo ""
echo ""

