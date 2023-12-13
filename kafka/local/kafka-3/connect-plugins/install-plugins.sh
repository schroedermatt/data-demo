#!/bin/sh

cd "$(dirname -- "$0")"

mkdir -p ./tmp
touch ./tmp/connect-distributed.properties

tput setaf 5
echo ""
echo "installing connectors needed to run this demo"
echo ""
tput sgr 0

if [ ! -d "./debezium-debezium-connector-postgresql" ]; then
  tput setaf 2
  echo "installing connector: debezium-debezium-connector-postgresql"
  tput sgr 0
  confluent-hub install --worker-configs ./tmp/connect-distributed.properties --component-dir . --no-prompt debezium/debezium-connector-postgresql:1.9.2
  echo""
else
  tput setaf 3
  echo "connector already installed: debezium-debezium-connector-postgresql"
  tput sgr 0
fi

if [ ! -d "./confluentinc-kafka-connect-s3" ]; then
  tput setaf 2
  echo "installing connector: confluentinc/kafka-connect-s3" 
  tput sgr 0
  confluent-hub install --worker-configs ./tmp/connect-distributed.properties --component-dir . --no-prompt confluentinc/kafka-connect-s3:10.5.7
  echo""
else
  tput setaf 3
  echo "connector already installed: confluentinc/kafka-connect-s3"
  tput sgr 0
fi

tput setaf 5
echo ""
echo "installation completed"
echo ""
tput sgr 0


rm -f ./tmp/connect-distributed.properties
rmdir ./tmp
