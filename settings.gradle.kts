pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "api-server"

include(
    "common",
    "base",
    "standing-water-api-application",
    "standing-water-api-presenter",
    "standing-water-domain",
    "standing-water-db",
    "standing-water-redis",
    "standing-water-s3"
)
