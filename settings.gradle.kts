rootProject.name = "ddudu"

include(
    // bootstrap
    "bootstrap:bootstrap-common",
    "bootstrap:user-api",
    "bootstrap:stats-api",
    "bootstrap:planning-api",

    // application
    "application:application-common",
    "application:user-application",
    "application:stats-application",
    "application:planning-application",

    // domain
    "domain:domain-common",
    "domain:user-domain",
    "domain:stats-domain",
    "domain:planning-domain",

    // infra
    "infra:infra-mysql-common",
    "infra:user-infra-mysql",
    "infra:stats-infra-mysql",
    "infra:planning-infra-mysql",
    "infra:user-infra-external-api"
)
