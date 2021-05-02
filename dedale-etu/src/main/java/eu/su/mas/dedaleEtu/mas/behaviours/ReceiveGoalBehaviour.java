package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveGoalBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5831869817043577320L;
	private List<String> myGoalHolder;
	private List<String> avoidPos;
	
	public ReceiveGoalBehaviour(final AbstractDedaleAgent myagent, List<String> myGoalHolder, List<String> avoidPos)
	{
		super(myagent);
		this.myGoalHolder = myGoalHolder;
		this.avoidPos = avoidPos;
	}

	@Override
	public void action() {
//		System.out.println(this.getAgent().getLocalName() + " is waiting for names !");

		
		/** Receive name or names */
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("GOAL-AVOID"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		if (msgReceived!=null) {
			String info = msgReceived.getContent();
			Object[] a = info.split(" ");
			myGoalHolder.clear();
			myGoalHolder.add((String)a[0]);
			avoidPos.clear();
			avoidPos.add((String)a[1]);
			System.out.println(this.getAgent() + " has received " + msgReceived.getSender() + "'s order to go to " 
							+ myGoalHolder.get(0) + " without passing by " + avoidPos.get(0));
		}
	}


	
}
