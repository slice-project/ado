package org.etri.ado.agent.registry.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class GetByRole implements Serializable {
	private static final long serialVersionUID = -770725144747505535L;
	
	public final String role;

	public GetByRole(String role) {
		this.role = role;
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
