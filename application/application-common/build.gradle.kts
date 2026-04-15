plugins {
    id("modoo.java-conventions")
    id("modoo.spring-conventions")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":domain:user-domain"))
    implementation(project(":domain:planning-domain"))
    implementation(project(":domain:stats-domain"))
    implementation(project(":domain:notification-domain"))

    implementation("org.springframework.boot:spring-boot-starter-aop")
}
