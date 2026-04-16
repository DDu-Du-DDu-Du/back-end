aws_region = "ap-northeast-2"

hosted_zone_name    = "mo-doo.com"
hosted_zone_comment = "HostedZone created by Route53 Registrar"
hosted_zone_name_servers = [
  "ns-848.awsdns-42.net.",
  "ns-265.awsdns-33.com.",
  "ns-1645.awsdns-13.co.uk.",
  "ns-1087.awsdns-07.org."
]
hosted_zone_soa_value = "ns-848.awsdns-42.net. awsdns-hostmaster.amazon.com. 1 7200 900 1209600 86400"

acm_certificate_arn = "arn:aws:acm:ap-northeast-2:650177546654:certificate/c22d087a-c284-4574-a89c-759cf7ddc367"

acm_validation_records = {
  "mo-doo.com" = {
    name  = "_a17d3ab8f64fb3762c6e5be89179c989.mo-doo.com"
    type  = "CNAME"
    value = "_c1bb21a8346962262cb913ec19711fb3.jkddzztszm.acm-validations.aws."
  }
  "api.mo-doo.com" = {
    name  = "_ba22f3d080b6d337456a446cd65fbf5b.api.mo-doo.com"
    type  = "CNAME"
    value = "_a79bd65296d7036ce79c5ab2d45af0de.jkddzztszm.acm-validations.aws."
  }
  "dev.api.mo-doo.com" = {
    name  = "_8f7fba6ba88135475a8cb096e5d89d38.dev.api.mo-doo.com"
    type  = "CNAME"
    value = "_f97ca60734acf299e66faa0a2b1ed826.jkddzztszm.acm-validations.aws."
  }
  "www.api.mo-doo.com" = {
    name  = "_660637fd5981d7552089076792159e70.www.api.mo-doo.com"
    type  = "CNAME"
    value = "_f6ac16fd0bc67c4488bd6f78010b61a1.jkddzztszm.acm-validations.aws."
  }
  "www.dev.api.mo-doo.com" = {
    name  = "_0e847de8d41c9d3b287980680011a6da.www.dev.api.mo-doo.com"
    type  = "CNAME"
    value = "_3ed47ff30da1449362d40ad5cc5e9578.jkddzztszm.acm-validations.aws."
  }
  "www.mo-doo.com" = {
    name  = "_174dc1fa165573646af1d82f239ccea7.www.mo-doo.com"
    type  = "CNAME"
    value = "_4f3cb9e9229094a0bb0408423fd6727b.jkddzztszm.acm-validations.aws."
  }
}
