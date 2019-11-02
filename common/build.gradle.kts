import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply {
    plugin("kotlin")
}

dependencies {
    val slf4jVersion = extra.properties["slf4jVersion"] ?: "1.7.28"
    val jacksonVersion = extra.properties["jacksonVersion"] ?: "2.9.9"
    val kotlinxCoroutineVersion = extra.properties["kotlinxCoroutineVersion"] ?: "1.3.0"
    val exposedVersion = extra.properties["exposedVersion"] ?: "0.17.3"
    val koinVersion = extra.properties["koinVersion"] ?: "2.0.1"
    val lettuceVersion = extra.properties["lettuceVersion"] ?: "5.1.8.RELEASE"
    val hikaricpVersion = extra.properties["hikaricpVersion"] ?: "3.4.1"
    val ktorVersion = extra.properties["ktorVersion"] ?: "1.2.4"

    implementation(kotlin("stdlib-jdk8"))

    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:jul-to-slf4j:$slf4jVersion")

    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinxCoroutineVersion")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutineVersion")

    compileOnly("org.jetbrains.exposed:exposed:$exposedVersion")

    compileOnly("org.koin:koin-core:$koinVersion")
    compileOnly("org.koin:koin-core-ext:$koinVersion")

    compileOnly("io.ktor:ktor-server-core:$ktorVersion")
    compileOnly("io.ktor:ktor-server-host-common:$ktorVersion")
    compileOnly("io.ktor:ktor-server-netty:$ktorVersion")

    compileOnly("io.ktor:ktor-client-apache:$ktorVersion")
    compileOnly("io.ktor:ktor-client-jackson:$ktorVersion")

    compileOnly("io.lettuce:lettuce-core:$lettuceVersion")

    compileOnly("com.zaxxer:HikariCP:$hikaricpVersion")
}

tasks.withType<KotlinCompile> {
    val jvmTarget = project.properties["jvmTarget"]?.toString() ?: "1.8"
    
    kotlinOptions.jvmTarget = jvmTarget
}