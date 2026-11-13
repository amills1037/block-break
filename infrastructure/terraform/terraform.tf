terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.48"
    }
  }

  backend "s3" {
      bucket       = "terraform.blockbreak.ca"
      key          = "env/terraform.tfstate"
      region       = "ca-central-1"
      use_lockfile = true
    }

  required_version = ">= 1.15"
}
