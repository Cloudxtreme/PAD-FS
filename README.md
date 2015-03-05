# PAD-FS



How to install:
===============

```
git clone --recursive https://github.com/DarioBalinzo/PAD-FS
cd PAD-FS/pad
mvn clean package
```

How to run the tests:
=====================

The scripts allow to deploy a 10 nodes filesystem in a pseudo distributed mode.

This script will simulate insertion with some node failure and restarting:

```
cd script
./test_node_failure.sh
```

to test network partition, in scripts folder run:

 ```
./test_partition.sh
```


to test network partition and node failure whit many insertion:

 ```
./test_many_insertions.sh
```

