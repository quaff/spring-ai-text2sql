plugins {
	id("org.springframework.boot").version("3.4.4")
	id("io.spring.dependency-management").version("latest.release")
	java
}

group = "com.example.ai"
version = "0.0.1-SNAPSHOT"

java {
	version = 21
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://repo.spring.io/milestone")
	}
	maven {
		url = uri("https://repo.spring.io/snapshot")
	}
}

dependencies {
	implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0-SNAPSHOT"))
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.ai:spring-ai-starter-model-deepseek")
	// implementation("org.springframework.ai:spring-ai-starter-model-ollama")
	implementation("com.github.jsqlparser:jsqlparser:5.1")
	runtimeOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:mysql")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
