package org.etri.ado;

import akka.actor.ActorRef;

public class ActorHolderImpl implements ActorHolder {

	protected ActorRef m_actor;
	
	public ActorHolderImpl(ActorRef actor) {
		m_actor = actor;
	}
	
	@Override
	public void setActor(ActorRef actor) {
		m_actor = actor;
	}

	@Override
	public ActorRef getActor() {
		return m_actor;
	}

}
