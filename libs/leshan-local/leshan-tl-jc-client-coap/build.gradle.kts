plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":leshan-lwm2m-client"))
    implementation(project(":leshan-tl-jc-shared"))
    implementation(project(":leshan-lwm2m-core"))
    implementation(libs.coap.mbedtls)
}
