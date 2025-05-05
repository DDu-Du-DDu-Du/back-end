plugins {
    id("ddudu.java-conventions")
    id("ddudu.jacoco-conventions")
}

dependencies {
    implementation(project(":domain:domain-common"))

    implementation("net.datafaker:datafaker:2.0.2")
}
