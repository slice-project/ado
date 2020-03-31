package org.etri.ado.agent.registry.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class SubscribeTo implements Serializable {
	private static final long serialVersionUID = -7202780914246770666L;
	
	public static class Context {
		public final ActorRef sender;
		
		public Context(ActorRef sender) {
			this.sender = sender;
		}
	}
}
