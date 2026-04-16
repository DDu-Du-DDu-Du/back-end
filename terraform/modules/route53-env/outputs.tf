output "record_fqdns" {
  value = [for record in aws_route53_record.alias_a : record.fqdn]
}
