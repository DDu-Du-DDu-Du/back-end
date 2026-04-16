import {
  to = module.network.aws_vpc.modoo_vpc
  id = "vpc-06954cfe43465a600"
}

import {
  to = module.network.aws_internet_gateway.modoo_igw
  id = "igw-0472d760edab6e245"
}

import {
  to = module.network.aws_subnet.modoo_public_a
  id = "subnet-09eb86ff2bd3b9c10"
}

import {
  to = module.network.aws_subnet.modoo_public_c
  id = "subnet-0e90f4beefe213804"
}

import {
  to = module.network.aws_subnet.modoo_private_a
  id = "subnet-09a1ec3544fb7ac81"
}

import {
  to = module.network.aws_subnet.modoo_private_c
  id = "subnet-0771d19b1d7e9bd17"
}

import {
  to = module.network.aws_route_table.modoo_public_rt
  id = "rtb-0c1aee5e61729f4f0"
}

import {
  to = module.network.aws_route_table.modoo_private_rt_a
  id = "rtb-08d1e34d9ea20db49"
}

import {
  to = module.network.aws_route_table_association.public_a
  id = "subnet-09eb86ff2bd3b9c10/rtb-0c1aee5e61729f4f0"
}

import {
  to = module.network.aws_route_table_association.public_c
  id = "subnet-0e90f4beefe213804/rtb-0c1aee5e61729f4f0"
}

import {
  to = module.network.aws_route_table_association.private_a
  id = "subnet-09a1ec3544fb7ac81/rtb-08d1e34d9ea20db49"
}

import {
  to = module.network.aws_route_table_association.private_c
  id = "subnet-0771d19b1d7e9bd17/rtb-08d1e34d9ea20db49"
}

import {
  to = module.network.aws_vpc_endpoint.s3_gateway
  id = "vpce-0100e696d305eff17"
}

import {
  to = module.security.aws_security_group.modoo_alb_group
  id = "sg-080cb3c10b97f72de"
}

import {
  to = module.security.aws_security_group.modoo_app_group
  id = "sg-04ef3c0711e9c4c72"
}

import {
  to = module.security.aws_security_group.modoo_rds_group
  id = "sg-003e26571b51c8d31"
}

import {
  to = module.security.aws_vpc_security_group_ingress_rule.alb_http
  id = "sgr-0bdcfbae0f44684b6"
}

import {
  to = module.security.aws_vpc_security_group_ingress_rule.alb_https
  id = "sgr-0c1272e5a4bab3a90"
}

import {
  to = module.security.aws_vpc_security_group_egress_rule.alb_to_ecs
  id = "sgr-0b33b28fbc9f8e078"
}

import {
  to = module.security.aws_vpc_security_group_ingress_rule.ecs_from_alb
  id = "sgr-05ca8a9a245eef501"
}

import {
  to = module.security.aws_vpc_security_group_egress_rule.ecs_http_out
  id = "sgr-094b0b6acec2fb715"
}

import {
  to = module.security.aws_vpc_security_group_egress_rule.ecs_https_out
  id = "sgr-079f302e9ba368252"
}

import {
  to = module.security.aws_vpc_security_group_egress_rule.ecs_to_rds
  id = "sgr-071e9e0ee965dc4f8"
}

import {
  to = module.security.aws_vpc_security_group_egress_rule.ecs_to_alb_response
  id = "sgr-0c254680202ccd259"
}

import {
  to = module.security.aws_vpc_security_group_ingress_rule.rds_from_ecs
  id = "sgr-085f239635ca2d73a"
}

import {
  to = module.security.aws_vpc_security_group_egress_rule.rds_to_ecs_response
  id = "sgr-0305352074024e850"
}

import {
  to = module.rds.aws_db_subnet_group.modoo_db_subnet_group
  id = "modoo-db-subnet-group"
}

import {
  to = module.rds.aws_db_instance.modoo_rds
  id = "modoo-rds-dev"
}

import {
  to = module.alb.aws_lb.modoo_alb
  id = "arn:aws:elasticloadbalancing:ap-northeast-2:650177546654:loadbalancer/app/modoo-alb-dev/b084a0f076e41292"
}

import {
  to = module.alb.aws_lb_target_group.modoo_tg
  id = "arn:aws:elasticloadbalancing:ap-northeast-2:650177546654:targetgroup/modoo-tg-dev/92fecb439e560b4f"
}

import {
  to = module.alb.aws_lb_listener.modoo_alb_listener
  id = "arn:aws:elasticloadbalancing:ap-northeast-2:650177546654:listener/app/modoo-alb-dev/b084a0f076e41292/9d0192afe760f6da"
}

import {
  to = module.alb.aws_lb_listener.modoo_alb_https_listener
  id = "arn:aws:elasticloadbalancing:ap-northeast-2:650177546654:listener/app/modoo-alb-dev/b084a0f076e41292/28094a24db0567f2"
}

import {
  to = module.ecs.aws_iam_role.ecs_instance_role
  id = "ecsInstanceRole"
}

import {
  to = module.ecs.aws_iam_role_policy_attachment.ecs_instance_role_policy
  id = "ecsInstanceRole/arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

import {
  to = module.ecs.aws_iam_instance_profile.ecs_instance_profile
  id = "ecsInstanceRole"
}

import {
  to = module.ecs.aws_iam_role.ecs_task_execution_role
  id = "ecsTaskExecutionRole"
}

import {
  to = module.ecs.aws_iam_role_policy_attachment.ecs_task_execution_role_policy
  id = "ecsTaskExecutionRole/arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

import {
  to = module.ecs.aws_ecs_cluster.modoo_ecs_cluster
  id = "modoo-ecs-cluster-dev"
}

import {
  to = module.ecs.aws_launch_template.ecs_launch_template
  id = "lt-029787b912cd9f1fd"
}

import {
  to = module.ecs.aws_autoscaling_group.ecs_asg
  id = "Infra-ECS-Cluster-modoo-ecs-cluster-dev-83711ad6-ECSAutoScalingGroup-BC1LQMGTj6KX"
}

import {
  to = module.ecs.aws_ecs_capacity_provider.modoo_capacity_provider
  id = "Infra-ECS-Cluster-modoo-ecs-cluster-dev-83711ad6-AsgCapacityProvider-XECa6njigrke"
}

import {
  to = module.ecs.aws_ecs_cluster_capacity_providers.modoo_cluster_cp
  id = "modoo-ecs-cluster-dev"
}

import {
  to = module.ecs.aws_cloudwatch_log_group.ecs_log_group
  id = "/ecs/modoo-ecs-task-dev"
}

import {
  to = module.ecs.aws_ecs_task_definition.modoo_task
  id = "arn:aws:ecs:ap-northeast-2:650177546654:task-definition/modoo-ecs-task-dev:7"
}

import {
  to = module.ecs.aws_ecs_service.modoo_ecs_service
  id = "modoo-ecs-cluster-dev/modoo-ecs-service-dev"
}

import {
  to = module.ecr.aws_ecr_repository.modoo_spring_app
  id = "modoo/dev/spring-app"
}

import {
  to = module.ecr.aws_ecr_lifecycle_policy.modoo_spring_app
  id = "modoo/dev/spring-app"
}

import {
  to = module.route53_dev.aws_route53_record.alias_a["dev_api"]
  id = "Z09209125CJ92OR19RX7_dev.api.mo-doo.com_A"
}

import {
  to = module.route53_dev.aws_route53_record.alias_a["www_dev_api"]
  id = "Z09209125CJ92OR19RX7_www.dev.api.mo-doo.com_A"
}
