# variable "env_name" {
#   description = "Environment name"
#   type = string
#   default = "dev"
# }

# data "aws_ecr_repository" "java-api_ecr_repo" {
#   name = "java-api"
# }

# resource "aws_lambda_function" "java-api_function" {
#   function_name = "java-api-${var.env_name}"
#   timeout       = 30 # seconds
#   image_uri     = "${data.aws_ecr_repository.java-api_ecr_repo.repository_url}:latest"
#   package_type  = "Image"

#   role = aws_iam_role.java-api_function_role.arn

#   environment {
#     variables = {
#       ENVIRONMENT = var.env_name
#     }
#   }
# }

# resource "aws_iam_role" "java-api_function_role" {
#   name = "api-${var.env_name}"

#   assume_role_policy = jsonencode({
#     Statement = [
#       {
#         Action = "sts:AssumeRole"
#         Effect = "Allow"
#         Principal = {
#           Service = "lambda.amazonaws.com"
#         }
#       },
#     ]
#   })
# }


module "ecs_cluster" {
  source  = "terraform-aws-modules/ecs/aws//modules/cluster"
  version = "5.2.2"
  cluster_name = "ecs-fargate"

  cluster_configuration = {
    execute_command_configuration = {
      logging = "OVERRIDE"
      log_configuration = {
        cloud_watch_log_group_name = "/aws/ecs/aws-ec2"
      }
    }
  }

  fargate_capacity_providers = {
    FARGATE = {
      default_capacity_provider_strategy = {
        weight = 50
      }
    }
    FARGATE_SPOT = {
      default_capacity_provider_strategy = {
        weight = 50
      }
    }
  }

  tags = {
    Environment = "Development"
    Project     = "EcsEc2"
  }
}

module "ecs_container_definition" {
  source = "terraform-aws-modules/ecs/aws//modules/container-definition"

  name      = "example"
  cpu       = 512
  memory    = 1024
  essential = true
  image     = "public.ecr.aws/aws-containers/ecsdemo-frontend:776fd50"
  port_mappings = [
    {
      name          = "ecs-sample"
      containerPort = 80
      protocol      = "tcp"
    }
  ]

  # Example image used requires access to write to root filesystem
  readonly_root_filesystem = false

  memory_reservation = 100

  tags = {
    Environment = "dev"
    Terraform   = "true"
  }
}