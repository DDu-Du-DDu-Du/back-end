plugins {
    id("modoo.java-conventions")
    id("modoo.spring-conventions")
}

dependencies {
    implementation(project(":application:application-common"))
    implementation(project(":common"))
}
