
// Generated by Akka gRPC. DO NOT EDIT.
package org.etri.ado.gateway.openai;

import akka.annotation.*;
import akka.grpc.internal.*;
import akka.grpc.GrpcClientSettings;
import akka.grpc.javadsl.AkkaGrpcClient;
import akka.grpc.javadsl.SingleResponseRequestBuilder;
import akka.grpc.javadsl.StreamResponseRequestBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;

import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;

import static org.etri.ado.gateway.openai.ADO.Serializers.*;

import scala.concurrent.ExecutionContext;
import scala.compat.java8.FutureConverters;

public abstract class ADOClient extends ADOClientPowerApi implements ADO, AkkaGrpcClient {
  public static final ADOClient create(GrpcClientSettings settings, Materializer mat, ExecutionContext ec) {
    return new DefaultADOClient(settings, mat, ec);
  }

  protected final static class DefaultADOClient extends ADOClient {

      private final ClientState clientState;
      private final GrpcClientSettings settings;
      private final io.grpc.CallOptions options;
      private final Materializer mat;
      private final ExecutionContext ec;

      private DefaultADOClient(GrpcClientSettings settings, Materializer mat, ExecutionContext ec) {
        this.settings = settings;
        this.mat = mat;
        this.ec = ec;
        this.clientState = new ClientState(settings, mat, ec);
        this.options = NettyClientUtils.callOptions(settings);

        if (mat instanceof ActorMaterializer) {
          ((ActorMaterializer) mat).system().getWhenTerminated().whenComplete((v, e) -> close());
        }
      }

  
    
      private final SingleResponseRequestBuilder<com.google.protobuf.StringValue, org.etri.ado.gateway.openai.OpenAI.AgentRef> getAgentByIdRequestBuilder(scala.concurrent.Future<ManagedChannel> channel){
        return new JavaUnaryRequestBuilder<>(getAgentByIdDescriptor, channel, options, settings, ec);
      }
    
  
    
      private final SingleResponseRequestBuilder<org.etri.ado.gateway.openai.OpenAI.Capabilities, org.etri.ado.gateway.openai.OpenAI.Agents> getAgentOfRequestBuilder(scala.concurrent.Future<ManagedChannel> channel){
        return new JavaUnaryRequestBuilder<>(getAgentOfDescriptor, channel, options, settings, ec);
      }
    
  
    
      private final SingleResponseRequestBuilder<com.google.protobuf.StringValue, org.etri.ado.gateway.openai.OpenAI.Observation> getObservationRequestBuilder(scala.concurrent.Future<ManagedChannel> channel){
        return new JavaUnaryRequestBuilder<>(getObservationDescriptor, channel, options, settings, ec);
      }
    
  

      

        /**
         * For access to method metadata use the parameterless version of getAgentById
         */
        public java.util.concurrent.CompletionStage<org.etri.ado.gateway.openai.OpenAI.AgentRef> getAgentById(com.google.protobuf.StringValue request) {
          return getAgentById().invoke(request);
        }

        /**
         * Lower level "lifted" version of the method, giving access to request metadata etc.
         * prefer getAgentById(com.google.protobuf.StringValue) if possible.
         */
        
          public SingleResponseRequestBuilder<com.google.protobuf.StringValue, org.etri.ado.gateway.openai.OpenAI.AgentRef> getAgentById()
        
        {
          return clientState.withChannel( this::getAgentByIdRequestBuilder);
        }
      

        /**
         * For access to method metadata use the parameterless version of getAgentOf
         */
        public java.util.concurrent.CompletionStage<org.etri.ado.gateway.openai.OpenAI.Agents> getAgentOf(org.etri.ado.gateway.openai.OpenAI.Capabilities request) {
          return getAgentOf().invoke(request);
        }

        /**
         * Lower level "lifted" version of the method, giving access to request metadata etc.
         * prefer getAgentOf(org.etri.ado.gateway.openai.OpenAI.Capabilities) if possible.
         */
        
          public SingleResponseRequestBuilder<org.etri.ado.gateway.openai.OpenAI.Capabilities, org.etri.ado.gateway.openai.OpenAI.Agents> getAgentOf()
        
        {
          return clientState.withChannel( this::getAgentOfRequestBuilder);
        }
      

        /**
         * For access to method metadata use the parameterless version of getObservation
         */
        public java.util.concurrent.CompletionStage<org.etri.ado.gateway.openai.OpenAI.Observation> getObservation(com.google.protobuf.StringValue request) {
          return getObservation().invoke(request);
        }

        /**
         * Lower level "lifted" version of the method, giving access to request metadata etc.
         * prefer getObservation(com.google.protobuf.StringValue) if possible.
         */
        
          public SingleResponseRequestBuilder<com.google.protobuf.StringValue, org.etri.ado.gateway.openai.OpenAI.Observation> getObservation()
        
        {
          return clientState.withChannel( this::getObservationRequestBuilder);
        }
      

      
        private static MethodDescriptor<com.google.protobuf.StringValue, org.etri.ado.gateway.openai.OpenAI.AgentRef> getAgentByIdDescriptor =
          MethodDescriptor.<com.google.protobuf.StringValue, org.etri.ado.gateway.openai.OpenAI.AgentRef>newBuilder()
            .setType(
   MethodDescriptor.MethodType.UNARY 
  
  
  
)
            .setFullMethodName(MethodDescriptor.generateFullMethodName("org.etri.ado.gateway.ADO", "getAgentById"))
            .setRequestMarshaller(new ProtoMarshaller<com.google.protobuf.StringValue>(StringValueSerializer))
            .setResponseMarshaller(new ProtoMarshaller<org.etri.ado.gateway.openai.OpenAI.AgentRef>(AgentRefSerializer))
            .setSampledToLocalTracing(true)
            .build();
        
        private static MethodDescriptor<org.etri.ado.gateway.openai.OpenAI.Capabilities, org.etri.ado.gateway.openai.OpenAI.Agents> getAgentOfDescriptor =
          MethodDescriptor.<org.etri.ado.gateway.openai.OpenAI.Capabilities, org.etri.ado.gateway.openai.OpenAI.Agents>newBuilder()
            .setType(
   MethodDescriptor.MethodType.UNARY 
  
  
  
)
            .setFullMethodName(MethodDescriptor.generateFullMethodName("org.etri.ado.gateway.ADO", "getAgentOf"))
            .setRequestMarshaller(new ProtoMarshaller<org.etri.ado.gateway.openai.OpenAI.Capabilities>(CapabilitiesSerializer))
            .setResponseMarshaller(new ProtoMarshaller<org.etri.ado.gateway.openai.OpenAI.Agents>(AgentsSerializer))
            .setSampledToLocalTracing(true)
            .build();
        
        private static MethodDescriptor<com.google.protobuf.StringValue, org.etri.ado.gateway.openai.OpenAI.Observation> getObservationDescriptor =
          MethodDescriptor.<com.google.protobuf.StringValue, org.etri.ado.gateway.openai.OpenAI.Observation>newBuilder()
            .setType(
   MethodDescriptor.MethodType.UNARY 
  
  
  
)
            .setFullMethodName(MethodDescriptor.generateFullMethodName("org.etri.ado.gateway.ADO", "getObservation"))
            .setRequestMarshaller(new ProtoMarshaller<com.google.protobuf.StringValue>(StringValueSerializer))
            .setResponseMarshaller(new ProtoMarshaller<org.etri.ado.gateway.openai.OpenAI.Observation>(ObservationSerializer))
            .setSampledToLocalTracing(true)
            .build();
        

      /**
       * Initiates a shutdown in which preexisting and new calls are cancelled.
       */
      public java.util.concurrent.CompletionStage<akka.Done> close() {
        return clientState.closeCS() ;
      }

     /**
      * Returns a CompletionState that completes successfully when shutdown via close()
      * or exceptionally if a connection can not be established after maxConnectionAttempts.
      */
      public java.util.concurrent.CompletionStage<akka.Done> closed() {
        return clientState.closedCS();
      }
  }

}



