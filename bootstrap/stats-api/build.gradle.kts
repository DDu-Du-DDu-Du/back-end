plugins {
    id("ddudu.java-conventions")
    id("ddudu.spring-conventions")
}

dependencies {
    implementation(project(":bootstrap:bootstrap-common"))
    implementation(project(":application:application-common"))
    implementation(project(":common"))
    implementation(project(":application:stats-application"))

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}