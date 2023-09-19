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

# output "ecs_cluster" {
#     value = module.ecs_cluster.id
# }

module "ecs-fargate-task-definition" {
  source  = "umotif-public/ecs-fargate-task-definition/aws"
  version = "2.2.0"
  # insert the 2 required variables here
  enabled              = true
  name_prefix          = "test-container"
  task_container_image = "626109959667.dkr.ecr.us-east-1.amazonaws.com/java-api:latest"

  container_name      = "test-container-name"
  task_container_port = "8080"
  task_host_port      = "8080"

  task_definition_cpu    = "512"
  task_definition_memory = "4096"

  task_container_environment = {
    "ENVIRONEMNT" = "Test"
  }
}

module "ecs-service" {
    source          = "./modules/ecs"
    ecs_cluster_id = module.ecs_cluster.id
    task_definition_arn = module.ecs-fargate-task-definition.task_definition_arn
}