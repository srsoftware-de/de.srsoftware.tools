description = "SRSoftware Utils"

dependencies{
    implementation("org.json:json:latest.release")
}

publishing {
    publications {
        create<MavenPublication>("maven"){
            groupId = "de.srsoftware"
            artifactId = "tools.util"
            version = "1.0.0"
            from(components["java"])
        }
    }
    repositories{
        mavenLocal()
        mavenCentral()
    }
}