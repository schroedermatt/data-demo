# Demo

```bash
# env
./gradlew kafkaComposeUp
./gradlew s3ComposeUp
./gradlew tracingComposeUp

# set tracingEnabled=true in gradle.properties
./gradlew bootRunDaemon

# start s3 sink connector
curl -X POST localhost:18083/connectors -H "Content-Type: application/json" -d @./kafka/local/connect/s3-connector-config.json
curl localhost:18083/connectors/s3-sink/status | jq
curl -X DELETE localhost:18083/connectors/s3-sink

# run duckdb
duckdb --init ./observability/s3/duckdb-init.sql
# duckdb-samples.sql has queries
```

# Grafana Tempo

Useful Links -

* https://github.com/grafana/tempo/tree/main/example/docker-compose/otel-collector
* https://grafana.com/docs/tempo/latest/getting-started/