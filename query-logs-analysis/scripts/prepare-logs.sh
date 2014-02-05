#!/usr/bin/env bash

source ./scripts/config.sh

EXPECTED_ARGS=1
E_BADARGS=65

source scripts/config.sh

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` log-directory"
  exit $E_BADARGS
fi

echo "convert log in $1"

for i in $(ls $1/*log.gz); do ./scripts/parse-logfile-to-json.sh $i ${i/log/json}; done;
for i in $(ls $1/*json.gz); do ./scripts/convert-json-logs-to-tsv.sh $i ${i/json/tsv}; done;

echo "log converted in $1"



