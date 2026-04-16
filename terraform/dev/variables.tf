variable "aws_region" {
  type = string
}

variable "vpc_cidr" {
  type = string
}

variable "az_a" {
  type = string
}

variable "az_c" {
  type = string
}

variable "public_subnet_a_cidr" {
  type = string
}

variable "public_subnet_c_cidr" {
  type = string
}

variable "private_subnet_a_cidr" {
  type = string
}

variable "private_subnet_c_cidr" {
  type = string
}

variable "vpc_name" {
  type = string
}

variable "igw_name" {
  type = string
}

variable "public_subnet_a_name" {
  type = string
}

variable "public_subnet_c_name" {
  type = string
}

variable "private_subnet_a_name" {
  type = string
}

variable "private_subnet_c_name" {
  type = string
}

variable "public_route_table_name" {
  type = string
}

variable "private_route_table_name" {
  type = string
}

variable "s3_endpoint_name" {
  type = string
}

variable "alb_sg_name" {
  type = string
}

variable "alb_sg_description" {
  type = string
}

variable "app_sg_name" {
  type = string
}

variable "app_sg_description" {
  type = string
}

variable "rds_sg_name" {
  type = string
}

variable "rds_sg_description" {
  type = string
}

variable "alb_name" {
  type = string
}

variable "alb_tag_name" {
  type = string
}

variable "target_group_name" {
  type = string
}

variable "target_group_tag_name" {
  type = string
}

variable "listener_tag_name" {
  type = string
}

variable "https_listener_tag_name" {
  type = string
}

variable "acm_certificate_arn" {
  type = string
}

variable "https_listener_ssl_policy" {
  type = string
}

variable "route53_hosted_zone_id" {
  type = string
}

variable "ecr_repository_name" {
  type = string
}

variable "ecr_repository_tag_name" {
  type = string
}

variable "ecr_image_tag_mutability" {
  type = string
}

variable "ecr_image_scan_on_push" {
  type = bool
}

variable "ecr_max_image_count" {
  type = number
}

variable "ecs_key_pair_name" {
  type = string
}

variable "ecs_instance_role_name" {
  type = string
}

variable "ecs_instance_role_policy_arn" {
  type = string
}

variable "ecs_instance_profile_name" {
  type = string
}

variable "ecs_cluster_name" {
  type = string
}

variable "ecs_cluster_tag_name" {
  type = string
}

variable "launch_template_name" {
  type = string
}

variable "launch_template_image_id" {
  type = string
}

variable "launch_template_instance_type" {
  type = string
}

variable "ecs_instance_tag_name" {
  type = string
}

variable "asg_name" {
  type = string
}

variable "asg_min_size" {
  type = number
}

variable "asg_max_size" {
  type = number
}

variable "asg_desired_capacity" {
  type = number
}

variable "capacity_provider_name" {
  type = string
}

variable "additional_capacity_providers" {
  type = list(string)
}

variable "task_execution_role_name" {
  type = string
}

variable "task_execution_role_policy_arn" {
  type = string
}

variable "ecs_log_group_name" {
  type = string
}

variable "ecs_log_group_retention_in_days" {
  type = number
}

variable "ecs_task_family" {
  type = string
}

variable "ecs_task_cpu" {
  type = string
}

variable "ecs_task_memory" {
  type = string
}

variable "ecs_task_container_name" {
  type = string
}

variable "ecs_task_container_port" {
  type = number
}

variable "ecs_task_port_name" {
  type = string
}

variable "ecs_task_app_protocol" {
  type = string
}

variable "ecs_image_tag" {
  type = string
}

variable "ecs_task_healthcheck_command" {
  type = string
}

variable "ecs_task_definition_tag_name" {
  type = string
}

variable "ecs_service_name" {
  type = string
}

variable "ecs_service_desired_count" {
  type = number
}

variable "ecs_service_tag_name" {
  type = string
}

variable "db_subnet_group_name" {
  type = string
}

variable "db_subnet_group_tag_name" {
  type = string
}

variable "rds_identifier" {
  type = string
}

variable "rds_engine" {
  type = string
}

variable "rds_engine_version" {
  type = string
}

variable "rds_instance_class" {
  type = string
}

variable "rds_allocated_storage" {
  type = number
}

variable "rds_storage_type" {
  type = string
}

variable "rds_iops" {
  type = number
}

variable "rds_storage_throughput" {
  type = number
}

variable "rds_db_name" {
  type = string
}

variable "rds_username" {
  type = string
}

variable "rds_instance_subnet_group_name" {
  type = string
}

variable "rds_availability_zone" {
  type = string
}

variable "rds_publicly_accessible" {
  type = bool
}

variable "rds_multi_az" {
  type = bool
}

variable "rds_enabled_cloudwatch_logs_exports" {
  type = list(string)
}

variable "rds_skip_final_snapshot" {
  type = bool
}

variable "rds_instance_tag_name" {
  type = string
}

variable "db_password" {
  type      = string
  sensitive = true
}
