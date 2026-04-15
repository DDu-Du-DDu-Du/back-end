output "alb_security_group_id" {
  value = aws_security_group.modoo_alb_group.id
}

output "app_security_group_id" {
  value = aws_security_group.modoo_app_group.id
}

output "rds_security_group_id" {
  value = aws_security_group.modoo_rds_group.id
}
