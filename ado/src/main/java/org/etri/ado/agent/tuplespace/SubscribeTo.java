package org.etri.ado.agent.tuplespace;

import java.io.Serializable;

import akka.actor.ActorRef;

public class SubscribeTo implements Serializable {
	private static final long serialVersionUID = 6524254174180692054L;
	
	public final String agent;	
	
	public SubscribeTo(String agent) {
		this.agent = agent;
	}
	
	public static class Context {
		public final ActorRef sender;
		public final String agent;
		
		public Context(ActorRef sender, String agent) {
			this.sender = sender;
			this.agent = agent;
		}
	}
}
