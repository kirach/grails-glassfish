class GlassfishGrailsPlugin {
	def version = "0.1.2"
	def grailsVersion = "2.0 > *"
	def pluginExcludes = [
		"grails-app/controllers/**",
		"grails-app/views/**",
		"web-app/**",
	]

	// don't package in war
	def scopes = [excludes: 'war']

	def title = "Glassfish Plugin"
	def author = "kiRach"
	def authorEmail = "kirach@kirach.net"
	def description = 'Plugin for developing Grails applications under Glassfish app-server.'

	def documentation = "https://github.com/kiRach/grails-glassfish"

	def license = "APACHE"
	def issueManagement = [system: "GITHUB", url: "https://github.com/kiRach/grails-glassfish/issues"]
	def scm = [url: "https://github.com/kiRach/grails-glassfish"]
}
