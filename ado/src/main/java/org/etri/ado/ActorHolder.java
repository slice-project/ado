package org.etri.ado;

import akka.actor.ActorRef;

public interface ActorHolder {
	
	void setActor(ActorRef actor);
	
	ActorRef getActor();
}
