'''
Created on Jun 19, 2019

@author: yhsuh
'''
from google.protobuf.wrappers_pb2 import StringValue
import grpc

from org.etri.ado.client.agent import Agent
from org.etri.ado.gateway.openAI_pb2 import Capabilities
from org.etri.ado.gateway.openAI_pb2_grpc import ADOStub


class ADO(object):
    '''
    classdocs
    '''

    def __init__(self, url):
        '''
        Constructor
        '''
        channel = grpc.insecure_channel(url) 
        self.stub = ADOStub(channel)
        
        
    def get_agent_by_id(self, agent_id):
        respond = self.stub.getAgentById(StringValue(value=agent_id))
        url = respond.host + ':' + str(respond.port)       
         
        return Agent(url)        
    
    def get_agents_by_capabilitiies(self, capas):                
        respond = self.stub.getAgentOf(Capabilities(capabilities=capas))
        agentList = []
        for agent_ref in respond.agents:
            url = agent_ref.host + ':' + str(agent_ref.port)
            agentList.append(Agent(url))
        
        return agentList
    
    def get_observation(self, obs_id):
        respond = self.stub.getObservation(StringValue(value=obs_id))
        observation = []
        for obs in respond.obs:
            observation.append(obs)
        
        return observation