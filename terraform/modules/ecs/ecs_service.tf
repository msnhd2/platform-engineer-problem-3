
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

# resource "aws_lb_target_group_attachment" "this" {
#   target_group_arn = aws_lb_target_group.alb-javapi.arn
#   target_id        = aws_lb.this.arn
#   port             = 8080
# }

resource "aws_lb_listener" "ops_alb_listener_8080" {
  load_balancer_arn = aws_lb.this.arn
  port              = "8080"
  protocol          = "HTTP"
  #certificate_arn   = "${var.elk_cert_arn}"

  default_action {
    target_group_arn = aws_lb_target_group.alb-javapi.arn
    type             = "forward"
  }
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

resource "aws_lb_target_group" "alb-javapi" {
  name        = "testeapi-alb"
  target_type = "ip"
  port        = 80
  protocol    = "HTTP"
  vpc_id      = "vpc-0f62de7f0cee86007"
  health_check {
    path                = "/app/actuator/health"
    port                = 8080
    protocol            = "HTTP"
    healthy_threshold   = 3
    unhealthy_threshold = 3
    matcher             = "200"
  }
}

resource "aws_ecs_service" "testeapi" {
  name            = "testeapi"
  cluster         = var.ecs_cluster_id
  task_definition = var.task_definition_arn
  desired_count   = 1
  network_configuration {
      subnets = ["subnet-057120624e7d96312", "subnet-03de0c66b46a1d139"]
      assign_public_ip = true
      security_groups = [aws_security_group.alb.id]
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.alb-javapi.arn
    container_name   = "testeapi"
    container_port   = 8080
  }
}
