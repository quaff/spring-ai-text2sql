plugins {
	java
	checkstyle
	`java-library`
	`maven-publish`
	id("io.spring.javaformat").version("0.0.45")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	val repoUrlPrefix: String? by rootProject
	if (repoUrlPrefix != null) {
		maven {
			url = uri("${repoUrlPrefix}/maven-public/")
			isAllowInsecureProtocol = true
		}
	} else {
		mavenCentral()
		maven {
			url = uri("https://repo.spring.io/milestone")
		}
	}
}

dependencies {
	implementation(platform("""org.springframework.boot:spring-boot-dependencies:${property("spring-boot.version")}"""))
	implementation(platform("""org.springframework.ai:spring-ai-bom:${property("spring-ai.version")}"""))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.ai:spring-ai-starter-model-openai")
	// implementation("org.springframework.ai:spring-ai-starter-model-deepseek")
	// implementation("org.springframework.ai:spring-ai-starter-model-ollama")
	implementation("""org.commonmark:commonmark:${property("commonmark.version")}""")
	implementation("""org.webjars:jquery:${property("jquery.version")}""")
	implementation("""org.webjars:bootstrap:${property("bootstrap.version")}""")
	implementation("""org.webjars:echarts:${property("echarts.version")}""")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("com.h2database:h2")

	checkstyle("io.spring.javaformat:spring-javaformat-checkstyle:0.0.45")
}

java {
	withSourcesJar()
}

tasks.jar {
	manifest {
		attributes(
			mapOf(
				"Implementation-Title" to name,
				"Implementation-Version" to version,
				"Automatic-Module-Name" to name.replace("-", ".")  // for Jigsaw
			)
		)
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register("checkstyle") {
	description = "Run Checkstyle analysis for all classes"
	sourceSets.map { "checkstyle" + it.name.replaceFirstChar(Char::titlecase) }.forEach(::dependsOn)
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			versionMapping {
				usage("java-api") {
					fromResolutionOf("runtimeClasspath")
				}
				usage("java-runtime") {
					fromResolutionResult()
				}
			}
			suppressAllPomMetadataWarnings()
		}
	}
	val repoUrlPrefix: String? by project
	if (repoUrlPrefix != null) {
		repositories {
			val version: String by project
			val repoUser: String? by project
			val repoPassword: String? by project
			maven {
				url = if (version.endsWith("-SNAPSHOT")) {
					uri("${repoUrlPrefix}/maven-snapshots/")
				} else {
					uri("${repoUrlPrefix}/maven-releases/")
				}
				isAllowInsecureProtocol = true
				credentials {
					username = repoUser
					password = repoPassword
				}
			}
		}
	}
}
