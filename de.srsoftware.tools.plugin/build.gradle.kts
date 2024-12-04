description = "SRSoftware Tools : Plugins"

publishing {
    publications {
        create<MavenPublication>("maven"){
            groupId = "de.srsoftware"
            artifactId = "tools.plugin"
            version = "1.0.0"
            from(components["java"])
        }
    }
    repositories{
        mavenLocal()
    }
}