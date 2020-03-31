package org.etri.ado.agent.tuplespace;

import java.io.Serializable;

import org.javatuples.KeyValue;
import org.javatuples.Tuple;

public class Put <T extends Tuple> implements Serializable {
	private static final long serialVersionUID = -3989643144311704286L;
	
	public final KeyValue<String,T> tuple;

	public Put(KeyValue<String,T> tuple) {
		this.tuple = tuple;
	}
	
	public static class Context<T> {
		public final KeyValue<String,T> tuple;
		
		public Context(KeyValue<String, T> tuple) {
			this.tuple = tuple;
		}
	}	
}
