package org.etri.ado.agent;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface Agent {	
	
	INDArray getObservations() throws ObservationNotFound;
	
	void setActions(INDArray[] actions);	
}
