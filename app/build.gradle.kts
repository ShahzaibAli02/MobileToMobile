plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.playsync.mirroring"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.playsync.mirroring"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        debug {

//            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            manifestPlaceholders+= mapOf("admob_app_id" to "ca-app-pub-3477055393019732~3736001735")
            buildConfigField(type="String",name="PRIVACY_POLICY", value = "\"https://sites.google.com/view/m2mprivacypolicy\"")

            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = false
            manifestPlaceholders+= mapOf("admob_app_id" to "ca-app-pub-3477055393019732~3736001735")
            buildConfigField(type="String",name="PRIVACY_POLICY", value = "" +
                    "\"https://sites.google.com/view/m2mprivacypolicy\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    /*

    keyeAlias=key0m2m
    keyPass=key0m2m
     */
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //sdp
    implementation ("com.intuit.sdp:sdp-android:1.1.0")

    //lottie
    implementation ("com.airbnb.android:lottie:3.4.2")

    //Screen sharing
    implementation ("com.twilio:video-android:7.3.0")

    //to send http requests
    implementation ("com.android.volley:volley:1.2.1")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:31.1.0"))
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-crashlytics")
    implementation ("com.google.firebase:firebase-config-ktx")
    implementation ("com.google.firebase:firebase-messaging-ktx")

    //billing
    implementation ("com.android.billingclient:billing:6.1.0")

    //admob
    implementation ("com.google.android.gms:play-services-ads:22.6.0")

    //Gson
    implementation ("com.google.code.gson:gson:2.10.1")

    //permission
    implementation ("com.guolindev.permissionx:permissionx:1.7.1")

    //color picker
    implementation ("com.jaredrummler:colorpicker:1.1.0")
}