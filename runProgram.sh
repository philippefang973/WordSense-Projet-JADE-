#!/bin/bash
javac -cp .:lib/jade.jar:lib/mysql-connector-java-5.1.49-bin.jar -d classes src/wordsense/*.java
java -cp .:lib/jade.jar:lib/mysql-connector-java-5.1.49-bin.jar:classes jade.Boot -gui -agents "AgentCom:wordsense.AgentCom($1,$2,$3,$4);AgentDef:wordsense.AgentDef"

  
