dependencies {
    val awsSdkVersion: String by project

    implementation(project(":standing-water-domain"))

    implementation(platform("software.amazon.awssdk:bom:$awsSdkVersion"))
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:sts")
}