package org.etri.ado.agent.cp;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface CollaborationPolicy {

	INDArray[] policy(INDArray input);
}
