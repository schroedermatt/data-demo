output "resource-ids" {
  value = <<-EOT
  Kafka Cluster ID: ${confluent_kafka_cluster.dev-cluster.id}
  Kafka Cluster Bootstrap Endpoint: ${confluent_kafka_cluster.dev-cluster.bootstrap_endpoint}
  Kafka Cluster Rest Endpoint: ${confluent_kafka_cluster.dev-cluster.rest_endpoint}
  #
  #
  Service Accounts and their Kafka API Keys (API Keys inherit the permissions granted to the owner):
  #
  # ENV MANAGER
  ${confluent_service_account.env-manager.display_name}:                     ${confluent_service_account.env-manager.id}
  ${confluent_service_account.env-manager.display_name}'s Kafka API Key:     "${confluent_api_key.env-manager-kafka-api-key.id}"
  ${confluent_service_account.env-manager.display_name}'s Kafka API Secret:  "${confluent_api_key.env-manager-kafka-api-key.secret}"
  #
  # DATA DEMO
  ${confluent_service_account.data-demo-app.display_name}:                    ${confluent_service_account.data-demo-app.id}
  ${confluent_service_account.data-demo-app.display_name}'s Kafka API Key:    "${confluent_api_key.data-demo-app-kafka-api-key.id}"
  ${confluent_service_account.data-demo-app.display_name}'s Kafka API Secret: "${confluent_api_key.data-demo-app-kafka-api-key.secret}"
  #
  # WORKSHOP APPS
  ${confluent_service_account.workshop-app.display_name}:                    ${confluent_service_account.workshop-app.id}
  ${confluent_service_account.workshop-app.display_name}'s Kafka API Key:    "${confluent_api_key.workshop-app-kafka-api-key.id}"
  ${confluent_service_account.workshop-app.display_name}'s Kafka API Secret: "${confluent_api_key.workshop-app-kafka-api-key.secret}"
  #
  #
  In order to use the Confluent CLI v2 to produce and consume messages from topic '${confluent_kafka_topic.artists.topic_name}' using Kafka API Keys
  of ${confluent_service_account.data-demo-app.display_name} and ${confluent_service_account.workshop-app.display_name} service accounts
  run the following commands:

  # 1. Log in to Confluent Cloud
  $ confluent login --save
  # 2. Produce key-value records to topic '${confluent_kafka_topic.artists.topic_name}' by using ${confluent_service_account.data-demo-app.display_name}'s Kafka API Key
  $ confluent kafka topic produce ${confluent_kafka_topic.artists.topic_name} --environment ${confluent_environment.workshop.id} --cluster ${confluent_kafka_cluster.dev-cluster.id} --api-key "${confluent_api_key.data-demo-app-kafka-api-key.id}" --api-secret "${confluent_api_key.data-demo-app-kafka-api-key.secret}"
  # Enter a few records and then press 'Ctrl-C' when you're done.
  # Sample records:
  # {"id":1,"name":"Rocko Donny","genre":"rock"}
  # {"id":2,"name":"Jane Dean","genre":"classical"}
  # {"id":3,"name":"The WooHoos","genre":"punk"}
  # 3. Consume records from topic '${confluent_kafka_topic.artists.topic_name}' by using ${confluent_service_account.workshop-app.display_name}'s Kafka API Key
  $ confluent kafka topic consume ${confluent_kafka_topic.artists.topic_name} --from-beginning --environment ${confluent_environment.workshop.id} --cluster ${confluent_kafka_cluster.dev-cluster.id} --api-key "${confluent_api_key.workshop-app-kafka-api-key.id}" --api-secret "${confluent_api_key.workshop-app-kafka-api-key.secret}"
  # When you are done, press 'Ctrl-C'.
  EOT

  sensitive = true
}