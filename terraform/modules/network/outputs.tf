output "vpc_id" {
  value = aws_vpc.modoo_vpc.id
}

output "public_subnet_a_id" {
  value = aws_subnet.modoo_public_a.id
}

output "public_subnet_c_id" {
  value = aws_subnet.modoo_public_c.id
}

output "private_subnet_a_id" {
  value = aws_subnet.modoo_private_a.id
}

output "private_subnet_c_id" {
  value = aws_subnet.modoo_private_c.id
}
