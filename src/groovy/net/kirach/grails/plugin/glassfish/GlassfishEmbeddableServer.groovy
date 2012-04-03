package net.kirach.grails.plugin.glassfish

import static grails.build.logging.GrailsConsole.instance as CONSOLE

import grails.util.BuildSettings
import grails.util.BuildSettingsHolder
import grails.web.container.EmbeddableServer
import org.glassfish.embeddable.GlassFish
import org.glassfish.embeddable.GlassFishRuntime
import org.glassfish.embeddable.archive.ScatteredArchive
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
		this.buildSettings = BuildSettingsHolder.getSettings()
		this.glassfish = GlassFishRuntime.bootstrap().newGlassFish();

		//just remember this params for using later
		this.webXml = webXml
		this.basedir = basedir
		this.contextPath = contextPath
		this.classLoader = classLoader
	}

	private doStart(String host, int httpPort) {
		CONSOLE.updateStatus "Starting GlassFish server"

		//start server
		this.glassfish.start();

		this.embedded = glassfish.getService(WebContainer.class);

		WebContainerConfig config = new WebContainerConfig();
		//here we can configure our web container
		config.setPort(httpPort)
		config.setHostNames(host)
		this.embedded.setConfiguration(config);

		if (getConfigParam("comet")) {
			CONSOLE.updateStatus "Enabling GlassFish Comet support"
			def commandRunner = glassfish.getCommandRunner()
			def res = commandRunner.run("set", "server-config.network-config.protocols.protocol." + config.getListenerName() + ".http.comet-support-enabled=true")
		}

		//let's create scattered archive - representation of our web-application
		//but we won't use it by the way described in documentation - we need it for creating custom context
		ScatteredArchive archive = new ScatteredArchive(this.contextPath, ScatteredArchive.Type.WAR, new File(this.basedir))
		archive.addMetadata(new File(this.webXml));

		//let's create context - our web application from scattered war from step before and ClassLoader, provided by grails
		this.context = this.embedded.createContext(new File(archive.toURI()), this.contextPath, this.classLoader)

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

	private getConfigParam(String name) {
		buildSettings.config.grails.glassfish[name]
	}
}
