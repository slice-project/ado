
// Generated by Akka gRPC. DO NOT EDIT.
package org.etri.ado.gateway.openai;

import akka.grpc.javadsl.SingleResponseRequestBuilder;
import akka.grpc.javadsl.StreamResponseRequestBuilder;

import static org.etri.ado.gateway.openai.ADO.Serializers.*;

public abstract class ADOClientPowerApi {
  
    /**
     * Lower level "lifted" version of the method, giving access to request metadata etc.
     * prefer getAgentById(com.google.protobuf.StringValue) if possible.
     */
    
      public SingleResponseRequestBuilder<com.google.protobuf.StringValue, org.etri.ado.gateway.openai.OpenAI.AgentRef> getAgentById()
    
    {
        throw new java.lang.UnsupportedOperationException();
    }
  
    /**
     * Lower level "lifted" version of the method, giving access to request metadata etc.
     * prefer getAgentOf(org.etri.ado.gateway.openai.OpenAI.Capabilities) if possible.
     */
    
      public SingleResponseRequestBuilder<org.etri.ado.gateway.openai.OpenAI.Capabilities, org.etri.ado.gateway.openai.OpenAI.Agents> getAgentOf()
    
    {
        throw new java.lang.UnsupportedOperationException();
    }
  
    /**
     * Lower level "lifted" version of the method, giving access to request metadata etc.
     * prefer getObservation(com.google.protobuf.StringValue) if possible.
     */
    
      public SingleResponseRequestBuilder<com.google.protobuf.StringValue, org.etri.ado.gateway.openai.OpenAI.Observation> getObservation()
    
    {
        throw new java.lang.UnsupportedOperationException();
    }
  
}
