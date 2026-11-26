plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(project(":core"))
}

application {
    mainClass.set("analyzer.MainKt")
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}
