package org.etri.ado.agent.tuplespace;

import java.io.Serializable;

public class Remove implements Serializable {
	private static final long serialVersionUID = 3331907071902016432L;
	
	public final String key;

	public Remove(String key) {
		this.key = key;
	}
}
