#!/usr/bin/env bash

XMX="-Xmx4000m"
LOG=INFO
##LOG=DEBUG
LOGAT=1000
E_BADARGS=65
JAVA="java $XMX -cp target/query-logs-analysis-0.0.1-SNAPSHOT-jar-with-dependencies.jar"

export LC_ALL=C

TMP=/data/diego/tmp
