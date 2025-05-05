plugins {
    id("ddudu.java-conventions")
    id("ddudu.jpa-conventions")
}

dependencies {
    implementation(project(":application:application-common"))
    implementation(project(":application:planning-application"))
    implementation(project(":domain:planning-domain"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // TODO: stats infra 구성 후 제거
    implementation(project(":application:stats-application"))
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
