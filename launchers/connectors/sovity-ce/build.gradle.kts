plugins {
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

dependencies {
    api(project(":launchers:common:base"))
    api(project(":launchers:common:auth-daps"))
    api(project(":launchers:common:observability"))
}

application {
    mainClass.set("utopiaia.odc.Main")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}

group = libs.versions.sovityEdcGroup.get()
