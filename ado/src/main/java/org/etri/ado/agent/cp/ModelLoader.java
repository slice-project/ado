package org.etri.ado.agent.cp;

import java.io.IOException;

public interface ModelLoader {
	
	public CollaborationPolicy load(String uri) throws IOException;	
}
