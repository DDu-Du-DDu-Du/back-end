plugins {
    id("ddudu.java-conventions")
    id("ddudu.jacoco-conventions")
    id("java-test-fixtures")
}

dependencies {
    implementation(project(":common"))

    implementation("net.datafaker:datafaker:2.0.2")

    // TODO: privacy type 리팩터링으로 순환참조 가능성 해결
    implementation(project(":domain:user-domain"))
}
