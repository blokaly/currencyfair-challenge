import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	idea
	val kotlinVersion = "1.3.72"
	id("org.springframework.boot") version "2.3.0.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	id("com.google.cloud.tools.jib") version "2.4.0"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.allopen") version kotlinVersion
	kotlin("plugin.noarg") version kotlinVersion
}


group = "github.blokaly"
version = "0.0.1"

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8


repositories {
	mavenLocal()
	mavenCentral()
	jcenter()
}

configurations {
	all {
		exclude(module = "spring-boot-starter-logging")
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
		exclude(group="org.slf4j", module="slf4j-log4j12")
		exclude(group="org.apache.logging.log4j")
	}
}

dependencies {

	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda")

	// SpringBoot
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-undertow")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Logging
	implementation("org.slf4j:slf4j-api:1.7.25")
	implementation("ch.qos.logback:logback-classic:1.2.3")
	implementation("ch.qos.logback:logback-core:1.2.3")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.register<Copy>("copy") {
	from(file("src/main/resources"))
	into(buildDir)
}

jib {
	from { image = "openjdk:8-jdk-alpine" }
	to {
		image = "github.blokaly/currencyfair-challenge-server"
		tags = setOf("$version")
		container {
			jvmFlags = listOf("-Dspring.profiles.active=\${PROFILE_NAME}", "-Xmx1024m")
			mainClass = "github.blokaly.cfcs.ApplicationMainKt"
		}
	}
	container.creationTime = "USE_CURRENT_TIMESTAMP"
}
