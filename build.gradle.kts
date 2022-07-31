plugins {
    java
    application
}

group = "hk.ust.comp3021"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

application {
    mainClass.set("hk.ust.comp3021.Sokoban")
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs = listOf("--enable-preview")
        options.encoding = "UTF-8"
    }
    withType<Javadoc> {
        (options as? CoreJavadocOptions)?.apply {
            addStringOption("source", java.toolchain.languageVersion.get().toString())
            addBooleanOption("-enable-preview", true)
        }
    }
    withType<JavaExec> {
        jvmArgs("--enable-preview")
    }
    withType<Jar> {
        manifest {
            attributes.apply {
                this["Main-Class"] = application.mainClass.get()
            }
        }
    }
    withType<JacocoReport> {
        dependsOn(test)

        reports {
            xml.required.set(false)
            csv.required.set(false)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }
    withType<Test> {
        group = "verification"

        // Use JUnit Platform for unit tests.
        useJUnitPlatform()

        systemProperties(
            "junit.jupiter.execution.timeout.testable.method.default" to "2000 ms"
        )

        jvmArgs("--enable-preview")
    }
}
