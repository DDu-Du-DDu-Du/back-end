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
    implementation(project(":bootstrap:bootstrap-common"))
    implementation(project(":common"))
    implementation(project(":application:application-common"))
    implementation(project(":application:notification-application"))

    implementation("org.springframework.boot:spring-boot-starter")
}