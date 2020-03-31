package org.etri.ado.viewer;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.etri.ado.agent.AgentInfo;
import org.javatuples.Pair;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import akka.actor.Props;
import scala.concurrent.duration.FiniteDuration;

public class ADORenderer extends AbstractActor {

	private static class Tick {	}

	public static Props props(AgentDO ado) {
		return Props.create(ADORenderer.class, ado);
	}

	private final AgentDO m_ado;
	private Cancellable m_task;
	
	private JFrame m_renderFrame;
	private WorldSim m_world = new WorldSim();	
	private static final Tick s_tick = new Tick();	

	public ADORenderer(AgentDO ado) throws InterruptedException, ExecutionException {
		m_ado = ado;
	}
	
	@Override
	public void preStart() {
		m_renderFrame = new JFrame("World Viewer");
		m_renderFrame.add(m_world);
		m_renderFrame.setSize(Agent.WIDTH * 2, Agent.HEIGHT * 2);
		m_renderFrame.setVisible(true);
				
		m_task = getContext().system().scheduler().schedule(FiniteDuration.Zero(), 
															FiniteDuration.create(30, TimeUnit.MILLISECONDS), 
															getSelf(), 
															s_tick, 
															getContext().getDispatcher(), 
															getSelf());		
	}
	
	@Override
	public void postStop() {
		m_task.cancel();
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Tick.class, this::receiveTick).build();
	}
	
	private void receiveTick(Tick tick) {
		AgentInfo[] agentInfos = m_ado.getAgentAll();
		m_world.update(agentInfos);
		
		m_world.getAgents().stream().forEach(agent -> {
			Optional<Pair<Float,Float>> pair = m_ado.getObservation(agent.getId(), "loc");
			if ( !pair.isPresent() ) {
				return;
			}				
			agent.setLocation(pair.get());
//			m_world.repaint();
		});
		
		m_world.repaint();
	}
}