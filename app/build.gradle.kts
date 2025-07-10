plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.dataapk.keuangan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dataapk.keuangan"
        minSdk = 29
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.sqlite)

    // Pilih SALAH SATU dari opsi MPAndroidChart berikut:
    // Opsi 1: Jika menggunakan versi dari libs.versions.toml
    implementation(libs.mpandroidchart)

    // ATAU Opsi 2: Jika ingin manual (pastikan sudah ada jitpack di repositories)
    // implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}