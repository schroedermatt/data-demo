#!/bin/bash

FILE=$1
shift

curl -H "Content-Type: application/json" http://localhost:18083/connectors -X POST --data "@${FILE}"
