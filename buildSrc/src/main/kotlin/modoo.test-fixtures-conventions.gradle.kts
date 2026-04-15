plugins {
    id("java-library")
    id("java-test-fixtures")
}

dependencies {
    testFixturesCompileOnly("org.projectlombok:lombok:1.18.38")
    testFixturesAnnotationProcessor("org.projectlombok:lombok:1.18.38")
    testFixturesImplementation("net.datafaker:datafaker:2.0.2")
}