output "cluster_name" {
  value = aws_ecs_cluster.modoo_ecs_cluster.name
}

output "service_name" {
  value = aws_ecs_service.modoo_ecs_service.name
}

output "capacity_provider_name" {
  value = aws_ecs_capacity_provider.modoo_capacity_provider.name
}
