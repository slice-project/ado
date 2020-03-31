package org.etri.ado.agent.registry.message;

import java.io.Serializable;

import org.etri.ado.agent.AgentInfo;

public class Put implements Serializable {
	private static final long serialVersionUID = -8475362875278793954L;
	
	public final AgentInfo agent;

	public Put(AgentInfo agent) {
		this.agent = agent;
	}
	
}
