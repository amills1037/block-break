output "www_s3_bucket" {
  description = "S3 bucket for www files."
  value       = module.www_s3_bucket.s3_bucket_id
}

output "serverless_s3_bucket" {
  description = "S3 bucket for serverless files."
  value       = module.serverless_s3_bucket.s3_bucket_id
}

output "github_role_arn" {
  description = "GitHub role arn."
  value       = module.github_role.arn
}
