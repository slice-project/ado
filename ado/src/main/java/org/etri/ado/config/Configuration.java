package org.etri.ado.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;

import akka.actor.Extension;

public class Configuration implements Extension {

	public final String AGENT_ID;
	public final String AGENT_ROLE;
	public final String[] CAPABILITIES;
	public final String POLICY;
	public final String INTERVAL;
	
	public final String DEVICE_HOST;
	public final String DEVICE_PORT;

	public Configuration(Config config) {
		AGENT_ID = config.getString("ado.agent.id");
		AGENT_ROLE = config.getString("ado.agent.role");
		ConfigList capas = config.getList("ado.agent.capabilities");
		CAPABILITIES = capas.unwrapped().toArray(new String[capas.size()]);
		POLICY = config.getString("ado.agent.policy");
		INTERVAL = config.getString("ado.agent.interval");
		
		DEVICE_HOST = config.getString("ado.device.hostname");
		DEVICE_PORT = config.getString("ado.device.port");
	}
}
