import {
  to = module.route53_common.aws_route53_zone.this
  id = "Z09209125CJ92OR19RX7"
}

import {
  to = module.route53_common.aws_route53_record.hosted_zone_ns
  id = "Z09209125CJ92OR19RX7_mo-doo.com_NS"
}

import {
  to = module.route53_common.aws_route53_record.hosted_zone_soa
  id = "Z09209125CJ92OR19RX7_mo-doo.com_SOA"
}

import {
  to = module.route53_common.aws_route53_record.acm_validation["mo-doo.com"]
  id = "Z09209125CJ92OR19RX7__a17d3ab8f64fb3762c6e5be89179c989.mo-doo.com_CNAME"
}

import {
  to = module.route53_common.aws_route53_record.acm_validation["api.mo-doo.com"]
  id = "Z09209125CJ92OR19RX7__ba22f3d080b6d337456a446cd65fbf5b.api.mo-doo.com_CNAME"
}

import {
  to = module.route53_common.aws_route53_record.acm_validation["dev.api.mo-doo.com"]
  id = "Z09209125CJ92OR19RX7__8f7fba6ba88135475a8cb096e5d89d38.dev.api.mo-doo.com_CNAME"
}

import {
  to = module.route53_common.aws_route53_record.acm_validation["www.api.mo-doo.com"]
  id = "Z09209125CJ92OR19RX7__660637fd5981d7552089076792159e70.www.api.mo-doo.com_CNAME"
}

import {
  to = module.route53_common.aws_route53_record.acm_validation["www.dev.api.mo-doo.com"]
  id = "Z09209125CJ92OR19RX7__0e847de8d41c9d3b287980680011a6da.www.dev.api.mo-doo.com_CNAME"
}

import {
  to = module.route53_common.aws_route53_record.acm_validation["www.mo-doo.com"]
  id = "Z09209125CJ92OR19RX7__174dc1fa165573646af1d82f239ccea7.www.mo-doo.com_CNAME"
}
