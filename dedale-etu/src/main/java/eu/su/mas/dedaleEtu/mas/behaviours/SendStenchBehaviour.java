package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.util.AgentFinder;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendStenchBehaviour extends OneShotBehaviour {



	/**
	 * 
	 */
	private static final long serialVersionUID = -1356952483579975457L;
	private List<String> recipients;
	private Set<String> stenches;
	private LocalTime lastSend;
	private Duration delayMin;

	public SendStenchBehaviour (final Agent myagent, Set<String> stenches) {
		super(myagent);
		this.recipients = new ArrayList<String>();
		this.stenches = stenches;
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
		
		//TODO maybe ask if there's someone around before sending all that data
//		System.out.println(this.getAgent().getName() + " is sending their name !");
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		/** Find recipients */
		recipients = AgentFinder.findAgents(myAgent);
		recipients.remove(myAgent.getLocalName());
		if (!stenches.isEmpty() && !recipients.isEmpty() && myPosition!="" )
		{
			/** Send your stenches */
			ACLMessage msg=new ACLMessage(ACLMessage.INFORM);//FIPA
			msg.setSender( this .myAgent.getAID());
			msg.setProtocol("NAME-STENCH-AVOID") ;
			HashMap<String, Object> capsule = new HashMap<String, Object>();
			capsule.put("NAME", this.myAgent.getLocalName());
			capsule.put("STENCH", stenches);
			capsule.put("AVOID", myPosition);
			try {
				msg.setContentObject(capsule);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (String r: recipients)
			{
				//System.out.println(this.getAgent() + " is sending " + capsule + " to " + r);
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
