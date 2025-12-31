# --- 1. Artifact Registry Repository ---
resource "google_artifact_registry_repository" "repo" {
  location      = var.region
  repository_id = var.repository_name
  description   = "Docker repository for MNIST Kotlin App"
  format        = "DOCKER"
}

# --- 2. Cloud Run Service ---
resource "google_cloud_run_v2_service" "default" {
  name     = var.service_name
  location = var.region
  ingress  = "INGRESS_TRAFFIC_ALL"

  template {
    containers {
      # 初期デプロイ時は手動で上げたイメージ、またはGoogleのサンプルイメージを指定
      # 実際の運用では GitHub Actions がここを新しいイメージIDで上書きデプロイします
      image = "${var.region}-docker.pkg.dev/${var.project_id}/${var.repository_name}/${var.service_name}:latest"
      
      resources {
        limits = {
          cpu    = "1000m"
          memory = "512Mi"
        }
      }
      
      ports {
        container_port = 8080
      }
      
      # 環境変数を設定する場合
      env {
        name  = "PORT"
        value = "8080"
      }
    }
  }

  traffic {
    type    = "TRAFFIC_TARGET_ALLOCATION_TYPE_LATEST"
    percent = 100
  }
}

# --- 3. Allow Public Access (IAM) ---
resource "google_cloud_run_service_iam_binding" "default" {
  location = google_cloud_run_v2_service.default.location
  service  = google_cloud_run_v2_service.default.name
  role     = "roles/run.invoker"
  members = [
    "allUsers"
  ]
}

# --- Output URL ---
output "service_url" {
  value = google_cloud_run_v2_service.default.uri
}
