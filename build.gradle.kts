plugins {
    kotlin("multiplatform") version "1.4.30"
    id("maven-publish")
}

group = "org.paicoin.rpcclient"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(LEGACY) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.30")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.github.briandilley.jsonrpc4j:jsonrpc4j:1.5.2")
                implementation("com.fasterxml.jackson.core:jackson-databind:2.8.5")
                implementation("com.neovisionaries:nv-websocket-client:2.3")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.30")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
                implementation("io.reactivex.rxjava2:rxjava:2.1.7")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }

    val publicationsFromMainHost =
        listOf(jvm(), js()).map { it.name } + "kotlinMultiplatform"
    publishing {
        publications {
            matching { it.name in publicationsFromMainHost }.all {
                val targetPublication = this@all
                tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication == targetPublication }
                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
            }
        }

        repositories {
            maven {
                // change URLs to point to your repos, e.g. http://my.org/repo
                val repoUrl = project.property("artifactory_contextUrl")
                val releasesRepoUrl = uri("$repoUrl/releases")
                val snapshotsRepoUrl = uri("$repoUrl/snapshots")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                credentials {
                    username = project.property("artifactory_user") as String?
                    password = project.property("artifactory_password") as String?
                }
            }
        }
    }
}
