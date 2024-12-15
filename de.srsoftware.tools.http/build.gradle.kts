description = "SRSoftware Tools : HTTP utils"

dependencies{
    implementation("org.json:json:20240303")
    implementation(project(":de.srsoftware.tools.optionals"))
    implementation(project(":de.srsoftware.tools.util"))
}

publishing {
    publications {
        create<MavenPublication>("maven"){
            groupId = "de.srsoftware"
            artifactId = "tools.http"
            version = "1.0.0"
            from(components["java"])
        }
    }
    repositories{
        mavenLocal()
    }
}