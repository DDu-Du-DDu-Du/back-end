plugins {
    id("ddudu.java-conventions")
    id("ddudu.jacoco-conventions")
    id("ddudu.test-fixtures-conventions")
}

dependencies {
    implementation(project(":common"))

    // domain test
    testImplementation(testFixtures(project(":common")))
    testFixturesImplementation(testFixtures(project(":common")))
    testImplementation("net.datafaker:datafaker:2.0.2")
}
