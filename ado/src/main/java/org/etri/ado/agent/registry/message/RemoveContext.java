package org.etri.ado.agent.registry.message;

import org.etri.ado.agent.AgentInfo;

public class RemoveContext {

	public final String key;
	public final AgentInfo info;

	public RemoveContext(String key, AgentInfo info) {
		this.key = key;
		this.info = info;
	}		
	
}
