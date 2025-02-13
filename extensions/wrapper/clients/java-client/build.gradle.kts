
plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.openapi.generator6)
}

repositories {
    mavenCentral()
}

// By using a separate configuration we can skip having the Extension Jar in our runtime classpath
val openapiYaml = configurations.create("openapiGenerator")
val buildDir = layout.buildDirectory.get().asFile

dependencies {
    // We only need the openapi.yaml file from this dependency
    openapiYaml(project(":extensions:wrapper:wrapper-api")) {
        isTransitive = false
    }

    // Generated Client's Dependencies
    implementation(libs.swagger.annotations)
    implementation(libs.findbugs.jsr305)
    implementation(libs.okhttp.okhttp)
    implementation(libs.okhttp.loggingInterceptor)
    implementation(libs.gson)
    implementation(libs.gsonFire)
    implementation(libs.openapi.jacksonDatabindNullable)
    implementation(libs.apache.commonsLang)
    implementation(libs.jakarta.annotationApi)
    api(project(":utils:json-and-jsonld-utils"))

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// Extract the openapi file from the JAR
val openapiFile = "sovity-edc-api-wrapper.yaml"
val extractOpenapiYaml by tasks.registering(Copy::class) {
    dependsOn(openapiYaml)
    into("$buildDir")
    from(zipTree(openapiYaml.singleFile)) {
        include(openapiFile)
    }
}

tasks.getByName<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
    dependsOn(extractOpenapiYaml)
    generatorName.set("java")
    configOptions.set(
            mutableMapOf(
                    "invokerPackage" to "utopiaia.odc.client.gen",
                    "apiPackage" to "utopiaia.odc.client.gen.api",
                    "modelPackage" to "utopiaia.odc.client.gen.model",
                    "caseInsensitiveResponseHeaders" to "true",
                    "additionalModelTypeAnnotations" to listOf(
                        "@lombok.AllArgsConstructor",
                        "@lombok.Builder",
                        "@SuppressWarnings(\"all\")"
                    ).joinToString("\n"),
                    "annotationLibrary" to "swagger1",
                    "hideGenerationTimestamp" to "true",
                    "useRuntimeException" to "true",
            )
    )

    inputSpec.set("${buildDir}/${openapiFile}")
    outputDir.set("${buildDir}/generated/client-project")
}

val postprocessGeneratedClient by tasks.registering(Copy::class) {
    dependsOn("openApiGenerate")
    from("${buildDir}/generated/client-project/src/main/java")

    // @lombok.Builder clashes with the following generated model file.
    // It is the base class for OAS3 polymorphism via allOf/anyOf, which we won't use anyway.
    exclude("**/AbstractOpenApiSchema.java")

    // The Jax-RS dependency suggested by the generated project was causing issues with quarkus.
    // It was again only required for the polymorphism, which we won't use anyway.
    filter { if (it == "import javax.ws.rs.core.GenericType;") "" else it }

    into("${buildDir}/generated/sources/openapi/java/main")
}
sourceSets["main"].java.srcDir("${buildDir}/generated/sources/openapi/java/main")

checkstyle {
    // Checkstyle loathes the generated files
    // TODO make checkstyle skip generated files only
    this.sourceSets = emptyList()
}

tasks.getByName<JavaCompile>("compileJava") {
    dependsOn("postprocessGeneratedClient")
    options.compilerArgs = listOf("-Xlint:none")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
    withJavadocJar()
}

tasks.getByName("sourcesJar") {
    dependsOn(postprocessGeneratedClient)
}

tasks.withType<Javadoc> {
    val fullOptions = this.options as StandardJavadocDocletOptions
    fullOptions.tags = listOf("http.response.details:a:Http Response Details")
    fullOptions.addStringOption("Xdoclint:none", "-quiet")
}

group = libs.versions.sovityEdcGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            artifactId = "client"
            from(components["java"])
        }
    }
}
