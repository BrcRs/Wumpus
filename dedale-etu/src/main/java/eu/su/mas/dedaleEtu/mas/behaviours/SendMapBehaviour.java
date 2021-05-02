
package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.FSMExploreCoopAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MiniMap;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * The agent periodically share its map.
 * It blindly tries to send all its graph to its friend(s)  	
 * If it was written properly, this sharing action would NOT be in a ticker behaviour and only a subgraph would be shared.
 * @author hc
 *
 */
public class SendMapBehaviour extends OneShotBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3786961943225769407L;
	private List<MiniMap> myMapHolder;
	private List<String> receivers;
	private LocalTime lastSend;
	private Duration delayMin;

	/**
	 * The agent periodically share its map.
	 * It blindly tries to send all its graph to its friend(s)  	
	 * If it was written properly, this sharing action would NOT be in a ticker behaviour and only a subgraph would be shared.
	 * @param a the agent
	 * @param period the periodicity of the behaviour (in ms)
	 * @param mymap (the map to share)
	 * @param receivers the list of agents to send the map to
	 */
	public SendMapBehaviour(FSMExploreCoopAgent a, List<String> receivers) {
		super(a);
		this.myMapHolder=a.getMapHolder();
		this.receivers=receivers;
		this.delayMin = Duration.ZERO.plusSeconds(2);
	}


	@Override
	public void action() {

		if (lastSend == null) {
			lastSend = LocalTime.now();
		}else {
			if (Duration.between(LocalTime.now(), lastSend).plus(delayMin).isNegative()) {
				lastSend = LocalTime.now();
			}else {
				return;
			}
		}
		//System.out.println(this.getAgent() + " is sending their MAP !");

		//4) At each time step, the agent blindly send all its graph to its surrounding to illustrate how to share its knowledge (the topology currently) with the the others agents. 	
		// If it was written properly, this sharing action should be in a dedicated behaviour set, the receivers be automatically computed, and only a subgraph would be shared.
		if (!this.myMapHolder.isEmpty()) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setProtocol("SHARE-TOPO");
			msg.setSender(this.myAgent.getAID());
			if (!receivers.isEmpty())
			{
				for (String agentName : receivers) {
					msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
					System.out.println(this.getAgent() + " is sending their MAP to " + agentName);

				}

				//SerializableSimpleGraph<String, MapAttribute> sg=this.myMapHolder.get(0).getSerializableGraph();
				try {					
					msg.setContentObject(this.myMapHolder.get(0));
				} catch (IOException e) {
					e.printStackTrace();
				}
				((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
			}
		}
	}


}
