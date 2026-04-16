variable "hosted_zone_id" {
  type = string
}

variable "alias_a_records" {
  type = map(object({
    name                   = string
    dns_name               = string
    hosted_zone_id         = string
    evaluate_target_health = bool
  }))
}
