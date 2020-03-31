package org.etri.ado.gateway.openai;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.etri.ado.agent.AgentInfo;
import org.etri.ado.gateway.openai.OpenAI.Action;
import org.etri.ado.gateway.openai.OpenAI.Capabilities;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;

import akka.actor.ActorRef;

public class AgentImpl implements Agent {
	
	private final AgentInfo m_agent;

	public AgentImpl(AgentInfo agent) {
		m_agent = agent;
	}

	@Override
	public CompletionStage<StringValue> getId(Empty in) {		
		StringValue id = StringValue.newBuilder().setValue(m_agent.id).build();
		
		return CompletableFuture.completedFuture(id);
	}

	@Override
	public CompletionStage<BoolValue> isCapableOf(StringValue in) {
		String capability = in.getValue();
		SortedSet<String> capabilities = new TreeSet<>(Arrays.asList(m_agent.capabilities));
		BoolValue isCapable = BoolValue.newBuilder().setValue(capabilities.contains(capability)).build();
		
		return CompletableFuture.completedFuture(isCapable);
	}

	@Override
	public CompletionStage<Capabilities> getCapabilities(Empty in) {
		Capabilities.Builder builder = Capabilities.newBuilder();
		for ( int i = 0; i < m_agent.capabilities.length ; ++i ) {
			builder.addCapabilities(m_agent.capabilities[i]);
		}
				
		return CompletableFuture.completedFuture(builder.build());
	}

	@Override
	public CompletionStage<Empty> setAction(Action in) {		
		m_agent.ref.tell(in, ActorRef.noSender());
		
		Empty empty = Empty.newBuilder().build();
		return CompletableFuture.completedFuture(empty);
	}

}
