plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

version = "1.0"

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Module to provide shared code to the Meditation App"
        homepage = "Link to the Shared Module homepage"

        name = "shared"
        
        ios.deploymentTarget = "14.0"
        framework {
            baseName = "shared"
        }
    }
    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 31
    }
}

tasks {

    register("copyPodspec") {
        description = "Copy Podspec file to root folder so we can perform online distribution"
        doFirst {
            copy {
                from("$rootDir/shared/shared.podspec")
                into("$rootDir")
            }
        }
    }
}

val podspec = tasks["podspec"] as org.jetbrains.kotlin.gradle.tasks.PodspecTask
podspec.doLast {
    val podspec = file("${project.name.replace("-", "_")}.podspec")
    val newPodspecContent = podspec.readLines().map {
        when {
            it.contains("spec.source") -> "spec.source = { git: 'https://github.com/eltonjhony/MeditationSharedModule' }"
            it.contains("spec.vendored_frameworks") -> "spec.vendored_frameworks = 'shared/build/cocoapods/framework/shared.framework'"
            else -> it
        }
    }
    podspec.writeText(newPodspecContent.joinToString(separator = "\n"))
}