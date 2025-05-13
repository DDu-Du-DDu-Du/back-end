plugins {
    id("ddudu.java-conventions")
    id("ddudu.jpa-conventions")
}

dependencies {
    implementation(project(":domain:user-domain"))

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
}
