variable "repository_name" {
  type = string
}

variable "repository_tag_name" {
  type = string
}

variable "image_tag_mutability" {
  type = string
}

variable "image_scan_on_push" {
  type = bool
}

variable "max_image_count" {
  type = number
}
