data "aws_acm_certificate" "modoo" {
  domain      = var.hosted_zone_name
  statuses    = ["ISSUED"]
  types       = ["AMAZON_ISSUED"]
  most_recent = true
}

check "acm_certificate_arn_match" {
  assert {
    condition     = data.aws_acm_certificate.modoo.arn == var.acm_certificate_arn
    error_message = "조회된 ACM 인증서 ARN이 기대값과 다릅니다."
  }
}

module "route53_common" {
  source = "../modules/route53-common"

  hosted_zone_name       = var.hosted_zone_name
  hosted_zone_comment    = var.hosted_zone_comment
  name_servers           = var.hosted_zone_name_servers
  soa_value              = var.hosted_zone_soa_value
  acm_validation_records = var.acm_validation_records
}
