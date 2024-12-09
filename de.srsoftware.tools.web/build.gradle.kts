description = "SRSoftware Tools : Web"

publishing {
    publications {
        create<MavenPublication>("maven"){
            groupId = "de.srsoftware"
            artifactId = "tools.web"
            version = "1.0.0"
            from(components["java"])
        }
    }
    repositories{
        mavenLocal()
    }
}