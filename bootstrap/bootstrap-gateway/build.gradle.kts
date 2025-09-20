buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-mysql:11.8.1")
    }
}

plugins {
    id("ddudu.java-conventions")
    id("ddudu.spring-conventions")
    id("org.flywaydb.flyway") version "11.8.1"
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = false
}

dependencies {
    implementation(project(":infra:infra-mysql-common"))
    implementation(project(":infra:planning-infra-mysql"))
    implementation(project(":infra:stats-infra-mysql"))
    implementation(project(":infra:user-infra-mysql"))
    implementation(project(":infra:user-infra-external-api"))
    implementation(project(":infra:notification-infra-mysql"))
    implementation(project(":infra:notification-infra-inmemory-scheduler"))
    implementation(project(":infra:notification-infra-fcm"))
    implementation(project(":bootstrap:bootstrap-common"))
    implementation(project(":bootstrap:user-api"))
    implementation(project(":bootstrap:planning-api"))
    implementation(project(":bootstrap:stats-api"))
    implementation(project(":bootstrap:notification-api"))
    implementation(project(":bootstrap:notification-listener"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
}

flyway {
    url = "jdbc:mysql://localhost:13307/ddudu"
    user = "root"
    password = "1234"
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}

val copyMainSecret by tasks.registering(Copy::class) {
    from("${rootProject.projectDir}/secrets/main")
    include(listOf("application*.yaml", "*.json"))
    into(layout.buildDirectory.dir("resources/main"))
}

val copyTestSecret by tasks.registering(Copy::class) {
    from("${rootProject.projectDir}/secrets/test")
    include(listOf("application*.yaml", "*.json"))
    into(layout.buildDirectory.dir("resources/test"))
}

tasks.named("processResources") {
    dependsOn(copyMainSecret)
    dependsOn(copyTestSecret)
}
