dependencies {
    val kotlinxCoroutineVersion: String by project
    val ktorVersion: String by project

    implementation(project(":standing-water-domain"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutineVersion")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
}