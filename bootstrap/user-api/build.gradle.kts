plugins {
    id("ddudu.java-conventions")
    id("ddudu.spring-conventions")
}

dependencies {
    implementation(project(":bootstrap:bootstrap-common"))
    implementation(project(":application:application-common"))
    implementation(project(":application:user-application"))
    implementation(project(":common"))

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("net.datafaker:datafaker:2.0.2")
}