import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "com.jsrana.plugins.quicknotes"
version = "4.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea("2026.1")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

intellijPlatform {
    pluginConfiguration {
        id = "Quick Notes"
        name = "Quick Notes"
        version = "4.0"
        ideaVersion {
            sinceBuild = "242"
            untilBuild = provider { null }
        }
        vendor {
            name = "Jitendra Rana"
            email = "jsrana@gmail.com"
            url = "https://github.com/jrana/quicknotes"
        }
    }
    pluginVerification {
        ides {
            select {
                types = listOf(IntelliJPlatformType.IntellijIdeaCommunity)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = "261"
                untilBuild = "261.*"
            }
        }
    }
}
