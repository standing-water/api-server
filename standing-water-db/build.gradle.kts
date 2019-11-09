dependencies {
    val exposedVersion: String by project

    implementation(project(":standing-water-domain"))

    implementation("org.jetbrains.exposed:exposed:$exposedVersion")
}