resource "aws_route53_zone" "this" {
  name = var.hosted_zone_name

  comment = var.hosted_zone_comment
}

resource "aws_route53_record" "hosted_zone_ns" {
  zone_id = aws_route53_zone.this.zone_id
  name    = aws_route53_zone.this.name
  type    = "NS"
  ttl     = 172800
  records = var.name_servers
}

resource "aws_route53_record" "hosted_zone_soa" {
  zone_id = aws_route53_zone.this.zone_id
  name    = aws_route53_zone.this.name
  type    = "SOA"
  ttl     = 900
  records = [var.soa_value]
}

resource "aws_route53_record" "acm_validation" {
  for_each = var.acm_validation_records

  zone_id = aws_route53_zone.this.zone_id
  name    = each.value.name
  type    = each.value.type
  ttl     = 300
  records = [each.value.value]
}
