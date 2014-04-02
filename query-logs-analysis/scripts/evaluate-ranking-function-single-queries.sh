#!/usr/bin/env bash

source ./scripts/config.sh

EXPECTED_ARGS=3
E_BADARGS=65

source scripts/config.sh

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` assessment-dir measure-to-optimize[NDCG@24,P@24,R@24] function[europeana,bm25f]"
  exit $E_BADARGS
fi

$JAVA -Dpartial=true eu.europeana.querylog.cli.learn.EvaluateCLI -goldentruth $1 -measure $2 -rank $3






