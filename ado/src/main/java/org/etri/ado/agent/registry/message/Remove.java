package org.etri.ado.agent.registry.message;

import java.io.Serializable;

import org.etri.ado.agent.AgentInfo;

public class Remove implements Serializable {
	private static final long serialVersionUID = -5477537964952122448L;
	
	public final String id;

	public Remove(String id) {
		this.id = id;
	}
	
	public static class Context {
		public final String id;
		public final AgentInfo info;

		public Context(String id, AgentInfo info) {
			this.id = id;
			this.info = info;
		}
	}	
}
