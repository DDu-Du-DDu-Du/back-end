plugins {
    id("modoo.java-conventions")
    id("modoo.spring-conventions")
}

dependencies {
    implementation(project(":bootstrap:bootstrap-common"))
    implementation(project(":application:application-common"))
    implementation(project(":common"))
    implementation(project(":application:notification-application"))
}