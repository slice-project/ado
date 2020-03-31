package org.etri.ado.agent.cp;

import java.io.IOException;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.nd4j.linalg.io.ClassPathResource;

public class KerasModelImporter implements ModelLoader {

	@Override
	public CollaborationPolicy load(String uri) throws IOException {
				
		String simpleMlp = new ClassPathResource(uri).getFile().getPath();
		ComputationGraph model = null;
		
		try {
			model = KerasModelImport.importKerasModelAndWeights(simpleMlp);
		} 
		catch (UnsupportedKerasConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InvalidKerasConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new KerasModelAndWeight(model);
	}
}
