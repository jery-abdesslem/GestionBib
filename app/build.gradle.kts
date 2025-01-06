plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    //id("com.google.firebase.crashlytics") // Décommentez cette ligne si vous souhaitez utiliser Crashlytics
}

android {
    namespace = "com.example.gestionbib2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gestionbib2" // Assurez-vous que l'applicationId correspond au namespace
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat.v131)
    implementation(libs.constraintlayout.v210)
    implementation(libs.material.v140)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.v2003)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.viewpager2)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.v113)
    androidTestImplementation(libs.espresso.core.v340)
    implementation(platform(libs.firebase.bom))
    implementation(libs.play.services.location)
    //implementation(libs.firebase.crashlytics) // Décommentez cette ligne si vous souhaitez utiliser Crashlytics
}
