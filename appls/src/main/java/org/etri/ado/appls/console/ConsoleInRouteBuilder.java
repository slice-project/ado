package org.etri.ado.appls.console;

import org.apache.camel.builder.RouteBuilder;

import akka.actor.ActorRef;
import akka.camel.internal.component.CamelPath;

public class ConsoleInRouteBuilder extends RouteBuilder {

	private String m_uri;
	
	public ConsoleInRouteBuilder(ActorRef responder) {
		m_uri = CamelPath.toUri(responder);
	}
	
	@Override
	public void configure() throws Exception {
		from("stream:in").to(m_uri);
	}

}
