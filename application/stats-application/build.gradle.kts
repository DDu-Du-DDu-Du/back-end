plugins {
    id("ddudu.java-conventions")
    id("ddudu.spring-conventions")
    id("ddudu.jacoco-conventions")
}

dependencies {
    implementation(project(":application:application-common"))
    implementation(project(":common"))
    implementation(project(":domain:planning-domain"))
    implementation(project(":domain:user-domain"))
    implementation(project(":domain:stats-domain"))

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework:spring-tx")

    // application test fixtures
    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":domain:user-domain")))
    testImplementation(testFixtures(project(":domain:stats-domain")))
    testImplementation(testFixtures(project(":domain:planning-domain")))

    // For integration test, instead of mocking
    testImplementation(project(":infra:user-infra-mysql"))
    testImplementation(project(":infra:planning-infra-mysql"))
}

val copyTestSecret by tasks.registering(Copy::class) {
    from("${rootProject.projectDir}/secrets/test")
    include("application*.yaml")
    into(layout.buildDirectory.dir("resources/test"))
}

tasks.named("processResources") {
    dependsOn(copyTestSecret)
}
