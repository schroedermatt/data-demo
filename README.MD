# MSSE DATA DEMO


## Environment Prerequisites

### IntelliJ CE

https://www.jetbrains.com/idea/download/#section=mac

_Feel free to use another IDE, but IntelliJ has become the industry defacto IDE._

### SDKMAN!

1) [Install SDKMAN!](https://sdkman.io/install)
2) Install Java 17 via SDKMAN!
    ```bash
    sdk install java 17.0.2-tem
    sdk use java 17.0.2-tem
    ```

### Set Project SDK

![project-sdk](./assets/project_sdk.png)

### Enable Annotation Processors for Lombok

![enable-annotation-processing](./assets/enable_annotation_processing.png)

_[Lombok Docs](https://projectlombok.org/)_

### Docker

https://docs.docker.com/get-docker/

### jq

https://stedolan.github.io/jq/download/

## Getting Started

### PostgreSQL

From the `postgres/local` directory, run `./up.sh`. Check out the [postgres readme](./postgres/local/README.MD) for more details on what this will start up and how you can connect.

Run the mockdata-api ONLY -

```bash
./gradlew :mockdata-api:bootRunMockData
```

Sample request -

```bash
curl -X POST localhost:8080/customers | jq
```

Run the mockdata-api AND the mockdata-daemon -

```bash
# --parallel will start both at the same time
./gradlew bootRunMockData --parallel 

# you could also run the below commands from separate terminal tabs to have a cleaner separation of logs

## run from tab 1
./gradlew :mockdata-api:bootRunMockData
## run from tab 2
./gradlew :mockdata-daemon:bootRunMockData
```



