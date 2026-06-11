## Resources for website code

# cloudfront certificates must be created in the us-east-1 region
module "acm" {
  source  = "terraform-aws-modules/acm/aws"
  version = "~> 6.3"

  region = "us-east-1"

  domain_name = data.aws_route53_zone.selected.name

  subject_alternative_names = [
    format("www.%s", data.aws_route53_zone.selected.name),
  ]

  validation_method   = "DNS"
  zone_id             = data.aws_route53_zone.selected.zone_id
  wait_for_validation = true

  tags = local.tags
}

# main website is served from here
module "www_s3_bucket" {
  source  = "terraform-aws-modules/s3-bucket/aws"
  version = "~> 5.14"

  force_destroy = true ## files are create by the build process

  bucket                   = "cloudfront.www.${var.aws_route53_zone_name}"
  control_object_ownership = false

  attach_policy = true
  policy        = data.aws_iam_policy_document.www_s3_policy.json

  tags = local.tags
}

data "aws_iam_policy_document" "www_s3_policy" {
  # Origin Access Control
  statement {
    actions   = ["s3:GetObject"]
    resources = ["${module.www_s3_bucket.s3_bucket_arn}/*"]

    principals {
      type        = "Service"
      identifiers = ["cloudfront.amazonaws.com"]
    }

    condition {
      test     = "StringEquals"
      variable = "aws:SourceArn"
      values   = [module.www_distribution.cloudfront_distribution_arn]
    }
  }
}

resource "aws_iam_policy" "github_www_s3_policy" {
  name        = "github-www-s3-policy"
  description = "Allows specific actions"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action   = ["s3:PutObject"]
        Effect   = "Allow"
        Resource = "${module.www_s3_bucket.s3_bucket_arn}/*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "attach_github_www_s3_policy" {
  role       = module.github_role.name
  policy_arn = resource.aws_iam_policy.github_www_s3_policy.arn
}

module "www_distribution" {
  source = "terraform-aws-modules/cloudfront/aws"

  comment = "Block Break CloudFront"

  aliases = [
    var.aws_route53_zone_name,
    "www.${var.aws_route53_zone_name}"
  ]

  origin_access_control = {
    s3_oac = {
      description      = "CloudFront access to S3"
      origin_type      = "s3"
      signing_behavior = "always"
      signing_protocol = "sigv4"
    }
  }

  # logging_config = {
  #   bucket = "logs-my-cdn.s3.amazonaws.com"
  # }

  origin = {
    www_s3_bucket = {
      domain_name               = module.www_s3_bucket.s3_bucket_bucket_regional_domain_name
      origin_access_control_key = "s3_oac"
    }
  }

  default_cache_behavior = {
    target_origin_id       = "www_s3_bucket"
    viewer_protocol_policy = "redirect-to-https"
    cache_policy_id        = "4135ea2d-6df8-44a3-9df3-4b5a84be39ad" # AWS Managed-CachingDisabled

    function_association = {
      viewer-request = {
        function_key = "append_index"
      }
    }
  }

  default_root_object = "index.html"

  viewer_certificate = {
    acm_certificate_arn      = module.acm.acm_certificate_arn
    minimum_protocol_version = "TLSv1.3_2025"
    ssl_support_method       = "sni-only"
  }

  cloudfront_functions = {
    append_index = {
      runtime = "cloudfront-js-2.0"
      comment = "Applend index.html to directories"
      code    = <<-EOT
        function handler(event) {
            var request = event.request;
            var uri = request.uri;

            // If the URI ends with a slash, append 'index.html'
            if (uri.endsWith('/')) {
                request.uri += 'index.html';
            }
            // If the URI does not end with a slash and has no file extension, append '/index.html'
            else if (!uri.includes('.')) {
                request.uri += '/index.html';
            }

            return request;
        }
        EOT
      publish = true
    }
  }

  tags = local.tags
}

module "website_zone" {
  source  = "terraform-aws-modules/route53/aws"
  version = "~> 6.5"

  create_zone = false

  name = var.aws_route53_zone_name

  records = {
    apex = {
      full_name = var.aws_route53_zone_name
      type      = "A"

      alias = {
        name    = module.www_distribution.cloudfront_distribution_domain_name
        zone_id = "Z2FDTNDATAQYW2"
      }
    }

    www = {
      type = "CNAME"

      records = [
        module.www_distribution.cloudfront_distribution_domain_name
      ]

      ttl = 300
    }
  }
}
