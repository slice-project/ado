package org.etri.ado.viewer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.etri.ado.agent.AgentInfo;

public class WorldSim extends Canvas {
	private static final long serialVersionUID = -1762020454463748151L;
	
	private Map<String, Agent> m_agents = new HashMap<String, Agent>();	
	
	public WorldSim() {

	}
	
	public synchronized void paint(Graphics g) {
		setForeground(Color.WHITE);
		m_agents.values().stream().forEach(agent -> agent.paint(g));
	}
	
	public synchronized Collection<Agent> getAgents() {
		return m_agents.values();
	}
	
	public synchronized Agent getAgent(String id) {
		return m_agents.get(id);
	}
	
	public synchronized void update(AgentInfo[] infos) {
		Set<String> previous = m_agents.keySet();
		Set<String> now = new HashSet<String>();
		for ( int i = 0; i < infos.length; ++i ) {
			now.add(infos[i].id);
		}
	
		Set<String> removed = new TreeSet<String>(previous);
		removed.removeAll(now);
		
		Set<String> added = new TreeSet<String>(now);
		added.removeAll(previous);		
		
		removed.stream().forEach(id -> {
			m_agents.remove(id);
			System.out.println("REMOVED : " + id);
		});
		
		added.stream().forEach(id -> {	
			m_agents.put(id, new Agent(id));
			System.out.println("ADDED : " + id);
		});
	}
}
