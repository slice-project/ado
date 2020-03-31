package org.etri.ado.agent.tuplespace;

import java.io.Serializable;

import akka.actor.ActorRef;

public class Get implements Serializable {
	private static final long serialVersionUID = 7479087255512695489L;
	
	public final String agent;
	public final String key;

	public Get(String composite) {
		String[] split = composite.split("-");
		agent = split[0];
		key = split[1];
	}
	
	public Get(String agent, String key) {
		this.agent = agent;
		this.key = key;
	}
	
	public static class Context {
		public final ActorRef sender;
		public final String agent;
		public final String key;
		
		public Context(ActorRef sender, String agent, String key) {
			this.sender = sender;
			this.agent = agent;
			this.key = key;
		}
	}
}
