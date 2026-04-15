data "aws_key_pair" "modoo_ssh" {
  key_name = var.key_pair_name
}

resource "aws_iam_role" "ecs_instance_role" {
  name = var.ecs_instance_role_name

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_instance_role_policy" {
  role       = aws_iam_role.ecs_instance_role.name
  policy_arn = var.ecs_instance_role_policy_arn
}

resource "aws_iam_instance_profile" "ecs_instance_profile" {
  name = var.ecs_instance_profile_name
  role = aws_iam_role.ecs_instance_role.name
}

resource "aws_ecs_cluster" "modoo_ecs_cluster" {
  name = var.ecs_cluster_name

  configuration {
    execute_command_configuration {
      logging = "DEFAULT"
    }
  }

  tags = {
    Name = var.ecs_cluster_tag_name
  }
}

resource "aws_launch_template" "ecs_launch_template" {
  name          = var.launch_template_name
  image_id      = var.launch_template_image_id
  instance_type = var.launch_template_instance_type
  key_name      = data.aws_key_pair.modoo_ssh.key_name

  iam_instance_profile {
    arn = aws_iam_instance_profile.ecs_instance_profile.arn
  }

  network_interfaces {
    associate_public_ip_address = true
    delete_on_termination       = true
    device_index                = 0
    security_groups             = [var.app_security_group_id]
  }

  user_data = base64encode(
    "#!/bin/bash\r\necho ECS_CLUSTER=${aws_ecs_cluster.modoo_ecs_cluster.name} >> /etc/ecs/ecs.config\r\n"
  )

  tag_specifications {
    resource_type = "instance"
    tags = {
      Name = var.ecs_instance_tag_name
    }
  }
}

resource "aws_autoscaling_group" "ecs_asg" {
  name                = var.asg_name
  vpc_zone_identifier = var.asg_subnet_ids
  min_size            = var.asg_min_size
  max_size            = var.asg_max_size
  desired_capacity    = var.asg_desired_capacity

  launch_template {
    id      = aws_launch_template.ecs_launch_template.id
    version = "$Latest"
  }

  tag {
    key                 = "Name"
    value               = var.ecs_instance_tag_name
    propagate_at_launch = true
  }

  tag {
    key                 = "AmazonECSManaged"
    value               = ""
    propagate_at_launch = true
  }

  lifecycle {
    ignore_changes = [
      force_delete,
      force_delete_warm_pool,
      ignore_failed_scaling_activities,
      wait_for_capacity_timeout
    ]
  }
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name = var.task_execution_role_name

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = var.task_execution_role_policy_arn
}

resource "aws_cloudwatch_log_group" "ecs_log_group" {
  name              = var.log_group_name
  retention_in_days = var.log_group_retention_in_days

  tags = {
    Name = "modoo-ecs-logs"
  }
}

resource "aws_ecs_task_definition" "modoo_task" {
  family                   = var.task_family
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]
  cpu                      = var.task_cpu
  memory                   = var.task_memory
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "ARM64"
  }

  container_definitions = jsonencode([
    {
      name  = var.task_container_name
      image = var.task_image
      cpu   = 0

      portMappings = [
        {
          name          = var.task_port_name
          containerPort = var.task_container_port
          hostPort      = 0
          protocol      = "tcp"
          appProtocol   = var.task_app_protocol
        }
      ]

      environment      = []
      environmentFiles = []
      mountPoints      = []
      volumesFrom      = []
      ulimits          = []

      healthCheck = {
        command = [
          "CMD-SHELL",
          var.task_healthcheck_command
        ]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 60
      }

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.ecs_log_group.name
          "awslogs-create-group"  = "true"
          "awslogs-region"        = "ap-northeast-2"
          "awslogs-stream-prefix" = "ecs"
        }
        secretOptions = []
      }

      essential      = true
      systemControls = []
    }
  ])

  tags = {
    Name = var.task_definition_tag_name
  }
}

resource "aws_ecs_capacity_provider" "modoo_capacity_provider" {
  name = var.capacity_provider_name

  auto_scaling_group_provider {
    auto_scaling_group_arn         = aws_autoscaling_group.ecs_asg.arn
    managed_termination_protection = "DISABLED"

    managed_scaling {
      status                    = "ENABLED"
      target_capacity           = 100
      minimum_scaling_step_size = 1
      maximum_scaling_step_size = 10000
      instance_warmup_period    = 300
    }

    managed_draining = "ENABLED"
  }
}

resource "aws_ecs_cluster_capacity_providers" "modoo_cluster_cp" {
  cluster_name = aws_ecs_cluster.modoo_ecs_cluster.name

  capacity_providers = concat(
    [aws_ecs_capacity_provider.modoo_capacity_provider.name],
    var.additional_capacity_providers
  )

  default_capacity_provider_strategy {
    base              = 0
    weight            = 1
    capacity_provider = aws_ecs_capacity_provider.modoo_capacity_provider.name
  }
}

resource "aws_ecs_service" "modoo_ecs_service" {
  name            = var.ecs_service_name
  cluster         = aws_ecs_cluster.modoo_ecs_cluster.id
  task_definition = "${aws_ecs_task_definition.modoo_task.family}:${aws_ecs_task_definition.modoo_task.revision}"
  desired_count   = var.ecs_service_desired_count

  capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.modoo_capacity_provider.name
    weight            = 1
    base              = 0
  }

  load_balancer {
    target_group_arn = var.target_group_arn
    container_name   = var.task_container_name
    container_port   = var.task_container_port
  }

  tags = {
    Name = var.ecs_service_tag_name
  }
}
