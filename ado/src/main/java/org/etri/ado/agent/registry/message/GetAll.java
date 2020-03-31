package org.etri.ado.agent.registry.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class GetAll implements Serializable {
	private static final long serialVersionUID = 2006831865884998837L;

	public static class Context {
		public final ActorRef sender;

		public Context(ActorRef sender) {
			this.sender = sender;
		}
	}
	
}
