description = "SRSoftware Tools : JDBC utils"

dependencies {
    implementation(project(":de.srsoftware.tools.util"))

    testImplementation("org.xerial:sqlite-jdbc:3.47.1.0")

    testImplementation("com.mysql:mysql-connector-j:9.1.0")
    testImplementation("ch.vorburger.mariaDB4j:mariaDB4j:3.1.0")

    testImplementation("org.slf4j:slf4j-simple:2.0.16")
}

plugins {
    id("eu.kakde.gradle.sonatype-maven-central-publisher") version "1.0.6"
}

object Meta {
    val COMPONENT_TYPE = "java" // "java" or "versionCatalog"
    val GROUP = "de.srsoftware"
    val ARTIFACT_ID = "tools.jdbc"
    val VERSION = "1.3.2"
    val PUBLISHING_TYPE = "AUTOMATIC" // USER_MANAGED or AUTOMATIC
    val SHA_ALGORITHMS = listOf("SHA-256", "SHA-512") // sha256 and sha512 are supported but not mandatory. Only sha1 is mandatory but it is supported by default.
    val DESC = "SRSoftware Tools : JDBC utils"
    val LICENSE = "MIT License"
    val LICENSE_URL = "http://www.opensource.org/licenses/mit-license.php"
    val GITHUB_REPO = "srsoftware-de/de.srsoftware.tools"
    val DEVELOPER_ID = "srichter"
    val DEVELOPER_NAME = "Stephan Richter"
    val DEVELOPER_ORGANIZATION = "SRSoftware"
    val DEVELOPER_ORGANIZATION_URL = "https://srsoftware.de"
}

val sonatypeUsername: String? by project // this is defined in ~/.gradle/gradle.properties
val sonatypePassword: String? by project // this is defined in ~/.gradle/gradle.properties

sonatypeCentralPublishExtension {
    // Set group ID, artifact ID, version, and other publication details
    groupId.set(Meta.GROUP)
    artifactId.set(Meta.ARTIFACT_ID)
    version.set(Meta.VERSION)
    componentType.set(Meta.COMPONENT_TYPE) // "java" or "versionCatalog"
    publishingType.set(Meta.PUBLISHING_TYPE) // USER_MANAGED or AUTOMATIC

    // Set username and password for Sonatype repository
    username.set(sonatypeUsername)
    password.set(sonatypePassword)

    // Configure POM metadata
    pom {
        name.set(Meta.ARTIFACT_ID)
        description.set(Meta.DESC)
        url.set("https://github.com/${Meta.GITHUB_REPO}")
        licenses {
            license {
                name.set(Meta.LICENSE)
                url.set(Meta.LICENSE_URL)
            }
        }
        developers {
            developer {
                id.set(Meta.DEVELOPER_ID)
                name.set(Meta.DEVELOPER_NAME)
                organization.set(Meta.DEVELOPER_ORGANIZATION)
                organizationUrl.set(Meta.DEVELOPER_ORGANIZATION_URL)
            }
        }
        scm {
            url.set("https://github.com/${Meta.GITHUB_REPO}")
            connection.set("scm:git:https://github.com/${Meta.GITHUB_REPO}")
            developerConnection.set("scm:git:https://github.com/${Meta.GITHUB_REPO}")
        }
        issueManagement {
            system.set("GitHub")
            url.set("https://github.com/${Meta.GITHUB_REPO}/issues")
        }
    }
}
