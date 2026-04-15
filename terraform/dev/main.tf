module "network" {
  source = "../modules/network"

  aws_region = var.aws_region

  vpc_cidr = var.vpc_cidr
  az_a     = var.az_a
  az_c     = var.az_c

  public_subnet_a_cidr  = var.public_subnet_a_cidr
  public_subnet_c_cidr  = var.public_subnet_c_cidr
  private_subnet_a_cidr = var.private_subnet_a_cidr
  private_subnet_c_cidr = var.private_subnet_c_cidr

  vpc_name                 = var.vpc_name
  igw_name                 = var.igw_name
  public_subnet_a_name     = var.public_subnet_a_name
  public_subnet_c_name     = var.public_subnet_c_name
  private_subnet_a_name    = var.private_subnet_a_name
  private_subnet_c_name    = var.private_subnet_c_name
  public_route_table_name  = var.public_route_table_name
  private_route_table_name = var.private_route_table_name
  s3_endpoint_name         = var.s3_endpoint_name
}

module "security" {
  source = "../modules/security"

  vpc_id = module.network.vpc_id

  alb_sg_name        = var.alb_sg_name
  alb_sg_description = var.alb_sg_description
  app_sg_name        = var.app_sg_name
  app_sg_description = var.app_sg_description
  rds_sg_name        = var.rds_sg_name
  rds_sg_description = var.rds_sg_description
}

module "ecr" {
  source = "../modules/ecr"

  repository_name      = var.ecr_repository_name
  repository_tag_name  = var.ecr_repository_tag_name
  image_tag_mutability = var.ecr_image_tag_mutability
  image_scan_on_push   = var.ecr_image_scan_on_push
  max_image_count      = var.ecr_max_image_count
}

module "alb" {
  source = "../modules/alb"

  alb_name              = var.alb_name
  alb_tag_name          = var.alb_tag_name
  target_group_name     = var.target_group_name
  target_group_tag_name = var.target_group_tag_name
  listener_tag_name     = var.listener_tag_name
  vpc_id                = module.network.vpc_id
  security_group_id     = module.security.alb_security_group_id
  public_subnet_ids = [
    module.network.public_subnet_a_id,
    module.network.public_subnet_c_id
  ]
}

module "ecs" {
  source = "../modules/ecs"

  key_pair_name         = var.ecs_key_pair_name
  app_security_group_id = module.security.app_security_group_id
  asg_subnet_ids        = [module.network.public_subnet_a_id]

  ecs_instance_role_name       = var.ecs_instance_role_name
  ecs_instance_role_policy_arn = var.ecs_instance_role_policy_arn
  ecs_instance_profile_name    = var.ecs_instance_profile_name

  ecs_cluster_name     = var.ecs_cluster_name
  ecs_cluster_tag_name = var.ecs_cluster_tag_name

  launch_template_name          = var.launch_template_name
  launch_template_image_id      = var.launch_template_image_id
  launch_template_instance_type = var.launch_template_instance_type
  ecs_instance_tag_name         = var.ecs_instance_tag_name

  asg_name             = var.asg_name
  asg_min_size         = var.asg_min_size
  asg_max_size         = var.asg_max_size
  asg_desired_capacity = var.asg_desired_capacity

  capacity_provider_name        = var.capacity_provider_name
  additional_capacity_providers = var.additional_capacity_providers

  task_execution_role_name       = var.task_execution_role_name
  task_execution_role_policy_arn = var.task_execution_role_policy_arn

  log_group_name              = var.ecs_log_group_name
  log_group_retention_in_days = var.ecs_log_group_retention_in_days

  task_family              = var.ecs_task_family
  task_cpu                 = var.ecs_task_cpu
  task_memory              = var.ecs_task_memory
  task_container_name      = var.ecs_task_container_name
  task_container_port      = var.ecs_task_container_port
  task_port_name           = var.ecs_task_port_name
  task_app_protocol        = var.ecs_task_app_protocol
  task_image               = "${module.ecr.repository_url}:${var.ecs_image_tag}"
  task_healthcheck_command = var.ecs_task_healthcheck_command
  task_definition_tag_name = var.ecs_task_definition_tag_name

  ecs_service_name          = var.ecs_service_name
  ecs_service_desired_count = var.ecs_service_desired_count
  ecs_service_tag_name      = var.ecs_service_tag_name
  target_group_arn          = module.alb.target_group_arn

  depends_on = [module.alb]
}

module "rds" {
  source = "../modules/rds"

  db_password = var.db_password

  rds_security_group_id = module.security.rds_security_group_id
  private_subnet_ids = [
    module.network.private_subnet_a_id,
    module.network.private_subnet_c_id
  ]

  db_subnet_group_name     = var.db_subnet_group_name
  db_subnet_group_tag_name = var.db_subnet_group_tag_name

  identifier     = var.rds_identifier
  engine         = var.rds_engine
  engine_version = var.rds_engine_version
  instance_class = var.rds_instance_class

  allocated_storage  = var.rds_allocated_storage
  storage_type       = var.rds_storage_type
  iops               = var.rds_iops
  storage_throughput = var.rds_storage_throughput

  db_name  = var.rds_db_name
  username = var.rds_username

  instance_subnet_group_name = var.rds_instance_subnet_group_name
  availability_zone          = var.rds_availability_zone
  publicly_accessible        = var.rds_publicly_accessible
  multi_az                   = var.rds_multi_az

  enabled_cloudwatch_logs_exports = var.rds_enabled_cloudwatch_logs_exports

  skip_final_snapshot = var.rds_skip_final_snapshot
  instance_tag_name   = var.rds_instance_tag_name
}
