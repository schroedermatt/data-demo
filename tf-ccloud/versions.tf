terraform {
  required_providers {
    # https://registry.terraform.io/providers/confluentinc/confluent/latest/docs
    confluent = {
      source  = "confluentinc/confluent"
      version = "1.19.0"
    }
  }
}