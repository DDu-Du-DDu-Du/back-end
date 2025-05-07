plugins {
    id("ddudu.java-conventions")
    id("ddudu.jacoco-conventions")
    id("java-test-fixtures")
}

dependencies {
    implementation(project(":common"))

    implementation("net.datafaker:datafaker:2.0.2")
}
