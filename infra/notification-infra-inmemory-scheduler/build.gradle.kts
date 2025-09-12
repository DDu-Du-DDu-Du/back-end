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
    implementation(project(":application:notification-application"))
    implementation(project(":domain:notification-domain"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-test")
}