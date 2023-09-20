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