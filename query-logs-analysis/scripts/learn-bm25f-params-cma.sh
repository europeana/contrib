#!/usr/bin/env bash

source ./scripts/config.sh

EXPECTED_ARGS=3
E_BADARGS=65

source scripts/config.sh

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: `basename $0` assessment-dir measure-to-optimize[NDCG@24,P@24,R@24] log-file"
  exit $E_BADARGS
fi

$JAVA eu.europeana.querylog.cli.learn.LearnBM25FParametersWithCMAESCLI -goldentruth $1 -measure $2 -log $3






