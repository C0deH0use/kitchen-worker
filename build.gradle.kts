plugins {
	java
	idea
	jacoco
	checkstyle
	`jvm-test-suite`
	`jacoco-report-aggregation`
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
}

checkstyle {
	toolVersion = "10.12.1"
	configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
	sourceSets = listOf(project.sourceSets.main.get())
}

group = "pl.codehouse.restaurant"
version = "0.0.1-SNAPSHOT"
val junitVersion = "5.10.2"
val junitPlatformVersion = "1.10.2"
val jacksonVersion = "2.18.2"
val cucumberVersion = "7.15.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

idea {
	module {
		testSources.from(file("src/integrationTest/java"), file("src/test/java"))
		testResources.from(file("src/integrationTest/resources"), file("src/test/resources"))
	}
}

dependencies {
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.kafka:spring-kafka")

	implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
	implementation("com.fasterxml.jackson.core:jackson-core:${jacksonVersion}")
	implementation("com.fasterxml.jackson.core:jackson-annotations:${jacksonVersion}")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:kafka")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.testcontainers:r2dbc")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

testing {
	suites {
		withType<JvmTestSuite> {
			useJUnitJupiter(junitVersion)
			dependencies {
				implementation("io.projectreactor:reactor-test")

				implementation("org.springframework.boot:spring-boot-starter-test") {
					exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
				}
				implementation("org.springframework.boot:spring-boot-starter-data-jpa")
				implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
				implementation("org.springframework.kafka:spring-kafka-test")

				implementation("org.junit.jupiter:junit-jupiter:$junitVersion")
				implementation("org.junit.platform:junit-platform-suite:$junitPlatformVersion")
				implementation("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")

				implementation("io.cucumber:cucumber-java:$cucumberVersion")
				implementation("io.cucumber:cucumber-junit:$cucumberVersion")
				implementation("io.cucumber:cucumber-spring:$cucumberVersion")
				implementation("io.cucumber:cucumber-junit-platform-engine:$cucumberVersion")

				runtimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
			}
		}

		val integrationTest by registering(JvmTestSuite::class) {
			testType.set(TestSuiteType.INTEGRATION_TEST)
			sources {
				java {
					setSrcDirs(listOf("src/integrationTest/java"))
				}
				resources {
					setSrcDirs(listOf("src/integrationTest/resources", "src/test/resources"))
				}
			}
			dependencies {
				implementation(project())
				implementation(sourceSets.test.get().output)
				implementation(sourceSets.test.get().runtimeClasspath)
				implementation(project.dependencies.platform("org.springframework.boot:spring-boot-dependencies:3.2.3"))

				implementation("org.flywaydb:flyway-core")
				implementation("org.flywaydb:flyway-database-postgresql")

				implementation("org.springframework.boot:spring-boot-testcontainers")

				implementation("org.testcontainers:junit-jupiter")
				implementation("org.testcontainers:kafka")
				implementation("org.testcontainers:postgresql")
				implementation("org.testcontainers:r2dbc")
			}
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("cucumber.junit-platform.naming-strategy", "long")
	finalizedBy(tasks.jacocoTestReport)

	testLogging {
		events("passed", "skipped", "failed")
	}
}

tasks.named<Test>("integrationTest") {
	useJUnitPlatform()
	systemProperty("cucumber.junit-platform.naming-strategy", "long")
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	reports {
		xml.required = true
		csv.required = true
		html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
	}
}

tasks.withType<JacocoReport>().configureEach {
	dependsOn(project.tasks.withType<Test>())
	// execution data needs to be aggregated from all exec files in the project for multi jvm test suite testing
	tasks.withType<Test>().forEach(::executionData) // confusing
}

tasks.withType<JacocoCoverageVerification>().configureEach {
	dependsOn(project.tasks.withType<JacocoReport>())
	// execution data needs to be aggregated from all exec files in the project for multi jvm test suite testing
	executionData(project.tasks.withType<JacocoReport>().map { it.executionData })
}

tasks.withType<Test>().configureEach {
	maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
	setForkEvery(100)
	reports.html.required.set(true)
}
