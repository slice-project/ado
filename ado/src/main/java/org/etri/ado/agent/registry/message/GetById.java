package org.etri.ado.agent.registry.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class GetById implements Serializable {
	private static final long serialVersionUID = -4497535520894516973L;

	public final String id;

	public GetById(String id) {
		this.id = id;
	}

	public static class Context {
		public final ActorRef sender;
		public final String key;

		public Context(ActorRef sender, String key) {
			this.sender = sender;
			this.key = key;
		}
	}
}
