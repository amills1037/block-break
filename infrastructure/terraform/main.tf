provider "aws" {
  # region = "ca-central-1"
}

locals {
  tags = {
    App = "BlockBreak"
    Stack = "Production"
  }
}

data "aws_route53_zone" "selected" {
  name = var.aws_route53_zone_name
}

## One per account per url??  this may need to be added in a different condiguration set
module "github_oidc_provider" {
  source = "terraform-aws-modules/iam/aws//modules/iam-oidc-provider"
  version = "~> 6.6"
  url = "https://token.actions.githubusercontent.com"

  tags = local.tags
}

module "github_role" {
  source = "terraform-aws-modules/iam/aws//modules/iam-role"
  version = "~> 6.6"

  name = "block-break-github-oidc-role"

  enable_github_oidc = true
  oidc_wildcard_subjects = ["${var.git_hub_repo}:*"]

  tags = local.tags
}

module "deploy_s3_bucket" {
  source  = "terraform-aws-modules/s3-bucket/aws"
  version = "~> 5.14"

  force_destroy = true ## files are create by the build process

  bucket                   = "deploy-block-break.${var.aws_route53_zone_name}"
  control_object_ownership = false

  tags = local.tags
}
