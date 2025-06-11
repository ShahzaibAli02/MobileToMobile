import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
// Load local.properties
val localProperties2 : Properties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists())
    {
        load(file.inputStream())
    }
}
android {


        namespace = "com.playsync.mirroring"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.playsync.mirroring"
        minSdk = 24
        targetSdk = 34
        versionCode = 5
        versionName = "1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "TWILIO_ACCOUNT_SID", "\"${localProperties2.getProperty("TWILIO_ACCOUNT_SID")}\"")
        buildConfigField("String", "TWILIO_API_KEY", "\"${localProperties2.getProperty("TWILIO_API_KEY")}\"")
        buildConfigField("String", "TWILIO_API_SECRET", "\"${localProperties2.getProperty("TWILIO_API_SECRET")}\"")
    }

    buildTypes {

        debug {

//            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            buildConfigField(type="String",name="PRIVACY_POLICY", value = "\"https://sites.google.com/view/m2mprivacypolicy\"")

            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = false
            buildConfigField(type="String",name="PRIVACY_POLICY", value = "" +
                    "\"https://google.com\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
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
    implementation ("com.twilio:video-android:7.8.0")
    implementation("com.twilio.sdk:twilio:10.6.10")

    //to send http requests
    implementation ("com.android.volley:volley:1.2.1")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:31.1.0"))
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-crashlytics")
    implementation ("com.google.firebase:firebase-config-ktx")
    implementation ("com.google.firebase:firebase-messaging-ktx")



    //Gson
    implementation ("com.google.code.gson:gson:2.10.1")

    //permission
    implementation ("com.guolindev.permissionx:permissionx:1.7.1")

    //color picker
    implementation ("com.jaredrummler:colorpicker:1.1.0")
}