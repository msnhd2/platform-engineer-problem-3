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

################################################################################
# LB Resources
################################################################################
resource "aws_lb" "this" {
  name     = "testeapi-alb"
  internal = false

  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = ["subnet-057120624e7d96312", "subnet-03de0c66b46a1d139"]
  enable_http2       = "false"

  enable_cross_zone_load_balancing = true
  enable_deletion_protection       = false
  tags                             = { Service = "alb", AlbType = "application" }
}

resource "aws_security_group" "alb" {
  name   = "testeapi"
  vpc_id = 	"vpc-0f62de7f0cee86007"

  ingress {
    protocol    = "tcp"
    from_port   = 8080
    to_port     = 8080
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow internet to access port 8080 for redirect."
  }

  egress {
    # TEMP for testing, should be locked to just services protocols
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"] # TODO: make sure only vpc cidr or private sunets cidrs
    description = "Allow internal communitcations."
  }
}

resource "aws_ecs_service" "mongo" {
  name            = "testeapi"
  cluster         = module.ecs_cluster.id
  task_definition = module.ecs-fargate-task-definition.task_definition_arn
  desired_count   = 1
  iam_role        = "awsvpc"

  ordered_placement_strategy {
    type  = "binpack"
    field = "cpu"
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.foo.arn
    container_name   = "testeapi"
    container_port   = 8080
  }

  placement_constraints {
    type       = "memberOf"
    expression = "attribute:ecs.availability-zone in [us-east-1a, us-east-1f]"
  }
}