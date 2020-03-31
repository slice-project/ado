'''
Created on Jun 19, 2019

@author: yhsuh
'''
from google.protobuf.empty_pb2 import Empty
from google.protobuf.wrappers_pb2 import StringValue
import grpc

from org.etri.ado.gateway.openAI_pb2 import Action
from org.etri.ado.gateway.openAI_pb2_grpc import AgentStub


class Agent(object):
    '''
    classdocs
    '''


    def __init__(self, url):
        '''
        Constructor
        '''
        channel = grpc.insecure_channel(url) 
        self.stub = AgentStub(channel)
        
        
    def get_id(self):
        respond = self.stub.getId(Empty())
        
        return respond.value
    
    def get_capabilitiies(self):
        respond = self.stub.getCapabilities(Empty())
        capaList = []
        for capability in respond.capabilities:
            capaList.append(capability)
        
        return capaList
    
    def is_capable_of(self, capability):
        respond = self.stub.isCapableOf(StringValue(value=capability))
        
        return respond.value
    
    def set_action(self, action_id, action):
        self.stub.setAction(Action(capability=action_id, actions=action))
        
        