#! /bin/bash
# Author: Dario Balinzo
# Testing many insertion


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
sleep 5 

#using the file system without fault
echo "PAD-TEST: using the file system  (putting stuff) without fault"
for i in {1..11}
do
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2001 pad$i asd$i
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2006 pippo$i asd$i
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2008 abc$i asd$i
done
echo "waiting synch.........."
sleep 16

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

for i in {1..11}
do
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2001 pad$i asd
done
echo "waiting synch.........."
sleep 16

echo "PAD-TEST: simulating partition A=[1,2,3] stopped, B=[4,5,6,7,8,9,10] can go"
pkill -f pad-0.0.1-SNAPSHOT 
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name4 &>log/_name4.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name5 &>log/_name5.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name6 &>log/_name6.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name7 &>log/_name7.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name8 &>log/_name8.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name9 &>log/_name9.txt  &
java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name10 &>log/_name10.txt  &
echo "waiting startup.........."
sleep 6

for i in {1..11}
do
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar put //localhost:2008 abc$i asd
done
sleep 16
#killing all matching process
pkill -f pad-0.0.1-SNAPSHOT 

echo "PAD-TEST: simulating partition resolving"
for i in {1..10}
do
	java -jar ../target/pad-0.0.1-SNAPSHOT.jar ../config.xml name$i &>log/__name$i.txt  &
done

echo "PAD-TEST: waiting synch"
sleep 20

for i in {1..11}
do
	out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar get //localhost:2007 pad$i )
	expected="PAD-CLIENT: get pad"$i" = asd"
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
	
	out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar get //localhost:2007 pippo$i )
	expected="PAD-CLIENT: get pippo"$i" = asd"$i
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
	
	out=$(java -jar ../target/pad-0.0.1-SNAPSHOT.jar get //localhost:2007 abc$i )
	expected="PAD-CLIENT: get abc"$i" = asd"
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
done

echo "deleting storage"
for i in {1..10}
do
	echo "deleting node"$i
	#rm -fr "/tmp/node"$i"_storage"
done


#killing all matching process
pkill -f pad-0.0.1-SNAPSHOT 

echo ""
echo ""
echo ""
echo "TEST 5 OK"
echo ""
echo ""
echo ""

