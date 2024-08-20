plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("com.google.devtools.ksp")
}
fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String = ProcessBuilder(split("\\s(?=(?:[^'\"`]*(['\"`])[^'\"`]*\\1)*[^'\"`]*$)".toRegex()))
    .directory(workingDir)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start()
    .apply { waitFor(timeoutAmount, timeoutUnit) }
    .run {
        val error = errorStream.bufferedReader().readText().trim()
        inputStream.bufferedReader().readText().trim()
    }
android {
    namespace = "co.ec.cnsyn.codecatcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "co.ec.cnsyn.codecatcher"
        minSdk = 24
        targetSdk = 34
        val commitCount = ("git rev-list --count HEAD".runCommand(workingDir = rootDir).toInt())
        versionCode = commitCount
        versionName = "1.0.$commitCount"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.room.ktx)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.material.icons.extended)

    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.ui.text.google.fonts) // Update version as needed
    implementation(libs.accompanist.systemuicontroller)




}