dependencies {
    val koinVersion: String by project
    val slf4jVersion: String by project
    val logbackVersion: String by project
    val logbackJsonVersion: String by project
    val hikaricpVersion: String by project
    val mysqlJdbcVersion: String by project
    val lettuceVersion: String by project

    implementation(project(":standing-water-api-presenter"))
    implementation(project(":standing-water-domain"))
    implementation(project(":standing-water-api-db"))
    implementation(project(":standing-water-api-redis"))

    implementation("org.koin:koin-core:$koinVersion")
    implementation("org.koin:koin-core-ext:$koinVersion")

    implementation("com.zaxxer:HikariCP:$hikaricpVersion")
    implementation("mysql:mysql-connector-java:$mysqlJdbcVersion")

    implementation("io.lettuce:lettuce-core:$lettuceVersion")

    implementation("org.slf4j:jul-to-slf4j:$slf4jVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")


    testImplementation("org.koin:koin-test:$koinVersion")
}