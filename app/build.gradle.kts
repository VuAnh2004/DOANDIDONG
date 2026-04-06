plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.doanqldiem"

    // Nâng lên 36 để fix lỗi AAR Metadata (yêu cầu từ androidx.activity 1.13.0+)
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.doanqldiem"
        minSdk = 24
        targetSdk = 36 // Chạy tối ưu trên Android 16 (S)
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
        // Sử dụng Java 17 nếu Android Studio của bạn phiên bản mới (Koala/Ladybug)
        // Nếu bị lỗi đỏ dòng này thì hạ xuống VERSION_11
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }



    // Build Tools bản 36 để tương thích hoàn toàn
    buildToolsVersion = "36.0.0-rc1"
}

dependencies {
    // Các thư viện lõi từ Catalog (libs.versions.toml)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.activity)

    // Thư viện bổ sung - Giữ nguyên như bạn cần
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // Thư viện hỗ trợ mở trình duyệt nhanh (Custom Tabs)
    implementation("androidx.browser:browser:1.8.0")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}