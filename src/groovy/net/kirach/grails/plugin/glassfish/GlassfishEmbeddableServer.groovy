package net.kirach.grails.plugin.glassfish

import grails.web.container.EmbeddableServer
import org.glassfish.embeddable.GlassFish
import org.glassfish.embeddable.GlassFishProperties
import org.glassfish.embeddable.BootstrapProperties
import org.glassfish.embeddable.GlassFishRuntime
import org.glassfish.embeddable.archive.ScatteredArchive
import org.glassfish.embeddable.web.WebContainer
import org.glassfish.embeddable.Deployer
import org.glassfish.embeddable.web.WebListener
import org.glassfish.embeddable.web.HttpListener
import org.glassfish.embeddable.web.Context
import org.glassfish.embeddable.web.VirtualServer
import org.glassfish.embeddable.web.config.VirtualServerConfig
import grails.util.BuildSettingsHolder
import org.glassfish.embeddable.web.config.WebContainerConfig
import java.util.logging.Level

/**
 * Glassfish embeddable server for grails use.
 *
 * @author kiRach
 */
class GlassfishEmbeddableServer implements EmbeddableServer {

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
		//just remember this params for using later
		this.glassfish = GlassFishRuntime.bootstrap().newGlassFish();
		this.webXml = webXml
		this.basedir = basedir
		this.contextPath = contextPath
		this.classLoader = classLoader
	}

	private doStart() {
		System.out.println("Starting a GlassFish server")

		//start server
		glassfish.start();

		embedded = glassfish.getService(WebContainer.class);

		WebContainerConfig config = new WebContainerConfig();
		//here we can configure our web container
		config.setPort(8080)
		embedded.setConfiguration(config);

		//let's create scattered archive - representation of our web-application
		//but we won't use it by the way described in documentation - we need it for creating custom context
		ScatteredArchive archive = new ScatteredArchive(this.contextPath, ScatteredArchive.Type.WAR, new File(basedir))
		archive.addMetadata(new File(webXml));

		//let's create context - our web application from scattered war from step before and ClassLoader, provided by grails
		this.context = embedded.createContext(new File(archive.toURI()), this.contextPath, this.classLoader)

	}

	@Override
	void start() {
		doStart()
	}

	@Override
	void start(int port) {
		doStart()
	}

	@Override
	void start(String host, int port) {
		doStart()
	}

	@Override
	void startSecure() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	void startSecure(int port) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	void startSecure(String host, int httpPort, int httpsPort) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	void stop() {
		System.out.println("Stopping server " + glassfish);
		if (glassfish != null) {
			glassfish.stop();
			glassfish.dispose();
			glassfish = null;
		}
	}

	@Override
	void restart() {
		System.out.println("Restarting a GlassFish server")
		//here it mentioned, that restart doesn't work: http://bit.ly/Hhb9nw
		glassfish.stop()
		glassfish.start()
	}
}
