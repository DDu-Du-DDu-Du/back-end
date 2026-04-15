plugins {
    id("modoo.java-conventions")
    id("modoo.jpa-conventions")
}

dependencies {
    implementation(project(":domain:user-domain"))

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
}
