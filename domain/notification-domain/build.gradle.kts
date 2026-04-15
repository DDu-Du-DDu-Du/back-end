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

    // If notification domain needs user references later, add dependency as needed
}
