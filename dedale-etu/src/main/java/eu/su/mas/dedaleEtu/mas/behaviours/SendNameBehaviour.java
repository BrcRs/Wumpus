package eu.su.mas.dedaleEtu.mas.behaviours;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.util.AgentFinder;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendNameBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5254264735256714596L;

	private List<String> recipients;

	private LocalTime lastSend;

	private Duration delayMin;

	public SendNameBehaviour (final Agent myagent) {
		super(myagent);
		this.recipients = new ArrayList<String>();
		this.delayMin = Duration.ZERO.plusSeconds(1);
	}

	@Override
	public void action() {
		
		if (lastSend == null) {
			lastSend = LocalTime.now();
		}
		else {
			if (Duration.between(LocalTime.now(), lastSend).plus(delayMin).isNegative()) {
				lastSend = LocalTime.now();
			}else {
//				LocalTime now = LocalTime.now();
//				System.out.println(now + " - " + lastSend + " = " + Duration.between(now, lastSend));
//				System.out.println(lastSend + " - " + now + " = " + Duration.between(lastSend, now));
				return;
			}
		}
		
//		System.out.println(this.getAgent() + " is sending their name !");
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		/** Find recipients */
		recipients = AgentFinder.findAgents(myAgent);
		recipients.remove(myAgent.getLocalName());
		if (!recipients.isEmpty() & myPosition!="" )
		{
			/** Send your name */
			ACLMessage msg=new ACLMessage(ACLMessage.INFORM);//FIPA
			msg.setSender( this .myAgent.getAID());
			msg.setProtocol("SAY-NAME") ;

			msg.setContent(myAgent.getLocalName());

			for (String r: recipients)
			{
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
