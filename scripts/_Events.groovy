// No programmable web.xml path yet, so put it in the right place automatically
eventGenerateWebXmlEnd = {
	System.setProperty("grails.server.factory", "net.kirach.grails.plugin.glassfish.GlassfishServerFactory")
}