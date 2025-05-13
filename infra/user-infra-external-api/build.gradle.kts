plugins {
    id("ddudu.java-conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

dependencies {
    implementation(project(":common"))
    implementation(project(":application:application-common"))
    implementation(project(":application:user-application"))
    implementation(project(":domain:user-domain"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.retry:spring-retry")
}