plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.hilt)
  id("com.google.gms.google-services")
  kotlin("kapt")
}

android {
  namespace = "com.openclassrooms.hexagonal.games"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.openclassrooms.hexagonal.games"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
  }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.11"
  }
}

dependencies {
  // Kotlin
  implementation(platform(libs.kotlin.bom))

  // Dependency Injection
  implementation(libs.hilt)
  kapt(libs.hilt.compiler)
  implementation(libs.hilt.navigation.compose)
  testImplementation(libs.hilt.android.testing)
  androidTestImplementation(libs.hilt.android.testing)
  kaptAndroidTest(libs.hilt.compiler)

  // Firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.analytics)
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.firestore.ktx)
  implementation(libs.firebase.messaging)
  implementation(libs.firebase.ui.auth.v802)
  implementation(libs.firebase.ui.storage)
  implementation(libs.firebase.ui.firestore)

  // Compose
  implementation(platform(libs.compose.bom))
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.material)
  implementation(libs.compose.material3)
  implementation(libs.lifecycle.runtime.compose)
  debugImplementation(libs.compose.ui.tooling)
  debugImplementation(libs.compose.ui.test.manifest)
  implementation(libs.activity.compose)
  implementation(libs.navigation.compose)

  // Image Loading and Permissions
  implementation(libs.coil.compose)
  implementation(libs.accompanist.permissions)

  // Coroutines
  implementation(libs.kotlinx.coroutines.android)
  testImplementation(libs.kotlinx.coroutines.test)

  // Unit Tests
  testImplementation(libs.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.slf4j.simple)

  // Android Tests
  androidTestImplementation(libs.ext.junit)
  androidTestImplementation(libs.espresso.core)
}

kapt {
  arguments {
    arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
  }
}