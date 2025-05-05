plugins {
    id("ddudu.java-conventions")
    id("ddudu.spring-conventions")
    id("ddudu.jacoco-conventions")
}

dependencies {
    implementation(project(":application:application-common"))
    implementation(project(":domain:user-domain"))

    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework:spring-tx")
}
