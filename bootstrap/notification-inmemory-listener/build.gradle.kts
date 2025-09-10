plugins {
    id("ddudu.java-conventions")
    id("ddudu.spring-conventions")
}

dependencies {
    implementation(project(":bootstrap:bootstrap-common"))
    implementation(project(":application:application-common"))
    implementation(project(":common"))
    implementation(project(":application:notification-application"))
}