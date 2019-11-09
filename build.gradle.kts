import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    application
    kotlin("jvm") version "1.3.50"
}

group = "kr.jadekim.standingwater"
version = "v1"

application {
    mainClassName = "kr.jadekim.standingwater.server.api.MainKt"
    applicationDefaultJvmArgs = listOf(
        "-server",
        "-Djava.security.egd=file:/dev/./urandom"
    )
}

allprojects {
    apply {
        plugin("kotlin")
    }

    repositories {
        mavenCentral()
        jcenter()
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
}

configure(subprojects.filter { it.name != "common" }) {
    val jvmTarget: String by project

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = jvmTarget
    }
}

dependencies {
    implementation(project(":standing-water-api-application"))
}

configure<IdeaModel> {
    project {
        languageLevel = IdeaLanguageLevel(JavaVersion.VERSION_11)
    }
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}