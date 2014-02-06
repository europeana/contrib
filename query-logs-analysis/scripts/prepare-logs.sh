#!/usr/bin/env bash

source ./scripts/config.sh

EXPECTED_ARGS=3
E_BADARGS=65

source scripts/config.sh

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` log-directory json-log-directory tsv-log-directory"
  exit $E_BADARGS
fi

LOG=$1
JSON=$2
TSV=$3

mkdir -p $JSON $TSV

echo "convert log in $LOG to json in $JSON"
for i in $(ls $1/*log.gz); do f=$(basename $i); ./scripts/parse-logfile-to-json.sh $i $JSON/${f/log/json}; done;
echo "convert json log in $JSON to tsv in $TSV"
for i in $(ls $JSON/*json.gz); do f=$(basename $i); ./scripts/convert-json-logs-to-tsv.sh $i $TSV/${f/json/tsv}; done;

echo "log converted in json $JSON and tsv $TSV"



