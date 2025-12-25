plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.recipeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.recipeapp"
        minSdk = 24
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
    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE.txt",
                "META-INF/DEPENDENCIES",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE.txt",
                "META-INF/LICENSE-notice.md"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.glide)
    implementation(libs.monitor)
    androidTestImplementation(libs.junit.jupiter)
    annotationProcessor(libs.compiler)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation (libs.runner)
    androidTestImplementation(libs.espresso.contrib)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(libs.espresso.accessibility)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.idling.concurrent)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(libs.espresso.contrib)

    implementation(libs.androidx.appcompat.v161)
    implementation(libs.material.v1110)
    implementation(libs.androidx.constraintlayout.v214)

    implementation(libs.okhttp)
    implementation(libs.okhttp.v4120)
    implementation(libs.logging.interceptor)

    implementation(libs.retrofit.v2110)

    implementation(libs.gson)
    implementation(libs.picasso)

    implementation(libs.ktor.client.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.glide.v4151)
    annotationProcessor(libs.compiler.v4151)

    implementation(libs.timber)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)

    implementation(libs.gson)

    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.v592)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v370)
    androidTestImplementation(libs.androidx.espresso.contrib.v351)
    androidTestImplementation(libs.androidx.espresso.intents.v370)
    androidTestImplementation(libs.androidx.runner.v152)
    androidTestImplementation(libs.androidx.rules.v150)

    implementation(libs.androidx.monitor.v161)
    androidTestImplementation(libs.androidx.uiautomator)
    implementation(libs.bcrypt)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.commons.io)
}
apply(plugin = "com.google.gms.google-services")