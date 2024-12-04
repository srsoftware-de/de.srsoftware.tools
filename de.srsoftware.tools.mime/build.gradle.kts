description = "SRSoftware Tools : Mime types"

publishing {
    publications {
        create<MavenPublication>("maven"){
            groupId = "de.srsoftware"
            artifactId = "tools.mime"
            version = "1.0.0"
            from(components["java"])
        }
    }
    repositories{
        mavenLocal()
    }
}