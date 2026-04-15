variable "key_pair_name" {
  type = string
}

variable "app_security_group_id" {
  type = string
}

variable "asg_subnet_ids" {
  type = list(string)
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

variable "log_group_name" {
  type = string
}

variable "log_group_retention_in_days" {
  type = number
}

variable "task_family" {
  type = string
}

variable "task_cpu" {
  type = string
}

variable "task_memory" {
  type = string
}

variable "task_container_name" {
  type = string
}

variable "task_container_port" {
  type = number
}

variable "task_port_name" {
  type = string
}

variable "task_app_protocol" {
  type = string
}

variable "task_image" {
  type = string
}

variable "task_healthcheck_command" {
  type = string
}

variable "task_definition_tag_name" {
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

variable "target_group_arn" {
  type = string
}
