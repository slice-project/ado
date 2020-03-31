package org.etri.ado.gateway.openai;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.message.GetByCapabilities;
import org.etri.ado.agent.registry.message.GetById;
import org.etri.ado.agent.tuplespace.Get;
import org.etri.ado.gateway.openai.OpenAI.AgentRef;
import org.etri.ado.gateway.openai.OpenAI.Agents;
import org.etri.ado.gateway.openai.OpenAI.Capabilities;
import org.etri.ado.gateway.openai.OpenAI.Observation;
import org.javatuples.Pair;

import com.google.protobuf.StringValue;

import akka.actor.ActorRef;
import akka.cluster.client.ClusterClient.SendToAll;
import akka.pattern.Patterns;
import scala.collection.Iterator;
import scala.collection.immutable.Set;

public class ADOImpl implements ADO {
	
	private static final String ADO_PATH = "/user/ado/singleton";
	
	private final ActorRef m_clusterClient;
	private final Map<AgentInfo, AgentRef> m_agents;
	
	public ADOImpl(ActorRef clusterClient, Map<AgentInfo, AgentRef> agents) {
		m_clusterClient = clusterClient;
		m_agents = agents;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CompletionStage<AgentRef> getAgentById(StringValue in) {
		String agentId = in.getValue();
		SendToAll msg = new SendToAll(ADO_PATH, new GetById(agentId));
		CompletionStage<Object> getById = Patterns.ask(m_clusterClient, msg, Duration.ofSeconds(1000));
		Optional<AgentInfo> agentInfo = null;
		try {
			agentInfo = (Optional<AgentInfo>)getById.toCompletableFuture().get();		
		} 
		catch ( Throwable e ) {
			e.printStackTrace();			
		}		
		
		AgentRef ref = null;
		if ( agentInfo == null || !agentInfo.isPresent() ) {
			ref = AgentRef.newBuilder().setHost("").setPort(0).build();
		}
		else {		
			ref = toAgentRef(agentInfo.get());
		}		
		
		return CompletableFuture.completedFuture(ref);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompletionStage<Agents> getAgentOf(Capabilities in) {
		String[] capas = in.getCapabilitiesList().toArray(new String[in.getCapabilitiesCount()]);
		SendToAll msg = new SendToAll(ADO_PATH, new GetByCapabilities(capas));
		CompletionStage<Object> getByCapa = 
				Patterns.ask(m_clusterClient, msg, Duration.ofSeconds(1000));
		
		Optional<Set<AgentInfo>> agentInfos = null;
		try {		
			agentInfos = (Optional<Set<AgentInfo>>)getByCapa.toCompletableFuture().get();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		
		Agents agents = null;
		Agents.Builder builder = Agents.newBuilder();			
		if ( agentInfos == null || !agentInfos.isPresent() ) {
			agents = builder.build();
		}
		else {			
			Iterator<AgentInfo> iter = agentInfos.get().iterator();
			while ( iter.hasNext() ) {
				builder.addAgents(toAgentRef(iter.next()));
			}
			agents = builder.build();
		}
		
		return CompletableFuture.completedFuture(agents);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompletionStage<Observation> getObservation(StringValue in) {
		String obsId = in.getValue();
		
		SendToAll msg = new SendToAll(ADO_PATH, new Get(obsId));
		CompletionStage<Object> stage = Patterns.ask(m_clusterClient, msg, Duration.ofSeconds(1000));
		Optional<Pair<Float, Float>> pair = null;
		try {
			pair = (Optional<Pair<Float, Float>>)stage.toCompletableFuture().get();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
		}
		
		Observation obs = null;
		Observation.Builder builder = Observation.newBuilder();
		if ( pair == null || !pair.isPresent() ) {
			obs = builder.addObs(Float.NaN).addObs(Float.NaN).build();
		}
		else {
			obs = builder.addObs(pair.get().getValue0()).addObs(pair.get().getValue1()).build();
		}
		
		return CompletableFuture.completedFuture(obs);
	}
	
	private AgentRef toAgentRef(AgentInfo info) {		
		return m_agents.get(info);
	}
}
