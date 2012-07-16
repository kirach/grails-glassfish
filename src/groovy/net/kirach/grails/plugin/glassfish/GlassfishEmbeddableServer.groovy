package net.kirach.grails.plugin.glassfish

import static grails.build.logging.GrailsConsole.instance as CONSOLE

import grails.util.BuildSettings
import grails.util.BuildSettingsHolder
import grails.web.container.EmbeddableServer
import org.apache.commons.io.FileUtils
import org.glassfish.embeddable.GlassFish
import org.glassfish.embeddable.GlassFishRuntime
import org.glassfish.embeddable.web.Context
import org.glassfish.embeddable.web.WebContainer
import org.glassfish.embeddable.web.config.WebContainerConfig

/**
 * Glassfish embeddable server for grails use.
 *
 * @author kiRach
 */
class GlassfishEmbeddableServer implements EmbeddableServer {

	final BuildSettings buildSettings

	GlassFish glassfish
	WebContainer embedded
	String webXml
	String basedir
	String contextPath
	ClassLoader classLoader

	Context context

	/**
	 * Constructor.
	 */
	GlassfishEmbeddableServer(String basedir, String webXml, String contextPath, ClassLoader classLoader) {
		buildSettings = BuildSettingsHolder.getSettings()
		glassfish = GlassFishRuntime.bootstrap().newGlassFish();

		//just remember this params for using later
		this.webXml = webXml
		this.basedir = basedir
		this.contextPath = contextPath
		this.classLoader = classLoader
	}

	/**
	 * Start embedded glassfish and deploy grails app to it.
	 *
	 * @param host
	 * @param httpPort
	 */
	private doStart(String host, int httpPort) {
		CONSOLE.updateStatus "Starting GlassFish server"

		//start server
		glassfish.start();

		//get web-container
		embedded = glassfish.getService(WebContainer.class);

		//set web-container' config
		WebContainerConfig config = new WebContainerConfig();
		//here we can configure our web container
		config.setPort(httpPort)
		config.setHostNames(host)
		embedded.setConfiguration(config);

		//enable comet support, if needed
		if (getConfigParam("comet")) {
			CONSOLE.updateStatus "Enabling GlassFish Comet support"
			def commandRunner = glassfish.getCommandRunner()
			def res = commandRunner.run("set", "server-config.network-config.protocols.protocol." + config.getListenerName() + ".http.comet-support-enabled=true")
		}

		//enable websocket support, if needed
		if (getConfigParam("websocket")) {
			CONSOLE.updateStatus "Enabling GlassFish Websocket support"
			def commandRunner = glassfish.getCommandRunner()
			def res = commandRunner.run("set", "server-config.network-config.protocols.protocol." + config.getListenerName() + ".http.websockets-support-enabled=true")
		}

		//copy grails' web.xml to our directory
		def tempWebXml = new File("${this.basedir}/WEB-INF/web.xml")
		FileUtils.copyFile(new File(this.webXml), tempWebXml)

		try {
			//let's create context - our web application from basedir war from step before and ClassLoader, provided by grails
			this.context = this.embedded.createContext(new File(this.basedir), this.contextPath, this.classLoader)
		} finally {
			//remove previously copied web.xml after deployment
			tempWebXml.delete()
		}
	}

	@Override
	void start() {
		start(null, null)
	}

	@Override
	void start(int port) {
		start(null, port)
	}

	@Override
	void start(String host, int port) {
		doStart(host ?: DEFAULT_HOST, port ?: DEFAULT_PORT)
	}

	@Override
	void startSecure() {
		//TODO
	}

	@Override
	void startSecure(int port) {
		//TODO
	}

	@Override
	void startSecure(String host, int httpPort, int httpsPort) {
		//TODO
	}

	@Override
	void stop() {
		CONSOLE.updateStatus "Stopping GlassFish server"
		if (glassfish != null) {
			glassfish.stop();
			glassfish.dispose();
			glassfish = null;
		}
	}

	@Override
	void restart() {
		CONSOLE.updateStatus "Restarting a GlassFish server"
		//here it is mentioned, that restart doesn't work: http://bit.ly/Hhb9nw
		stop()
		start()
	}

	/**
	 * Get config param from "Config.groovy", related to glassfish.
	 *
	 * @param name Param name.
	 * @return Param value.
	 */
	private getConfigParam(String name) {
		buildSettings.config.grails.glassfish[name]
	}
}
