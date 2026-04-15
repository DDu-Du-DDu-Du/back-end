plugins {
    id("modoo.java-conventions")
    id("modoo.spring-conventions")
}

dependencies {
    implementation(project(":bootstrap:bootstrap-common"))
    implementation(project(":application:application-common"))
    implementation(project(":common"))
    implementation(project(":application:planning-application"))

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}