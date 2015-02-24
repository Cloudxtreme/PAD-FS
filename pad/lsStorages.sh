#! /bin/bash

for i in {1..7}
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
