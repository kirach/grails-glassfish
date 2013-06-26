grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		build 'org.slf4j:slf4j-api:1.6.6'
		build 'org.slf4j:slf4j-log4j12:1.6.6'
		build 'org.glassfish.main.extras:glassfish-embedded-all:3.1.2'
	}

	plugins {
		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}
	}
}
