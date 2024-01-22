# Prerequisites

## Git

You will need Git to clone this repository. Follow the [getting started guide](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) to complete your installation.

## Java 17

You can install Java 17 however you'd like, but SDKMAN is a tool that makes installing and switching between versions a breeze.

1) [Install SDKMAN!](https://sdkman.io/install)
2) Install Java 17 via SDKMAN!
    ```bash
    sdk install java 17.0.2-tem
    sdk use java 17.0.2-tem
    ```

## Docker

To run any of the local docker clusters, you must first install and start Docker.

https://docs.docker.com/get-docker/

* **📣 ATTN LINUX USERS:** you must also run through these steps -> https://docs.docker.com/engine/install/linux-postinstall/   
* **📣 ATTN WINDOWS USERS:** you must be running WSL2 -> https://docs.docker.com/desktop/wsl/

## IDE

> Only required if you're doing development on `data-demo`

[IntelliJ CE](https://www.jetbrains.com/idea/download/#section=mac) is recommended, but use what you're comfortable with.

### Project SDK Configuration

> Only required if you're doing development on `data-demo`

Once you've opened the project in your IDE of choice, ensure the SDK is aligned with your Java 17 installation.

![project-sdk](./assets/project_sdk.png)

### Enable Annotation Processors for Lombok

> Only required if you're doing development on `data-demo`

Lombok is a tool that helps remove boilerplate in Java applications. Lombok is utilized in this project, so you must enabled annotation processors for Lombok to work.

![enable-annotation-processing](./assets/enable_annotation_processing.png)

_[Lombok Docs](https://projectlombok.org/)_

## `jq` (optional)

`jq` is a command line tool that can format (and do many other things) JSON responses. It is really only needed if you plan to run the mockdata-api and call the API via curl.

```bash
# example usage of jq 
curl -X POST localhost:8080/customers | jq
```

https://stedolan.github.io/jq/download/