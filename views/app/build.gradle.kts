plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.apollographql.apollo3") version "4.0.0-beta.5"
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "lydia.yuan.viewslisteventtracking"
    compileSdk = 34

    defaultConfig {
        applicationId = "lydia.yuan.viewslisteventtracking"
        minSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.logging.interceptor)
    implementation(libs.picasso)
    implementation(libs.androidx.fragment.ktx.v170beta01)
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v280alpha03)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.apollo.runtime.v400beta5)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

apollo {
    service("pokemon") {
        packageName.set("com.pokemon")

        // // This will create a downloadPokemonApolloSchemaFromIntrospection task
        // to download the schema from the server, just run: ./gradlew downloadPokemonApolloSchemaFromIntrospection
        introspection {
            endpointUrl.set("https://graphql-pokeapi.vercel.app/api/graphql")
            schemaFile.set(file("src/main/graphql/com/pokemon/schema.graphqls"))
        }
    }
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}