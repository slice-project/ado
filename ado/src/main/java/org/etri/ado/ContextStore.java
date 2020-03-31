package org.etri.ado;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import org.etri.ado.actor.TupleSpaceActor;
import org.etri.ado.agent.tuplespace.Get;
import org.etri.ado.agent.tuplespace.Put;
import org.etri.ado.config.Settings;
import org.etri.ado.config.Configuration;
import org.javatuples.KeyValue;
import org.javatuples.Tuple;

import akka.actor.ActorRef;
import akka.pattern.Patterns;

public class ContextStore extends ActorHolderImpl implements TupleSpace {

	public ContextStore(AgentSystem system) {
		super(null);
		
		Configuration settings = Settings.SettingsProvider.get(system.getActorSystem());
		ActorRef actor = system.getActorSystem().actorOf(TupleSpaceActor.props(settings.AGENT_ID));
		setActor(actor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<? extends Tuple> get(String id) {
		CompletionStage<Object> stage = Patterns.ask(m_actor, new Get(id), Duration.ofSeconds(1000));
		Optional<Tuple> tuple = null;
		try {
			tuple = (Optional<Tuple>)stage.toCompletableFuture().get();
		}
		catch ( Throwable ignored ) {
			tuple = Optional.empty();
		}
		
		return tuple;
	}

	@Override
	public void put(String key, Tuple value) {
		KeyValue<String,Tuple> tuple = KeyValue.with(key, value);
		Put<Tuple> putCmd = new Put<Tuple>(tuple);
		m_actor.tell(putCmd, m_actor);		
	}	
}
