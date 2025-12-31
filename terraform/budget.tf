variable "billing_account_id" {
  description = "The ID of the billing account to associate this budget with"
  type        = string
  default     = "01705E-9DC35C-4D8136"
}

variable "budget_amount" {
  description = "The amount of the budget in JPY"
  type        = number
  default     = 1000
}

resource "google_billing_budget" "budget" {
  billing_account = var.billing_account_id
  display_name    = "Budget for ${var.project_id}"

  budget_filter {
    projects = ["projects/${var.project_id}"]
  }

  amount {
    specified_amount {
      currency_code = "JPY"
      units         = var.budget_amount
    }
  }

  threshold_rules {
    threshold_percent = 0.5
  }
  
  threshold_rules {
    threshold_percent = 0.9
  }

  threshold_rules {
    threshold_percent = 1.0
  }
  
  # 通知チャンネルの設定（デフォルトでは請求管理者にメールが届きます）
  # Pub/Subに通知を飛ばしてSlack連携等も可能ですが、まずはデフォルトで十分です。
}
