aws_region = "ap-northeast-2"

vpc_cidr = "172.31.0.0/16"
az_a     = "ap-northeast-2a"
az_c     = "ap-northeast-2c"

public_subnet_a_cidr  = "172.31.0.0/20"
public_subnet_c_cidr  = "172.31.32.0/20"
private_subnet_a_cidr = "172.31.128.0/20"
private_subnet_c_cidr = "172.31.144.0/20"

vpc_name                 = "modoo-default"
igw_name                 = "modoo-igw"
public_subnet_a_name     = "modoo-default-public-a"
public_subnet_c_name     = "modoo-default-public-c"
private_subnet_a_name    = "modoo-private-a"
private_subnet_c_name    = "modoo-private-c"
public_route_table_name  = "modoo-public"
private_route_table_name = "modoo-private-rtb-a"
s3_endpoint_name         = "modoo-s3-endpoint"

alb_sg_name        = "ModooAlbGroup"
alb_sg_description = "ALB Security Group"
app_sg_name        = "ModooAppGroup"
app_sg_description = "ECS Security Group"
rds_sg_name        = "ModooRdsGroup"
rds_sg_description = "RDS Security Group"

alb_name                  = "modoo-alb-dev"
alb_tag_name              = "modoo-alb-dev"
target_group_name         = "modoo-tg-dev"
target_group_tag_name     = "modoo-tg-dev"
listener_tag_name         = "modoo-alb-listener"
https_listener_tag_name   = "modoo-alb-https-listener"
acm_certificate_arn       = "arn:aws:acm:ap-northeast-2:650177546654:certificate/c22d087a-c284-4574-a89c-759cf7ddc367"
https_listener_ssl_policy = "ELBSecurityPolicy-TLS13-1-2-Res-PQ-2025-09"
route53_hosted_zone_id    = "Z09209125CJ92OR19RX7"

ecr_image_tag_mutability = "IMMUTABLE"
ecr_image_scan_on_push   = false
ecr_max_image_count      = 20

ecs_key_pair_name               = "modoo-dev-ssh"
ecs_instance_role_name          = "ecsInstanceRole"
ecs_instance_role_policy_arn    = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
ecs_instance_profile_name       = "ecsInstanceRole"
ecs_cluster_name                = "modoo-ecs-cluster-dev"
ecs_cluster_tag_name            = "modoo-ecs-cluster-dev"
launch_template_name            = "ECSLaunchTemplate_jHFSMI7rg3iG"
launch_template_image_id        = "ami-08767fd97a1f677c7"
launch_template_instance_type   = "t4g.small"
ecs_instance_tag_name           = "ECS Instance - modoo-ecs-cluster-dev"
asg_name                        = "Infra-ECS-Cluster-modoo-ecs-cluster-dev-83711ad6-ECSAutoScalingGroup-BC1LQMGTj6KX"
asg_min_size                    = 0
asg_max_size                    = 2
asg_desired_capacity            = 1
capacity_provider_name          = "Infra-ECS-Cluster-modoo-ecs-cluster-dev-83711ad6-AsgCapacityProvider-XECa6njigrke"
additional_capacity_providers   = ["FARGATE", "FARGATE_SPOT"]
task_execution_role_name        = "ecsTaskExecutionRole"
task_execution_role_policy_arn  = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
ecs_log_group_name              = "/ecs/modoo-ecs-task-dev"
ecs_log_group_retention_in_days = 7
ecs_task_family                 = "modoo-ecs-task-dev"
ecs_task_cpu                    = "1024"
ecs_task_memory                 = "1024"
ecs_task_container_name         = "modoo-app-dev"
ecs_task_container_port         = 8080
ecs_task_port_name              = "spring-webapp-port"
ecs_task_app_protocol           = "http"
ecs_task_healthcheck_command    = "wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1"
ecs_task_definition_tag_name    = "modoo-ecs-task-dev"
ecs_service_name                = "modoo-ecs-service-dev"
ecs_service_desired_count       = 1
ecs_service_tag_name            = "modoo-ecs-service-dev"

db_subnet_group_name                = "modoo-db-subnet-group"
db_subnet_group_tag_name            = "Modoo DB subnet group"
rds_identifier                      = "modoo-rds-dev"
rds_engine                          = "mysql"
rds_engine_version                  = "8.4.7"
rds_instance_class                  = "db.t3.micro"
rds_allocated_storage               = 20
rds_storage_type                    = "gp3"
rds_iops                            = 3000
rds_storage_throughput              = 125
rds_instance_subnet_group_name      = "default-vpc-06954cfe43465a600"
rds_availability_zone               = "ap-northeast-2a"
rds_publicly_accessible             = false
rds_multi_az                        = false
rds_enabled_cloudwatch_logs_exports = ["error", "slowquery"]
rds_skip_final_snapshot             = true
rds_instance_tag_name               = "modoo-rds-dev"
