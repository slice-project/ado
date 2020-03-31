package org.etri.ado.agent.cp;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;

public class KerasModelAndWeight implements CollaborationPolicy {

	private final ComputationGraph m_model;
	
	public KerasModelAndWeight(ComputationGraph model) {
		m_model = model;
	}
	
	@Override
	public INDArray[] policy(INDArray input) {
		
		return m_model.output(input);
	}
}
