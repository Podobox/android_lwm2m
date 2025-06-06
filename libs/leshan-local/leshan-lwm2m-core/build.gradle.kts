plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.slf4j.reload4j)
    implementation(libs.jackson.databind)
    implementation(libs.cbor)
}
