#!/bin/bash

#export JTIMELOG_DATAFILE="/some/path/to/timelog.txt"
#export JTIMELOG_CATEGORIESFILE="/some/path/to/tasks.txt"

export PATH=/home/stanislav.mitrega/.local/bin:/home/stanislav.mitrega/bin:/home/stanislav.mitrega/.local/bin:/home/stanislav.mitrega/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin:/snap/bin:/opt/gtimelog_tools:/opt/zim:/home/standa/Avast/work/tools/es_api:/opt/gtimelog_tools:/opt/zim:/home/standa/Avast/work/tools/es_api:/opt/jdk-17/bin:/opt/gtimelog-tools-master
export JTIMELOG_DATAFILE="/home/stanislav.mitrega/DataSync/jtimelog/timelog.txt"
export JTIMELOG_CATEGORIESFILE="/home/stanislav.mitrega/DataSync/jtimelog/tasks.txt"

cd /home/stanislav.mitrega/Projekty/jtimelog
java -Xmx100m -Xms30m -jar target/jtimelog-1.0-SNAPSHOT-jar-with-dependencies.jar 2>&1 > jtimelog.log

