package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.util.AgentFinder;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendGoalBehaviour extends OneShotBehaviour {



	/**
	 * 
	 */
	private static final long serialVersionUID = 2535915500589143766L;
	private List<String> recipients;
	private List<String> myGoalHolder;

	public SendGoalBehaviour (final Agent myagent, List<String> myGoalHolder) {
		super(myagent);
		this.recipients = new ArrayList<String>();
		this.myGoalHolder = myGoalHolder;

	}

	@Override
	public void action() {
//		System.out.println(this.getAgent().getName() + " is sending their name !");
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		/** Find recipients */
		recipients = AgentFinder.findAgents(myAgent);
		recipients.remove(myAgent.getLocalName());
		if (!myGoalHolder.isEmpty() && !recipients.isEmpty() && myPosition!="" )
		{
			/** Send your public goal */
			ACLMessage msg=new ACLMessage(ACLMessage.INFORM);//FIPA
			msg.setSender( this .myAgent.getAID());
			msg.setProtocol("GOAL-AVOID") ;

			msg.setContent(myGoalHolder.get(0) + " " + myPosition);

			for (String r: recipients)
			{
				System.out.println(this.getAgent() + " is sending " + myGoalHolder.get(0) + " " + myPosition + " to " + r);
				msg.addReceiver(new AID(r, AID.ISLOCALNAME));
			}
//			try {
				((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
//			}
//			catch (NullPointerException e)
//			{
//				System.err.println("Warning: Unable to send message via AbstractDedaleAgent.sendMessage()");
//			}
		}
	}



}
