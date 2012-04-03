import org.glassfish.embeddable.GlassFishRuntime
import grails.web.container.EmbeddableServerFactory
import grails.web.container.EmbeddableServer
import org.glassfish.embeddable.BootstrapProperties
import org.glassfish.embeddable.GlassFishProperties
import org.glassfish.embeddable.GlassFish

/**
 * Factory for glassfish server. Provides ability to embed
 * glassfish app server within Grails' run-app command.
 *
 * @author kiRach
 */
class GlassfishServerFactory implements EmbeddableServerFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	EmbeddableServer createInline(String basedir, String webXml, String contextPath, ClassLoader classLoader) {
		return new GlassfishEmbeddableServer(basedir, webXml, contextPath, classLoader)
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	EmbeddableServer createForWAR(String warPath, String contextPath) {
		return null
	}
}
