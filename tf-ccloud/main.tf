provider "confluent" {
  # run these to set the tf variables
  # export TF_VAR_confluent_cloud_api_key="***REDACTED***"
  # export TF_VAR_confluent_cloud_api_secret="***REDACTED***"

  # cloud_api_key    = var.confluent_cloud_api_key
  # cloud_api_secret = var.confluent_cloud_api_secret
}

resource "confluent_environment" "workshop" {
  display_name = "workshop"
}

resource "confluent_kafka_cluster" "dev-cluster" {
  display_name = "development"
  availability = "SINGLE_ZONE"
  cloud        = "AWS"
  region       = "us-east-2"
  basic {}
//  dedicated {
//    cku = 1
//  }
  environment {
    id = confluent_environment.workshop.id
  }
}

#####################
#    ENV MANAGER    #
#####################

resource "confluent_service_account" "env-manager" {
  display_name = "env-manager"
  description  = "Service Account to manage all resources within the 'dev-cluster' Kafka cluster"
}

resource "confluent_role_binding" "env-manager-cloud-cluster-admin" {
  principal   = "User:${confluent_service_account.env-manager.id}"
  role_name   = "CloudClusterAdmin"
  crn_pattern = confluent_kafka_cluster.dev-cluster.rbac_crn
}

resource "confluent_api_key" "env-manager-kafka-api-key" {
  display_name = "env-manager-kafka-api-key"
  description  = "Kafka API Key that is owned by 'env-manager' service account"
  owner {
    id          = confluent_service_account.env-manager.id
    api_version = confluent_service_account.env-manager.api_version
    kind        = confluent_service_account.env-manager.kind
  }

  managed_resource {
    id          = confluent_kafka_cluster.dev-cluster.id
    api_version = confluent_kafka_cluster.dev-cluster.api_version
    kind        = confluent_kafka_cluster.dev-cluster.kind

    environment {
      id = confluent_environment.workshop.id
    }
  }

  depends_on = [
    confluent_role_binding.env-manager-cloud-cluster-admin
  ]
}

#############################
#  DATA DEMO (MOCKDATA) APP #
#############################

resource "confluent_service_account" "data-demo-app" {
  display_name = "data-demo-app"
  description   = "Service Account for the Data Demo (Mock Data Generator) application"
}

resource "confluent_api_key" "data-demo-app-kafka-api-key" {
  display_name = "data-demo-app-kafka-api-key"
  description  = "Kafka API Key that is owned by 'data-demo-app' service account"
  owner {
    id          = confluent_service_account.data-demo-app.id
    api_version = confluent_service_account.data-demo-app.api_version
    kind        = confluent_service_account.data-demo-app.kind
  }

  managed_resource {
    id          = confluent_kafka_cluster.dev-cluster.id
    api_version = confluent_kafka_cluster.dev-cluster.api_version
    kind        = confluent_kafka_cluster.dev-cluster.kind

    environment {
      id = confluent_environment.workshop.id
    }
  }
}

resource "confluent_kafka_acl" "data-demo-app-write-on-prefixed-topics" {
  # description   = "ALLOW WRITE operation to TOPIC(s) PREFIXED with '${var.data-demo-app-topic-prefix}' for SERVICE_ACCOUNT '${confluent_service_account.data-demo-app.id}'"
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  resource_type = "TOPIC"
  resource_name = var.data-demo-app-topic-prefix
  pattern_type  = "PREFIXED"
  principal     = "User:${confluent_service_account.data-demo-app.id}"
  host          = "*"
  operation     = "WRITE"
  permission    = "ALLOW"
  rest_endpoint = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

#############################
#       WORKSHOP APPS       #
#############################

resource "confluent_service_account" "workshop-app" {
  display_name  = "workshop-app"
  description   = "Shared service account for workshop applications"
}

resource "confluent_api_key" "workshop-app-kafka-api-key" {
  display_name  = "workshop-app-kafka-api-key"
  description   = "Kafka API Key that is owned by 'workshop-app' service account"
  owner {
    id          = confluent_service_account.workshop-app.id
    api_version = confluent_service_account.workshop-app.api_version
    kind        = confluent_service_account.workshop-app.kind
  }

  managed_resource {
    id          = confluent_kafka_cluster.dev-cluster.id
    api_version = confluent_kafka_cluster.dev-cluster.api_version
    kind        = confluent_kafka_cluster.dev-cluster.kind

    environment {
      id = confluent_environment.workshop.id
    }
  }
}

# READ on the data-demo topics allows the service account to - Fetch, OffsetCommit, TxnOffsetCommit
resource "confluent_kafka_acl" "workshop-app-read-on-prefixed-topics" {
  # description   = "ALLOW READ operation to TOPIC(s) PREFIXED with '${var.data-demo-app-topic-prefix}' for SERVICE_ACCOUNT '${confluent_service_account.workshop-app.id}'"
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  resource_type = "TOPIC"
  resource_name = var.data-demo-app-topic-prefix
  pattern_type  = "PREFIXED"
  principal     = "User:${confluent_service_account.workshop-app.id}"
  host          = "*"
  operation     = "READ"
  permission    = "ALLOW"
  rest_endpoint = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

# 'ALL' on the workshop topics allows the service account to - Alter, AlterConfigs, Create, Delete, Describe, DescribeConfigs, Read, Write
resource "confluent_kafka_acl" "workshop-app-manage-prefixed-topics" {
  # description   = "ALLOW ALL operations on TOPIC(s) PREFIXED with '${var.workshop-app-topic-prefix}' for SERVICE_ACCOUNT '${confluent_service_account.workshop-app.id}'"
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  resource_type = "TOPIC"
  resource_name = var.workshop-app-topic-prefix
  pattern_type  = "PREFIXED"
  principal     = "User:${confluent_service_account.workshop-app.id}"
  host          = "*"
  operation     = "ALL"
  permission    = "ALLOW"
  rest_endpoint = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

# READ on consumer groups allows the service account to - AddOffsetsToTxn, Heartbeat, JoinGroup, LeaveGroup, OffsetCommit, OffsetFetch, SyncGroup, TxnOffsetCommit
resource "confluent_kafka_acl" "workshop-app-groups-read" {
  # description   = "ALLOW READ operation to GROUP(s) PREFIXED with '${var.workshop-app-topic-prefix}' for SERVICE_ACCOUNT '${confluent_service_account.workshop-app.id}'"
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  resource_type = "GROUP"
  resource_name = var.workshop-app-topic-prefix
  pattern_type  = "PREFIXED"
  principal     = "User:${confluent_service_account.workshop-app.id}"
  host          = "*"
  operation     = "READ"
  permission    = "ALLOW"
  rest_endpoint = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

# READ on consumer groups allows the service account to - AddOffsetsToTxn, Heartbeat, JoinGroup, LeaveGroup, OffsetCommit, OffsetFetch, SyncGroup, TxnOffsetCommit
resource "confluent_kafka_acl" "confluent-consumer-cli-groups-read" {
  # description   = "ALLOW READ operation to GROUP(s) PREFIXED with 'confluent_cli_consumer_' for SERVICE_ACCOUNT '${confluent_service_account.workshop-app.id}'"
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  resource_type = "GROUP"
  // The existing values of resource_name, pattern_type attributes are set up to match Confluent CLI's default consumer group ID ("confluent_cli_consumer_<uuid>").
  // https://docs.confluent.io/confluent-cli/current/command-reference/kafka/topic/confluent_kafka_topic_consume.html
  resource_name = "confluent_cli_consumer_"
  pattern_type  = "PREFIXED"
  principal     = "User:${confluent_service_account.workshop-app.id}"
  host          = "*"
  operation     = "READ"
  permission    = "ALLOW"
  rest_endpoint = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

#######################
#       TOPICS        #
#######################

resource "confluent_kafka_topic" "addresses" {
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  topic_name         = "${var.data-demo-app-topic-prefix}-addresses"
  partitions_count   = 3
  rest_endpoint      = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

resource "confluent_kafka_topic" "artists" {
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  topic_name         = "${var.data-demo-app-topic-prefix}-artists"
  partitions_count   = 3
  rest_endpoint      = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

resource "confluent_kafka_topic" "customers" {
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  topic_name         = "${var.data-demo-app-topic-prefix}-customers"
  partitions_count   = 3
  rest_endpoint      = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

resource "confluent_kafka_topic" "emails" {
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  topic_name         = "${var.data-demo-app-topic-prefix}-emails"
  partitions_count   = 3
  rest_endpoint      = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

resource "confluent_kafka_topic" "events" {
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  topic_name         = "${var.data-demo-app-topic-prefix}-events"
  partitions_count   = 3
  rest_endpoint      = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

resource "confluent_kafka_topic" "phones" {
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  topic_name         = "${var.data-demo-app-topic-prefix}-phones"
  partitions_count   = 3
  rest_endpoint      = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

resource "confluent_kafka_topic" "streams" {
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  topic_name         = "${var.data-demo-app-topic-prefix}-streams"
  partitions_count   = 3
  rest_endpoint      = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

resource "confluent_kafka_topic" "tickets" {
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  topic_name         = "${var.data-demo-app-topic-prefix}-tickets"
  partitions_count   = 3
  rest_endpoint      = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}

resource "confluent_kafka_topic" "venues" {
  kafka_cluster {
    id = confluent_kafka_cluster.dev-cluster.id
  }
  topic_name         = "${var.data-demo-app-topic-prefix}-venues"
  partitions_count   = 3
  rest_endpoint      = confluent_kafka_cluster.dev-cluster.rest_endpoint
  credentials {
    key    = confluent_api_key.env-manager-kafka-api-key.id
    secret = confluent_api_key.env-manager-kafka-api-key.secret
  }
}