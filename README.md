# PAD-FS

A Distributed, Eventually consistent, FileSystem. Resilient to node failure and network partition


How to install:
===============

```
git clone --recursive https://github.com/DarioBalinzo/PAD-FS
cd PAD-FS/pad
mvn clean package
```

How to run:
==========
```
./startNodes.sh
```


How to stop:
=============
```
./stopNodes.sh
```


How to list all states:
=======================
```
./lsStorages.sh
```



How to put:
==========
```
 java -jar target/pad-0.0.1-SNAPSHOT.jar put "urlregistry" "key" "value"

```

How to get:
==========
```
java -jar target/pad-0.0.1-SNAPSHOT.jar get "urlregistry" "key" 

```