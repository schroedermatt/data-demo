#!/bin/bash

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

#if [ ! -d "./confluentinc-kafka-connect-elasticsearch" ]; then
#  tput setaf 2
#  echo "installing connector: confluentinc-kafka-connect-elasticsearch"
#  tput sgr 0
#  confluent-hub install --worker-configs ./tmp/connect-distributed.properties --component-dir . --no-prompt confluentinc/kafka-connect-elasticsearch:14.0.0
#  echo""
#else
#  tput setaf 3
#  echo "connector already installed: confluentinc-kafka-connect-elasticsearch"
#  tput sgr 0
#fi


#
# OpenSearch Aiven Connector
#
# This connector is not part of the confluent-hub, so the confluent-hub CLI cannot be used to install it. Fortunately,
# the bundle exists as a release on GitHub, and using the tar, it can be extracted
#
if [ ! -d "./opensearch-connector-for-apache-kafka-3.1.1" ]; then
  tput setaf 2
  echo "installing connector: opensearch-connector-for-apache-kafka"
  curl -L https://github.com/Aiven-Open/opensearch-connector-for-apache-kafka/releases/download/v3.1.1/opensearch-connector-for-apache-kafka-3.1.1.tar | tar xfv - -C . opensearch-connector-for-apache-kafka-3.1.1
  tput sgr 0
else
  tput setaf 3
  echo "connector already installed: opensearch-connector-for-apache-kafka"
  tput sgr 0
fi


tput setaf 5
echo ""
echo "installation completed"
echo ""
tput sgr 0


rm -f ./tmp/connect-distributed.properties
rmdir ./tmp
