#!/bin/bash

#export JTIMELOG_DATAFILE="/some/path/to/timelog.txt"
#export JTIMELOG_CATEGORIESFILE="/some/path/to/tasks.txt"

java -Xmx100m -Xms30m -jar target/jtimelog-1.0-SNAPSHOT-jar-with-dependencies.jar 2>&1 > jtimelog.log

