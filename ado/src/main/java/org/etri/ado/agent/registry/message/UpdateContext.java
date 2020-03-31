package org.etri.ado.agent.registry.message;

import org.etri.ado.agent.AgentInfo;

public class UpdateContext {

	public static final String BY_ID = "by_id";
	public static final String BY_ROLE = "by_role";
	public static final String BY_CAP = "by_capabilities";
	public static final String REMOVE = "remove";
	
	public final String type;
	public final AgentInfo info;

	public UpdateContext(String type, AgentInfo info) {
		this.type = type;
		this.info = info;
	}		
	
}
