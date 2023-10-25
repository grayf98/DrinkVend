plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.btcontroll"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.btcontroll"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
//        Square SDK
//        multiDexEnabled = true

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

//  Square SDK
//    dexOptions{
//        // Ensures incremental builds remain fast
//        preDexLibraries = true
//        // Required to build with Reader SDK
//        jumboMode = true
//        // Required to build with Reader SDK
//        keepRuntimeAnnotatedClasses = false
//    }
}

dependencies {
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.mik3y:usb-serial-for-android:3.6.0")
    implementation ("pub.devrel:easypermissions:3.0.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("org.json:json:20231013")

    // Square SDK
//    val readerSdkVersion = "1.7.4"
//    TODO APPLICATION ID
//    implementation("com.squareup.sdk.reader:reader-sdk-$SQUARE_READER_SDK_APPLICATION_ID:$readerSdkVersion")
//    runtimeOnly("com.squareup.sdk.reader:reader-sdk-internals:$readerSdkVersion")

//    TODO UNCOMMENT IF SDK DOESNT WORK POSSIBLE FIX
//    Add this dependency if your minSdkVersion < 21
//    implementation("androidx.multidex:multidex:2.0.0")


}