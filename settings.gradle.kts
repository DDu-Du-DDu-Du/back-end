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
    "bootstrap:notification-api",
    "bootstrap:notification-listener",
    "bootstrap:notification-scheduler",

    // application
    "application:application-common",
    "application:user-application",
    "application:stats-application",
    "application:planning-application",
    "application:notification-application",

    // domain
    "domain:user-domain",
    "domain:stats-domain",
    "domain:planning-domain",
    "domain:notification-domain",

    // infra
    "infra:infra-mysql-common",
    "infra:user-infra-mysql",
    "infra:stats-infra-mysql",
    "infra:planning-infra-mysql",
    "infra:user-infra-external-api",
    "infra:notification-infra-mysql",
    "infra:notification-infra-inmemory-scheduler",
    "infra:notification-infra-fcm",

    // common
    "common",

    // coverage report
    "code-coverage-report"
)
