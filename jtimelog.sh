#!/bin/bash

export JTIMELOG_DATAFILE="$HOME/DataSync/jtimelog/timelog.txt"
export JTIMELOG_CATEGORIESFILE="$HOME/DataSync/jtimelog/tasks.txt"

cd $HOME/Projekty/jtimelog

[ -z "$JTIMELOG_MONOFONT_SIZE" ] && JTIMELOG_MONOFONT_SIZE=16
export JTIMELOG_MONOFONT_SIZE
java -Xmx100m -Xms30m -jar target/jtimelog-1.0-SNAPSHOT-jar-with-dependencies.jar 2>&1 > jtimelog.log
