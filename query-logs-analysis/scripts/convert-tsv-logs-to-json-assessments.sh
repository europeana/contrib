#!/usr/bin/env bash

source ./scripts/config.sh

EXPECTED_ARGS=2
E_BADARGS=65

source scripts/config.sh

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` europeana-log-file.tsv assessments.json[.gz]"
  exit $E_BADARGS
fi

echo "generate assessments from $1"
sort -k4 $1 > /tmp/tmp
$JAVA eu.europeana.querylog.cli.GenerateAssessmentsCLI -input /tmp/tmp -output $2


echo "assessments in $2"



