plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "ddudu"

include(
    // bootstrap
    "bootstrap:bootstrap-gateway",
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
    "domain:user-domain",
    "domain:stats-domain",
    "domain:planning-domain",

    // infra
    "infra:infra-mysql-common",
    "infra:user-infra-mysql",
    "infra:stats-infra-mysql",
    "infra:planning-infra-mysql",
    "infra:user-infra-external-api",

    // common
    "common",

    // coverage report
    "code-coverage-report"
)
