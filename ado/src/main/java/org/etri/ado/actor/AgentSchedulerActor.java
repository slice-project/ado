package org.etri.ado.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.etri.ado.AgentSystem;
import org.etri.ado.CLIBuilder;
import org.etri.ado.agent.Agent;
import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.ObservationNotFound;
import org.etri.ado.agent.cp.CollaborationPolicy;
import org.etri.ado.agent.registry.message.Put;
import org.etri.ado.config.Configuration;
import org.etri.ado.config.Settings;
import org.etri.ado.gateway.openai.OpenAI.Action;
import org.nd4j.linalg.api.ndarray.INDArray;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.camel.CamelMessage;
import akka.cluster.client.ClusterClientReceptionist;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.FiniteDuration;

public class AgentSchedulerActor extends AbstractActor {

	private static class Tick {	}	
	
	private final LoggingAdapter m_log = Logging.getLogger(getContext().system(), this);	
	
	public static Props prop(AgentSystem system) {
		return Props.create(AgentSchedulerActor.class, system);
	}

	private static final Tick s_tick = new Tick();
	private Cancellable m_task;
	
	private final long m_interval;
	private final AgentInfo m_info;	
	
	private List<Function<CamelMessage,Action>> m_handlers = new ArrayList<Function<CamelMessage,Action>>();
	
	private final AgentSystem m_system;
	private final Agent m_agent;
	private final CollaborationPolicy m_policy;
		
	public AgentSchedulerActor(AgentSystem system) {
		m_system = system;
		
		Configuration settings = Settings.SettingsProvider.get(system.getActorSystem());
		m_interval = Long.parseLong(settings.INTERVAL);
		m_info = new AgentInfo(settings.AGENT_ID, settings.AGENT_ROLE, getSelf(), settings.CAPABILITIES);			
		
		m_agent = system.getAgent();
		m_policy = system.getCP();
	}
	
	@Override
	public void preStart() {
		try {
			ClusterClientReceptionist.get(getContext().system()).registerService(getSelf());
			m_system.getAgentRegistryActor().tell(new Put(m_info), getSelf());			
			m_handlers.add(new CLIBuilder());
		}
		catch ( Throwable e ) {
			m_log.error(e, e.getMessage());
		}
	}
	
	@Override
	public void postStop() {
		m_task.cancel();
	}	
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
		builder.match(CamelMessage.class, this::receiveCommand);
		builder.match(Tick.class, this::receiveTick);
		
		return builder.build();
	}
	
	private void receiveTick(Tick tick) {
		
		INDArray obs = null;
		try {
			obs = m_agent.getObservations();
		}
		catch ( ObservationNotFound onf ) {
			m_log.debug("Exception: " + onf.getDetails());
			return;
		}		
		
		INDArray[] actions = m_policy.policy(obs);
		m_agent.setActions(actions);
	}
	
	private void receiveCommand( CamelMessage msg) {
		m_handlers.stream().forEach(handler -> {
			Action action = handler.apply(msg);
			if (action == null ) return;
			
			String capability = action.getCapability();
			if ( capability.equals(CLIBuilder.START) ) {
				m_task = getContext().system().scheduler().schedule(FiniteDuration.Zero(), 
						FiniteDuration.create(m_interval, TimeUnit.MILLISECONDS), 
						getSelf(), 
						s_tick, 
						getContext().getDispatcher(), 
						getSelf());
			}
			else if ( capability.equals(CLIBuilder.STOP) ) {
				m_task.cancel();
			}
		});		
	}	
	
}
