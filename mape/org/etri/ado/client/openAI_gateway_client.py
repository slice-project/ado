'''
Created on Jun 18, 2019

@author: yhsuh
'''
from org.etri.ado.client.ado import ADO

if __name__ == '__main__':
    
    ado = ADO('127.0.0.1:7080')
    agent1 = ado.get_agent_by_id('agent0')
    
    agent1_id = agent1.get_id()
    agent1_capas = agent1.get_capabilitiies()
    collide = agent1.is_capable_of('collide')
    movable = agent1.is_capable_of('movable')
    agent1.set_action('MoveDeltaXY', [0.13, 50.4])
    loc = ado.get_observation('agent1-loc')
    vel = ado.get_observation('agent1-velocity')
    
    print("agent1[loc={}, vel={}]".format(loc, vel))
    
    agents = ado.get_agents_by_capabilitiies(['movable','speak'])
    print(agents)

    