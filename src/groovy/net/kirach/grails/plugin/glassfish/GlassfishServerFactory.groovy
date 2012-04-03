package net.kirach.grails.plugin.glassfish

import grails.web.container.EmbeddableServerFactory
import grails.web.container.EmbeddableServer

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
