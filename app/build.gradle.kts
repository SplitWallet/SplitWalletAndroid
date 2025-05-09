plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.jacoco)
}

android {
    namespace = "com.example.splitwallet"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.splitwallet"
        minSdk = 28
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    packagingOptions {
        exclude ("META-INF/DEPENDENCIES")
        exclude ("META-INF/LICENSE")
        exclude ("META-INF/LICENSE.txt")
        exclude ("META-INF/license.txt")
        exclude ("META-INF/NOTICE")
        exclude ("META-INF/NOTICE.txt")
        exclude ("META-INF/notice.txt")
        exclude ("META-INF/ASL2.0")
        exclude ("META-INF/*.kotlin_module")
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true

        }
    }
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest") // указываем, что тесты должны быть выполнены перед отчетом

    reports {
        xml.required.set(true) // включаем отчет в формате XML
        html.required.set(true) // включаем отчет в формате HTML
    }

    // Фильтры классов
    val fileFilter = listOf(
        "**/com/example/splitwallet/repository/UserRepository.class",
        "**/*\$Companion.class",
        "**/*\$*.class"
    )

    // Определяем директорию классов для отчета
    val debugTree = fileTree(mapOf("dir" to "$buildDir/intermediates/classes/debug")) {
        exclude(*fileFilter.toTypedArray()) // применяем фильтры
    }

// Устанавливаем директории классов и исходников
    classDirectories.setFrom(files(debugTree))
    sourceDirectories.setFrom(files("$projectDir/src/main/java"))

// Указываем данные о выполнении тестов для генерации отчета
    executionData.setFrom(
        fileTree(mapOf("dir" to buildDir)).include("jacoco/testDebugUnitTest.exec") // убедитесь, что этот файл существует
    )

}

dependencies {
    // Базовые AndroidX и UI
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.activity)
    implementation(libs.swiperefreshlayout)
    implementation(libs.recyclerview)
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.github.clans:fab:1.6.4")

    // Архитектура и жизненный цикл
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Сетевые библиотеки
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.simpleframework:simple-xml:2.7.1")

    // Google OAuth
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.api-client:google-api-client-android:2.2.0")
    implementation("com.google.http-client:google-http-client-gson:1.43.3")

    // Вспомогательные библиотеки
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation(libs.espresso.contrib)

    // Lombok (если нужен только на compile time)
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    // Тестирование (юнит-тесты)
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.17.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.robolectric:robolectric:4.12.2")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Android-инструментальные тесты
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("org.skyscreamer:jsonassert:1.5.1")
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.1")
    androidTestImplementation("org.mockito:mockito-android:5.11.0")

    // Для mock web server
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

// Для JUnit и ActivityScenarioRule
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.fragment:fragment-testing:1.6.2")


    //debugImplementation("androidx.fragment:fragment-testing:1.6.2")

}
