# PAD-FS

A Distributed, Eventually consistent, FileSystem. 


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
cd script
./startNodes.sh
```


How to stop:
=============
in script folder
```
./stopNodes.sh
```


How to list all states:
=======================
```
in script folder
./lsStorages.sh
```



How to put:
==========
in script folder
```
 java -jar ../target/pad-0.0.1-SNAPSHOT.jar put "urlregistry" "key" "value"

```

How to get:
==========
in script folder
```
java -jar ../target/pad-0.0.1-SNAPSHOT.jar get "urlregistry" "key" 

```