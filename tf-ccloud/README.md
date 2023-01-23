# Terraform + Confluent Cloud

The `main.tf` file in this will provision a [full Confluent Cloud environment](#resources) tailored to the Data Demo & Streams Workshop apps.

## Getting Started

### Install `tf`

`https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli`

### Configure Confluent Cloud API Key + Secret

```bash
export CONFLUENT_CLOUD_API_KEY="ABCDEFG6HPYEX2ZN"
export CONFLUENT_CLOUD_API_SECRET="/HIJKLMNOP+QRSTUVWXYZIebwIEg6ldxioAaoYW4DL+orAvVy4vJOU2SR"
```

#### Confluent CLI

If you plan to run the `confluent cli`, follow the [install directions](https://docs.confluent.io/confluent-cli/current/install.html).

Once installed, you can run `confluent login --save` which will open a browser and allow you to complete the login flow.

### Terraform Init

Initialize the backend (state) and provider plugins (confluent cloud).

```bash
# run from the tf-ccloud directory
terraform init
```

### Terraform Apply

```bash
# run from the tf-ccloud directory
terraform apply

# review the proposed resources and enter 'yes' to proceed
```

### Terraform Output Resource IDs

Once applied, the following command will dump out the credentials you'll need to plug into the Data Demo and other apps.

```bash
terraform output resource-ids
```

### Enable Auto Create Topics

By default, the dedicated clusters disable automatic topic creation. However, this makes the workshop development tricky so we want to flip the `auto.create.topics.enable` config to `true`.

To do this, you're going to need the following values from [terraform output resource-ids](#terraform-output-resource-ids).

* Kafka Cluster ID
* Kafka Cluster Rest Endpoint
* Basic Token - `base64` encode the *Env Manager's API Key and Secret* (`API_Key:API_Secret | base64`)

```bash
curl --location --request PUT 'https://{CLUSTER_REST_ENDPOINT}/kafka/v3/clusters/{CLUSTER_ID}/broker-configs/auto.create.topics.enable' \
    --header 'Authorization: Basic {BASE_64_ENCODED_TOKEN}' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "value": "true"
    }'
```

## Resources

* `confluent_environment.workshop` named "workshop"
* `confluent_kafka_cluster.dev-cluster` named "development" within the "workshop" environment
  * AWS, Single Zone (us-east-2), 1 CKU Dedicated Cluster
* `confluent_service_account.env-manager`: Service Account to manage all resources within the 'dev-cluster' Kafka cluster
  * `confluent_role_binding`: CloudClusterAdmin
  * `confluent_api_key.env-manager-kafka-api-key`: Kafka API Key that is owned by 'env-manager' service account
* `confluent_service_account.data-demo-app`: Service Account for the Data Demo (Mock Data Generator) application
  * `confluent_api_key.data-demo-app-kafka-api-key`: Kafka API Key that is owned by 'data-demo-app' service account
  * `confluent_kafka_acl`: ALLOW WRITE operation to TOPIC(s) PREFIXED with 'data-demo*' for SERVICE_ACCOUNT 'data-demo-app'
* `confluent_service_account.workshop-app`: Shared service account for workshop applications
  * `confluent_api_key.workshop-app-kafka-api-key`: Kafka API Key that is owned by 'workshop-app' service account
  * `confluent_kafka_acl`: ALLOW READ operation to TOPIC(s) PREFIXED with 'data-demo*' for SERVICE_ACCOUNT 'workshop-app'
    * READ on the data-demo topics allows the service account to - Fetch, OffsetCommit, TxnOffsetCommit
  * `confluent_kafka_acl`: ALLOW ALL operations on TOPIC(s) PREFIXED with 'kafka-workshop*' for SERVICE_ACCOUNT 'workshop-app'
    * ALL on the workshop topics allows the service account to - Alter, AlterConfigs, Create, Delete, Describe, DescribeConfigs, Read, Write
  * `confluent_kafka_acl`: ALLOW READ operation to GROUP(s) PREFIXED with 'kafka-workshop*' for SERVICE_ACCOUNT 'workshop-app'
    * READ on consumer groups allows the service account to - AddOffsetsToTxn, Heartbeat, JoinGroup, LeaveGroup, OffsetCommit, OffsetFetch, SyncGroup, TxnOffsetCommit
  * `confluent_kafka_acl`: ALLOW READ operation to GROUP(s) PREFIXED with 'confluent_cli_consumer_' for SERVICE_ACCOUNT 'workshop-app'"
    * This allows the Confluent CLI to be used (the default consumer group ID is "confluent_cli_consumer_<uuid>")
* `confluent_kafka_topic`
  * data-demo-addresses (partitions: 3)
  * data-demo-artists (partitions: 3)
  * data-demo-customers (partitions: 3)
  * data-demo-emails (partitions: 3)
  * data-demo-events (partitions: 3)
  * data-demo-phones (partitions: 3)
  * data-demo-streams (partitions: 3)
  * data-demo-tickets (partitions: 3)
  * data-demo-venues (partitions: 3)