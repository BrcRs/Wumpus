package eu.su.mas.dedaleEtu.mas.agents.util;

import java.util.ArrayList;
import java.util.List;


import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;

public class AgentFinder {



	/**
	 * This method retreives other agents through the yellow pages (AMS)
	 * @param agent
	 * @return agents in network
	 */
	public static List<String> findAgents(final Agent agent)
	{
		AMSAgentDescription [] agentsDescriptionCatalog = null;
		List <String> agentsNames= new ArrayList<String>();
		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults (Long.valueOf(-1) );
			agentsDescriptionCatalog = AMSService.search(agent, new AMSAgentDescription (), c );
		}
		catch (Exception e) {
			System.out. println ( "Problem searching AMS: " + e );
			e.printStackTrace () ;
		}
		for ( int i=0; i<agentsDescriptionCatalog.length ; i++){
			AID agentID = agentsDescriptionCatalog[i ]. getName();
			agentsNames.add(agentID.getLocalName());
		}
		return agentsNames;
	}

}
