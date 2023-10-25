# ShadowTraffic

https://shadowtraffic.io/

## Docs

* ST Docs: https://docs.shadowtraffic.io/overview
* Faker: https://github.com/DiUS/java-faker/blob/8d7036a66fcad54bf652d1bae91fa4ab668c1334/src/main/java/com/github/javafaker/Faker.java

## Getting Started

1. Copy `template-license.env` -> rename to `license.env`
2. Insert license details into `license.env` (`.gitignore` will keep this file out of version control)
3. Start Kafka - `./gradlew kafkaComposeUp` (run from root of `data-demo` repo)
4. Start ShadowTraffic

```bash
# run the shadowtraffic container
### --watch will listen for config.json file changes and refresh
### --sample 10 will limit results to 10 (change number if desired)
### --stdout will print results to stdout instead of kafka
docker run \                                                                         INT 1m 10s
  --network container:broker \
  --env-file license.env \
  -v /your/path/to/data-demo/shadowtraffic/config.json:/home/config.json \
  shadowtraffic/shadowtraffic:latest \
  --config /home/config.json \
  --watch --sample 10 --stdout
  
# run the shadowtraffic container pointed at Kafka
docker run \                                                                         INT 1m 10s
  --network container:broker \
  --env-file license.env \
  -v /your/path/to/data-demo/shadowtraffic/config.json:/home/config.json \
  shadowtraffic/shadowtraffic:latest \
  --config /home/config.json
```