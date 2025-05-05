plugins {
    id("ddudu.java-conventions")
    id("ddudu.jpa-conventions")
}

dependencies {
    implementation(project(":application:application-common"))
    implementation(project(":application:user-application"))
    implementation(project(":domain:user-domain"))
}

val querydslSrcDir = "src/main/generated"

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(file(querydslSrcDir))
}

sourceSets {
    main {
        java.srcDir(querydslSrcDir)
    }
}

tasks.clean {
    delete(file(querydslSrcDir))
}