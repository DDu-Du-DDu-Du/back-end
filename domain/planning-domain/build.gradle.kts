plugins {
    id("modoo.java-conventions")
    id("modoo.jacoco-conventions")
    id("modoo.test-fixtures-conventions")
}

dependencies {
    implementation(project(":common"))

    // domain test
    testImplementation("net.datafaker:datafaker:2.0.2")
    testImplementation(testFixtures(project(":common")))
    testFixturesImplementation(testFixtures(project(":common")))

    // TODO: privacy type 리팩터링으로 순환참조 가능성 해결
    implementation(project(":domain:user-domain"))
}
