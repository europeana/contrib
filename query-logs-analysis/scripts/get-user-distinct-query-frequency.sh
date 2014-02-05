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
zcat $1/*tsv.gz | cut -f 2,4 | sort -k1,1 -k2,2 | uniq | cut -f 2 | sort | uniq -c | sort -nrk1 



