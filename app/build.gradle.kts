plugins {
    id("com.android.application")
}

android {
    namespace = "com.kingdomunderground.repsettracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kingdomunderground.repsettracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 5
        versionName = "1.4"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
