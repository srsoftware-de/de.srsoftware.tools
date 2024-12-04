description = "SRSoftware Tools : Optionals"

publishing {
    publications {
        create<MavenPublication>("maven"){
            groupId = "de.srsoftware"
            artifactId = "tools.optionals"
            version = "1.0.0"
            from(components["java"])
        }
    }
    repositories{
        mavenLocal()
    }
}