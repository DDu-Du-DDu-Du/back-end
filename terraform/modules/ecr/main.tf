resource "aws_ecr_repository" "modoo_spring_app" {
  name                 = var.repository_name
  image_tag_mutability = var.image_tag_mutability

  image_scanning_configuration {
    scan_on_push = var.image_scan_on_push
  }

  encryption_configuration {
    encryption_type = "AES256"
  }

  tags = {
    Name = var.repository_tag_name
  }
}

resource "aws_ecr_lifecycle_policy" "modoo_spring_app" {
  repository = aws_ecr_repository.modoo_spring_app.name
  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Image pool management with ${var.max_image_count} max images"
        selection = {
          tagStatus   = "any"
          countType   = "imageCountMoreThan"
          countNumber = var.max_image_count
        }
        action = {
          type = "expire"
        }
      }
    ]
  })
}
