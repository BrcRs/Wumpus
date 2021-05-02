package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.FSMExploreCoopAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MiniMap;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveMapBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -935420866690479768L;

	public ReceiveMapBehaviour(final AbstractDedaleAgent myagent)
	{
		super(myagent);
	}

	@Override
	public void action() {
		//System.out.println(this.getAgent().getLocalName() + " is waiting for MAPS !");

		/** Receive map */
		MessageTemplate msgTemplate = MessageTemplate.and(
				MessageTemplate.MatchProtocol("SHARE-TOPO"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		if (msgReceived!=null) {
			System.out.println(this.getAgent() + " has received a MAP from " + msgReceived.getSender().getLocalName());

			MiniMap sgreceived=null;
			try {
				//sgreceived = (SerializableSimpleGraph<String, MapAttribute>)msgReceived.getContentObject();
				sgreceived = (MiniMap)msgReceived.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			((FSMExploreCoopAgent)this.myAgent).getMapHolder().get(0).mergeMap(sgreceived);
		}
		
	}


	
}
