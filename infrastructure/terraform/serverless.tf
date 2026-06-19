module "serverless_s3_bucket" {
  source  = "terraform-aws-modules/s3-bucket/aws"
  version = "~> 5.14"

  force_destroy = true ## files are create by the build process

  bucket                   = "serverless.${var.aws_route53_zone_name}"
  control_object_ownership = false

  tags = local.tags
}

resource "aws_iam_policy" "github_serverless_s3_policy" {
  name        = "github-serverless-s3-policy"
  description = "Allows specific actions"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action   = ["s3:PutObject"]
        Effect   = "Allow"
        Resource = "${module.serverless_s3_bucket.s3_bucket_arn}/*"
      },
      {
        Action   = ["lambda:UpdateFunctionCode"]
        Effect   = "Allow"
        Resource = module.serverless_lambda_function.lambda_function_arn
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "attach_github_serverless_s3_policy" {
  role       = module.github_role.name
  policy_arn = resource.aws_iam_policy.github_serverless_s3_policy.arn
}

module "serverless_lambda_function" {
  source  = "terraform-aws-modules/lambda/aws"
  version = "~> 8.8"

  function_name = "websocket-serverless-lambda"
  description   = "Serverless block break lambda"
  handler       = "ca.blockbreak.serverless.App"
  runtime       = "java25"
  memory_size   = "1024"
  timeout       = "30"

  create_package         = false
  local_existing_package = "../../serverless/bootstrap/serverless.jar"

  ignore_source_code_hash = true

  allowed_triggers = {
    apigateway = {
      service    = "apigateway"
      source_arn = "${module.serverless_api_gateway.api_execution_arn}/*/*"
    }
  }

  attach_policy_statements = true
  policy_statements = {
    manage_connections = {
      effect = "Allow",
      actions = [
        "execute-api:ManageConnections",
        "execute-api:Invoke"
      ],
      resources = ["${module.serverless_api_gateway.api_execution_arn}/*"]
    }
    dynamodb_access = {
      effect = "Allow"
      actions = [
        "dynamodb:GetItem",
        "dynamodb:DeleteItem",
        "dynamodb:PutItem",
        "dynamodb:Scan",
        "dynamodb:Query",
        "dynamodb:UpdateItem",
      ]
      resources = [
        module.serverless_connection_table.dynamodb_table_arn,
        "${module.serverless_connection_table.dynamodb_table_arn}/index/*",
        module.serverless_stats_table.dynamodb_table_arn,
        "${module.serverless_stats_table.dynamodb_table_arn}/index/*"
      ]
    }
  }

  environment_variables = {
    STACK = "Production"
  }

  tags = local.tags
}

# API Gateway to log to CloudWatch
resource "aws_iam_role" "main_api_gateway_role" {
  name = "api-gateway-logs-role"

  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Principal" : {
          "Service" : "apigateway.amazonaws.com"
        },
        "Action" : "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "main" {
  role       = aws_iam_role.main_api_gateway_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"
}

module "serverless_api_gateway" {
  source  = "terraform-aws-modules/apigateway-v2/aws"
  version = "~> 6.1"

  name                       = "serverless-api-gateway"
  description                = "Block Break Serverless API Gateway"
  protocol_type              = "WEBSOCKET"
  route_selection_expression = "$request.body.action"

  # cors_configuration = {
  #   allow_headers = ["content-type", "x-amz-date", "authorization", "x-api-key", "x-amz-security-token", "x-amz-user-agent"]
  #   allow_methods = ["*"]
  #   allow_origins = ["*"]
  # }

  # Custom domain
  domain_name      = "serverless.${var.aws_route53_zone_name}"
  hosted_zone_name = var.aws_route53_zone_name

  stage_name = "prod"

  stage_default_route_settings = {
    data_trace_enabled       = true
    detailed_metrics_enabled = true
    logging_level            = "INFO"
    throttling_burst_limit   = 100
    throttling_rate_limit    = 100
  }
  # Access logs
  stage_access_log_settings = {
    create_log_group            = true
    log_group_retention_in_days = 7
    format = jsonencode({
      context = {
        domainName              = "$context.domainName"
        integrationErrorMessage = "$context.integrationErrorMessage"
        protocol                = "$context.protocol"
        requestId               = "$context.requestId"
        requestTime             = "$context.requestTime"
        responseLength          = "$context.responseLength"
        routeKey                = "$context.routeKey"
        stage                   = "$context.stage"
        status                  = "$context.status"
        error = {
          message      = "$context.error.message"
          responseType = "$context.error.responseType"
        }
        identity = {
          sourceIP = "$context.identity.sourceIp"
        }
        integration = {
          error             = "$context.integration.error"
          integrationStatus = "$context.integration.integrationStatus"
        }
      }
    })
  }

  # Routes & Integration(s)
  routes = {
    "$connect" = {
      logging_level      = "INFO"
      data_trace_enabled = true
      integration = {
        operation_name = "ConnectRoute"
        uri            = module.serverless_lambda_function.lambda_function_arn
      }
    }

    "$default" = {
      logging_level      = "INFO"
      data_trace_enabled = true
      integration = {
        operation_name = "DefaultRoute"
        uri            = module.serverless_lambda_function.lambda_function_arn
      }

      # We are going to send responses back with @connections
      # route_response_key = "$default"
    }

    "breakblock" = {
      logging_level      = "INFO"
      data_trace_enabled = true
      integration = {
        operation_name = "BlockBreakMessageRoute"
        uri            = module.serverless_lambda_function.lambda_function_arn
      }
    }

    "connect" = {
      logging_level      = "INFO"
      data_trace_enabled = true
      integration = {
        operation_name = "ConnectMessageRoute"
        uri            = module.serverless_lambda_function.lambda_function_arn
      }
    }

    "$disconnect" = {
      logging_level      = "INFO"
      data_trace_enabled = true
      integration = {
        operation_name = "DisconnectRoute"
        uri            = module.serverless_lambda_function.lambda_function_arn
      }

    }
  }

  tags = local.tags
}

resource "aws_lambda_permission" "serverless_api" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = module.serverless_lambda_function.lambda_function_name

  principal = "apigateway.amazonaws.com"

  source_arn = "${module.serverless_api_gateway.api_execution_arn}/*/*"
}

module "serverless_stats_table" {
  source  = "terraform-aws-modules/dynamodb-table/aws"
  version = "~> 5.5"

  name         = "production-block-break-stats"
  billing_mode = "PAY_PER_REQUEST"

  hash_key  = "PlayerId"
  range_key = "StatName"

  attributes = [
    {
      name = "PlayerId"
      type = "S"
    },
    {
      name = "StatName"
      type = "S"
    }
  ]

  stream_enabled   = true
  stream_view_type = "NEW_AND_OLD_IMAGES"

  tags = local.tags
}

resource "aws_dynamodb_table_item" "serverless_stats_item" {
  table_name = module.serverless_stats_table.dynamodb_table_id
  hash_key   = "PlayerId"
  range_key  = "StatName"

  item = jsonencode({
    PlayerId : { S : "global" },
    StatName : { S : "blocksBroken" },
    Stat : { N : "0" }
  })
}

module "serverless_connection_table" {
  source  = "terraform-aws-modules/dynamodb-table/aws"
  version = "~> 5.5"

  name         = "production-block-break-connection"
  billing_mode = "PAY_PER_REQUEST"

  hash_key = "ConnectionId"

  attributes = [
    {
      name = "ConnectionId"
      type = "S"
    }
  ]

  tags = local.tags
}

module "dynamodb_lambda_function" {
  source  = "terraform-aws-modules/lambda/aws"
  version = "~> 8.8"

  function_name = "dynamodb-serverless-lambda"
  description   = "DynamoDB block break lambda"
  handler       = "ca.blockbreak.serverless.GlobalIncrementHandler"
  runtime       = "java25"
  memory_size   = "1024"
  timeout       = "30"

  create_package         = false
  local_existing_package = "../../serverless/bootstrap/serverless.jar"

  ignore_source_code_hash = true

  allowed_triggers = {
    dynamodb = {
      service    = "dynamodb"
      source_arn = module.serverless_stats_table.dynamodb_table_stream_arn
    }
  }
  create_current_version_allowed_triggers = false
  publish                                 = true

  attach_policy_statements = true
  policy_statements = {
    manage_connections = {
      effect = "Allow",
      actions = [
        "execute-api:ManageConnections",
      ],
      resources = ["${module.serverless_api_gateway.api_execution_arn}/*"]
    }

    invoke_connections = {
      effect = "Allow",
      actions = [
        "execute-api:Invoke",
      ],
      resources = ["*"]
    }

    dynamodb_access = {
      effect = "Allow"
      actions = [
        "dynamodb:Scan",
        "dynamodb:DeleteItem",
        "dynamodb:DescribeStream",
        "dynamodb:GetRecords",
        "dynamodb:GetShardIterator",
        "dynamodb:ListStreams"
      ]
      resources = [
        module.serverless_connection_table.dynamodb_table_arn,
        "${module.serverless_connection_table.dynamodb_table_arn}/index/*",
        "${module.serverless_stats_table.dynamodb_table_arn}/stream/*",
        module.serverless_stats_table.dynamodb_table_arn,
        "${module.serverless_stats_table.dynamodb_table_arn}/index/*",
        "${module.serverless_stats_table.dynamodb_table_arn}/stream/*"
      ]
    }
  }

  # arn:aws:dynamodb:ca-central-1:743114209664:table/production-block-break-stats/stream/2026-06-19T04:05:36.263

  environment_variables = {
    STACK          = "Production"
    CONNECTION_URL = "https://${module.serverless_api_gateway.api_id}.execute-api.ca-central-1.amazonaws.com/prod/"
  }

  tags = local.tags

}

resource "aws_lambda_permission" "serverless_dynamodb" {
  statement_id  = "AllowExecutionFromDynamoDB"
  action        = "lambda:InvokeFunction"
  function_name = module.dynamodb_lambda_function.lambda_function_name

  principal = "dynamodb.amazonaws.com"

  source_arn = "${module.serverless_connection_table.dynamodb_table_arn}/*/*"
}
