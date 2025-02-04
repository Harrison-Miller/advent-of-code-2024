import java.net.HttpURLConnection
import java.net.URL
import java.io.File

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

val session = project.findProperty("aocSession") as? String
    ?: throw IllegalArgumentException("Please provide your Advent of Code session token in gradle.properties")

tasks.register("fetchInput") {
    group = "advent"
    description = "Fetches Advent of Code input and saves it as input.txt"

    val day = project.findProperty("day") as? String ?: throw IllegalArgumentException("Specify day with -Pday=X")

    doLast {
        val inputUrl = "https://adventofcode.com/$year/day/$day/input"
        val inputFile = File("${project.rootDir}/src/year$year/day$day/input.txt")

        // Ensure directory exists
        inputFile.parentFile.mkdirs()

        // Fetch input
        val connection = URL(inputUrl).openConnection() as HttpURLConnection
        connection.setRequestProperty("Cookie", "session=$session")

        if (connection.responseCode == 200) {
            inputFile.writeText(connection.inputStream.bufferedReader().readText().trim())
            println("✅ Input saved to ${inputFile.absolutePath}")
        } else {
            error("Failed to fetch input: HTTP ${connection.responseCode}")
        }
    }
}
