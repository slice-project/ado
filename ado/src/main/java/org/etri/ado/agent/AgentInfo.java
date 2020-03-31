package org.etri.ado.agent;

import java.io.Serializable;
import java.util.Arrays;

import akka.actor.ActorRef;

public class AgentInfo implements Serializable {

	private static final long serialVersionUID = 2518838627692098579L;
	
	public final String id;
	public final String role;
	public final ActorRef ref;
	public final String[] capabilities;
	
	public AgentInfo(String id, String role, ActorRef ref, String[] capabilities) {
		this.id = id;
		this.role = role;
		this.ref = ref;
		this.capabilities = capabilities;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		result = prime * result + ((capabilities == null) ? 0 : Arrays.hashCode(capabilities));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		
		AgentInfo other = (AgentInfo) obj;
		if ( id == null ) {
			if ( other.id != null )
				return false;
		}
		else if ( role == null ) {
			if ( other.role != null )
				return false;
		}
		else if ( !id.equals(other.id) )
			return false;
		if ( ref == null )
		{
			if ( other.ref != null )
				return false;
		} 
		else if ( !ref.equals(other.ref) )
			return false;
		if ( capabilities == null ) 
		{
			if ( other.capabilities != null )
				return false;
		} 
		else if ( !Arrays.equals(capabilities, other.capabilities) )
			return false;		
		
		return true;
	}

	@Override
	public String toString() {
		return "AgentInfo [id=" + id + ", role=" + role + ", ref=" + ref + ", capabilities=" + Arrays.toString(capabilities) + "]";
	}		
}
