#!/usr/bin/env bash

source ./scripts/config.sh

EXPECTED_ARGS=5
E_BADARGS=65

source scripts/config.sh

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` assessments.json[.gz] min-number-of-clicks min-number-of-documents min-number-of-users output"
  exit $E_BADARGS
fi

echo "print assessments from $1"

$JAVA eu.europeana.querylog.cli.PrintFilteredAssessmentsCLI -input $1 -clicks $2 -docs $3 -users $4 -output $5




