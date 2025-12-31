variable "project_id" {
  description = "The GCP project ID"
  type        = string
}

variable "region" {
  description = "The GCP region to deploy to"
  type        = string
  default     = "asia-northeast1"
}

variable "service_name" {
  description = "The name of the Cloud Run service"
  type        = string
  default     = "mnist-app"
}

variable "repository_name" {
  description = "The name of the Artifact Registry repository"
  type        = string
  default     = "mnist-repo"
}
