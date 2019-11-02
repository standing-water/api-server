dependencies {
    val slf4jVersion: String by project
    val jodaTimeVersion: String by project
    val kotlinxCoroutineVersion: String by project
    val jacksonVersion: String by project

    api(project(":common"))

    api(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutineVersion")

    api("org.slf4j:slf4j-api:$slf4jVersion")

    api("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    api("joda-time:joda-time:$jodaTimeVersion")
}