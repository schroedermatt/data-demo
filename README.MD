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

Run the mockdata-api -

```bash
./gradlew bootRunMockData
```

Sample request -

```bash
curl -X POST localhost:8080/customers | jq
```



