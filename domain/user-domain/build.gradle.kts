plugins {
    id("ddudu.java-conventions")
    id("ddudu.jacoco-conventions")
    id("ddudu.spring-conventions")
    id("java-test-fixtures")
}

dependencies {
    implementation(project(":domain:domain-common"))

    implementation("net.datafaker:datafaker:2.0.2")

    // TODO: 리팩토링 이후 의존성 제거
    implementation("org.springframework.boot:spring-boot-starter-security")
}
