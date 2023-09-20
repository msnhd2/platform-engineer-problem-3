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

resource "aws_ecs_task_definition" "test" {
  family                   = "testeapi2"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 2048
  memory                   = 4096
  execution_role_arn       = "arn:aws:iam::626109959667:role/ecsTaskExecutionRole"
  container_definitions    = <<TASK_DEFINITION
[
  {
    "name": "testeapi",
    "image": "626109959667.dkr.ecr.us-east-1.amazonaws.com/java-api:latest",
    "cpu": 2048,
    "memory": 4096,
    "essential": true,
    "portMappings": [
          {
            "containerPort": 8080,
            "hostPort": 8080
          }
        ]
  }
]
TASK_DEFINITION

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }
}


module "ecs-service" {
    source          = "./modules/ecs"
    ecs_cluster_id = module.ecs_cluster.id
    task_definition_arn = aws_ecs_task_definition.test.arn
}