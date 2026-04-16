resource "aws_route53_record" "alias_a" {
  for_each = var.alias_a_records

  zone_id = var.hosted_zone_id
  name    = each.value.name
  type    = "A"

  alias {
    name                   = each.value.dns_name
    zone_id                = each.value.hosted_zone_id
    evaluate_target_health = each.value.evaluate_target_health
  }
}
