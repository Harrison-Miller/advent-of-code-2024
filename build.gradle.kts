val year = project.findProperty("year") as? String ?: "2024" // Default to 2024


plugins {
    kotlin("jvm") version "2.1.0"
    application
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.11.1"
    }
}

(0..25).forEach { day ->
    tasks.register<JavaExec>("day$day") {
        group = "execution"
        description = "Runs Advent of Code $year Day $day"
        mainClass.set("year$year.day$day.MainKt")
        classpath = sourceSets["main"].runtimeClasspath
        standardOutput = System.out
        errorOutput = System.err
    }
}


