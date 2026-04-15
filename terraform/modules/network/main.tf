resource "aws_vpc" "modoo_vpc" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = var.vpc_name
  }
}

resource "aws_internet_gateway" "modoo_igw" {
  vpc_id = aws_vpc.modoo_vpc.id

  tags = {
    Name = var.igw_name
  }
}

resource "aws_subnet" "modoo_public_a" {
  vpc_id                  = aws_vpc.modoo_vpc.id
  cidr_block              = var.public_subnet_a_cidr
  availability_zone       = var.az_a
  map_public_ip_on_launch = false

  tags = {
    Name = var.public_subnet_a_name
  }
}

resource "aws_subnet" "modoo_public_c" {
  vpc_id                  = aws_vpc.modoo_vpc.id
  cidr_block              = var.public_subnet_c_cidr
  availability_zone       = var.az_c
  map_public_ip_on_launch = false

  tags = {
    Name = var.public_subnet_c_name
  }
}

resource "aws_subnet" "modoo_private_a" {
  vpc_id            = aws_vpc.modoo_vpc.id
  cidr_block        = var.private_subnet_a_cidr
  availability_zone = var.az_a

  tags = {
    Name = var.private_subnet_a_name
  }
}

resource "aws_subnet" "modoo_private_c" {
  vpc_id            = aws_vpc.modoo_vpc.id
  cidr_block        = var.private_subnet_c_cidr
  availability_zone = var.az_c

  tags = {
    Name = var.private_subnet_c_name
  }
}

resource "aws_route_table" "modoo_public_rt" {
  vpc_id = aws_vpc.modoo_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.modoo_igw.id
  }

  tags = {
    Name = var.public_route_table_name
  }
}

resource "aws_route_table" "modoo_private_rt_a" {
  vpc_id = aws_vpc.modoo_vpc.id

  tags = {
    Name = var.private_route_table_name
  }
}

resource "aws_route_table_association" "public_a" {
  subnet_id      = aws_subnet.modoo_public_a.id
  route_table_id = aws_route_table.modoo_public_rt.id
}

resource "aws_route_table_association" "public_c" {
  subnet_id      = aws_subnet.modoo_public_c.id
  route_table_id = aws_route_table.modoo_public_rt.id
}

resource "aws_route_table_association" "private_a" {
  subnet_id      = aws_subnet.modoo_private_a.id
  route_table_id = aws_route_table.modoo_private_rt_a.id
}

resource "aws_route_table_association" "private_c" {
  subnet_id      = aws_subnet.modoo_private_c.id
  route_table_id = aws_route_table.modoo_private_rt_a.id
}

resource "aws_vpc_endpoint" "s3_gateway" {
  vpc_id            = aws_vpc.modoo_vpc.id
  service_name      = "com.amazonaws.${var.aws_region}.s3"
  vpc_endpoint_type = "Gateway"
  route_table_ids   = [aws_route_table.modoo_private_rt_a.id]

  tags = {
    Name = var.s3_endpoint_name
  }
}
