plugins {
    id("ddudu.java-conventions")
    id("ddudu.jpa-conventions")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":infra:infra-mysql-common"))
    implementation(project(":application:application-common"))
    implementation(project(":application:planning-application"))
    implementation(project(":domain:planning-domain"))

    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // TODO: stats infra 구성 후 제거
    implementation(project(":application:stats-application"))
    implementation(project(":domain:stats-domain"))
}

val querydslSrcDir = "src/main/generated"

tasks.compileJava {
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
