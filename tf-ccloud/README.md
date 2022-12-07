# Terraform + Confluent Cloud

The `main.tf` file in this will provision a full Confluent Cloud environment tailored to the Data Demo apps.

The resources provisioned include -

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
  
## Getting Started

### Install `tf`

https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli

### Configure Confluent Cloud API Key + Secret

```bash
export CONFLUENT_CLOUD_API_KEY="ABCDEFG6HPYEX2ZN"
export CONFLUENT_CLOUD_API_SECRET="/HIJKLMNOP+QRSTUVWXYZIebwIEg6ldxioAaoYW4DL+orAvVy4vJOU2SR"
```

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

```txt
terraform output resource-ids

<<EOT
Kafka Cluster ID: lkc-3rr68m
# remove the "SASL_SSL://" and use this value for the CONFLUENT_CLOUD_BOOTSTRAP_SERVER
Kafka Cluster Bootstrap Endpoint: SASL_SSL://pkc-ymrq7.us-east-2.aws.confluent.cloud:9092
Kafka Cluster Rest Endpoint: https://pkc-ymrq7.us-east-2.aws.confluent.cloud:443

# use this value for the CONFLUENT_CLOUD_API_KEY
data-demo-app's Kafka API Key:    "ABCDEFNBESUYPKIB"
# use this value for the CONFLUENT_CLOUD_API_SECRET
data-demo-app's Kafka API Secret: "ABCDEFSD5OlV4+5f/3VC6xVb+DdKmOxunRNIy6oUV5ZxHl9zAPlF7Kb7nYI46Pig"
```

## Confluent CLI

If you plan to run the `confluent cli`, follow the [install directions](https://docs.confluent.io/confluent-cli/current/install.html).

Once installed, you can run `confluent login --save` which will open a browser and allow you to complete the login flow.