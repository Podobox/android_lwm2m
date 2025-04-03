import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.lmw2w_connect"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lmw2w_connect"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    // Exclure les fichiers en conflit lors de l'assemblage de l'APK
    packaging {
        resources {
            excludes += "META-INF/legal/LICENSE"
            excludes += "META-INF/legal/NOTICE.md"
            excludes += "META-INF/legal/3rd-party/cc0-legalcode.html"
            excludes += "META-INF/legal/3rd-party/BSD-3-Clause-LICENSE.txt"
            excludes += "META-INF/legal/3rd-party/APACHE-LICENSE-2.0.txt"
            excludes += "META-INF/legal/3rd-party/MIT-license.html"
            excludes += "META-INF/legal/3rd-party/CDDL+GPL-1.1.txt"
        }
    }
}

dependencies {

    implementation(libs.californium.core)
    implementation(libs.leshan.tl.jc.client.coap) {
        exclude(group = "com.github.peteroupc", module = "datautilities")
    }
    implementation("org.eclipse.leshan:leshan-lwm2m-client:2.0.0-M17") {
        exclude(group = "com.github.peteroupc", module = "datautilities")
    }

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}