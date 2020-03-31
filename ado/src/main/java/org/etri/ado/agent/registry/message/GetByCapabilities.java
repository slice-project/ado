package org.etri.ado.agent.registry.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class GetByCapabilities implements Serializable {
	private static final long serialVersionUID = 7429521851457453616L;
	
	public final String[] capabilities;

	public GetByCapabilities(String[] capabilities) {
		this.capabilities = capabilities;
	}

	public static class Context {
		public final ActorRef sender;
		public final String[] capabilities;

		public Context(ActorRef sender, String[] capabilities) {
			this.sender = sender;
			this.capabilities = capabilities;
		}
	}
}
