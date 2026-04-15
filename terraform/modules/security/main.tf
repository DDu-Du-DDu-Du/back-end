resource "aws_security_group" "modoo_alb_group" {
  name        = var.alb_sg_name
  description = var.alb_sg_description
  vpc_id      = var.vpc_id

  tags = {
    Name = var.alb_sg_name
  }
}

resource "aws_security_group" "modoo_app_group" {
  name        = var.app_sg_name
  description = var.app_sg_description
  vpc_id      = var.vpc_id

  tags = {
    Name = var.app_sg_name
  }
}

resource "aws_security_group" "modoo_rds_group" {
  name        = var.rds_sg_name
  description = var.rds_sg_description
  vpc_id      = var.vpc_id

  tags = {
    Name = var.rds_sg_name
  }
}

resource "aws_vpc_security_group_ingress_rule" "alb_http" {
  security_group_id = aws_security_group.modoo_alb_group.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 80
  to_port           = 80
  ip_protocol       = "tcp"
  description       = "From Internet to ALB Redirect"
}

resource "aws_vpc_security_group_ingress_rule" "alb_https" {
  security_group_id = aws_security_group.modoo_alb_group.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 443
  to_port           = 443
  ip_protocol       = "tcp"
  description       = "From Internet to ALB"
}

resource "aws_vpc_security_group_egress_rule" "alb_to_ecs" {
  security_group_id            = aws_security_group.modoo_alb_group.id
  referenced_security_group_id = aws_security_group.modoo_app_group.id
  from_port                    = 0
  to_port                      = 65535
  ip_protocol                  = "tcp"
  description                  = "From ALB to ECS"
}

resource "aws_vpc_security_group_ingress_rule" "ecs_from_alb" {
  security_group_id            = aws_security_group.modoo_app_group.id
  referenced_security_group_id = aws_security_group.modoo_alb_group.id
  from_port                    = 0
  to_port                      = 65535
  ip_protocol                  = "tcp"
  description                  = "From ALB to ECS"
}

resource "aws_vpc_security_group_egress_rule" "ecs_http_out" {
  security_group_id = aws_security_group.modoo_app_group.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 80
  to_port           = 80
  ip_protocol       = "tcp"
  description       = "From ECS to Internet"
}

resource "aws_vpc_security_group_egress_rule" "ecs_https_out" {
  security_group_id = aws_security_group.modoo_app_group.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 443
  to_port           = 443
  ip_protocol       = "tcp"
  description       = "From ECS to Internet"
}

resource "aws_vpc_security_group_egress_rule" "ecs_to_rds" {
  security_group_id            = aws_security_group.modoo_app_group.id
  referenced_security_group_id = aws_security_group.modoo_rds_group.id
  from_port                    = 3306
  to_port                      = 3306
  ip_protocol                  = "tcp"
  description                  = "From ECS to RDS"
}

resource "aws_vpc_security_group_egress_rule" "ecs_to_alb_response" {
  security_group_id            = aws_security_group.modoo_app_group.id
  referenced_security_group_id = aws_security_group.modoo_alb_group.id
  from_port                    = 1024
  to_port                      = 65535
  ip_protocol                  = "tcp"
  description                  = "From ECS to ALB Response"
}

resource "aws_vpc_security_group_ingress_rule" "rds_from_ecs" {
  security_group_id            = aws_security_group.modoo_rds_group.id
  referenced_security_group_id = aws_security_group.modoo_app_group.id
  from_port                    = 3306
  to_port                      = 3306
  ip_protocol                  = "tcp"
  description                  = "From ECS to RDS"
}

resource "aws_vpc_security_group_egress_rule" "rds_to_ecs_response" {
  security_group_id            = aws_security_group.modoo_rds_group.id
  referenced_security_group_id = aws_security_group.modoo_app_group.id
  from_port                    = 1024
  to_port                      = 65535
  ip_protocol                  = "tcp"
  description                  = "From RDS to ECS"
}
