plugins {
    id("ddudu.java-conventions")
    id("ddudu.spring-conventions")
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
    implementation(project(":bootstrap:user-api"))
    implementation(project(":bootstrap:planning-api"))
    implementation(project(":bootstrap:stats-api"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
}

val copyMainSecret by tasks.registering(Copy::class) {
    from("${rootProject.projectDir}/secrets/main")
    include("application*.yaml")
    into(layout.buildDirectory.dir("resources/main"))
}

tasks.named("processResources") {
    dependsOn(copyMainSecret)
}

val copyTestSecret by tasks.registering(Copy::class) {
    from("${rootProject.projectDir}/secrets/test")
    include("application*.yaml")
    into(layout.buildDirectory.dir("resources/test"))
}

tasks.named("processResources") {
    dependsOn(copyTestSecret)
}
