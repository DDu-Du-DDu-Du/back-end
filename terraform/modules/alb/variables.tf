variable "alb_name" {
  type = string
}

variable "alb_tag_name" {
  type = string
}

variable "target_group_name" {
  type = string
}

variable "target_group_tag_name" {
  type = string
}

variable "listener_tag_name" {
  type = string
}

variable "https_listener_tag_name" {
  type = string
}

variable "acm_certificate_arn" {
  type = string
}

variable "https_listener_ssl_policy" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "security_group_id" {
  type = string
}

variable "public_subnet_ids" {
  type = list(string)
}
