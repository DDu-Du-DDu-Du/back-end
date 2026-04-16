output "alb_dns_name" {
  value = aws_lb.modoo_alb.dns_name
}

output "alb_zone_id" {
  value = aws_lb.modoo_alb.zone_id
}

output "alb_arn" {
  value = aws_lb.modoo_alb.arn
}

output "target_group_arn" {
  value = aws_lb_target_group.modoo_tg.arn
}

output "listener_arn" {
  value = aws_lb_listener.modoo_alb_listener.arn
}

output "https_listener_arn" {
  value = aws_lb_listener.modoo_alb_https_listener.arn
}
