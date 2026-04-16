variable "hosted_zone_name" {
  type = string
}

variable "hosted_zone_comment" {
  type = string
}

variable "name_servers" {
  type = list(string)
}

variable "soa_value" {
  type = string
}

variable "acm_validation_records" {
  type = map(object({
    name  = string
    type  = string
    value = string
  }))
}
