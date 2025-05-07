plugins {
    id("ddudu.java-conventions")
    id("ddudu.jpa-conventions")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":infra:infra-mysql-common"))
    implementation(project(":application:application-common"))
    implementation(project(":application:stats-application"))
    implementation(project(":domain:stats-domain"))
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
