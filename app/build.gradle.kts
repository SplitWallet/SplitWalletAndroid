plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.splitwallet"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.splitwallet"
        minSdk = 28
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
    buildFeatures {
        viewBinding = true
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Retrofit 2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Для работы с JSON (Gson)

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor ("org.projectlombok:lombok:1.18.34")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Для логирования запросов

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-simplexml
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")

    implementation("org.simpleframework:simple-xml:2.7.1")

    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")

    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")

    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.android.material:material:1.6.0")
}