plugins {
    id("ddudu.java-conventions")
    id("ddudu.spring-conventions")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":domain:user-domain"))
    implementation(project(":domain:planning-domain"))
    implementation(project(":domain:stats-domain"))
}
