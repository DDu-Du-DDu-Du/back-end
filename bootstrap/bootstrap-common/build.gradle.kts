plugins {
    id("ddudu.java-conventions")
    id("ddudu.spring-conventions")
}

dependencies {
    implementation(project(":application:application-common"))
    implementation(project(":common"))
}
