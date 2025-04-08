plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":leshan-lwm2m-core"))
    implementation(libs.slf4j.reload4j)
    implementation(libs.junit.jupiter.api)
}
