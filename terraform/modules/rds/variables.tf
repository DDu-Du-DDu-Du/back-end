variable "db_password" {
  type      = string
  sensitive = true
}

variable "rds_security_group_id" {
  type = string
}

variable "private_subnet_ids" {
  type = list(string)
}

variable "db_subnet_group_name" {
  type = string
}

variable "db_subnet_group_tag_name" {
  type = string
}

variable "identifier" {
  type = string
}

variable "engine" {
  type = string
}

variable "engine_version" {
  type = string
}

variable "instance_class" {
  type = string
}

variable "allocated_storage" {
  type = number
}

variable "storage_type" {
  type = string
}

variable "iops" {
  type = number
}

variable "storage_throughput" {
  type = number
}

variable "db_name" {
  type = string
}

variable "username" {
  type = string
}

variable "instance_subnet_group_name" {
  type = string
}

variable "availability_zone" {
  type = string
}

variable "publicly_accessible" {
  type = bool
}

variable "multi_az" {
  type = bool
}

variable "enabled_cloudwatch_logs_exports" {
  type = list(string)
}

variable "skip_final_snapshot" {
  type = bool
}

variable "instance_tag_name" {
  type = string
}
