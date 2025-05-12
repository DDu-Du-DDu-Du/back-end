plugins {
    id("java-library")
    id("jacoco-report-aggregation")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

val jacocoModules = rootProject.allprojects.filter {
    it.displayName.contains("-application") || it.displayName.contains("-domain")
}

tasks.bootJar {
    enabled = false
}

repositories {
    mavenCentral()
}

dependencies {
    jacocoModules.forEach { jacocoAggregation(it) }
}

tasks.check {
    dependsOn(tasks.named<JacocoReport>("testCodeCoverageReport"))
}
