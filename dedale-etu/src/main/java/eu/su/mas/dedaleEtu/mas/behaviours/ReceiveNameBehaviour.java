package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveNameBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -628707543267148015L;
	private int exitValue;
	private List<String> otherAgents;
	
	public ReceiveNameBehaviour(final AbstractDedaleAgent myagent, List<String> otherAgents)
	{
		super(myagent);
		exitValue = 1;
		this.otherAgents = otherAgents;
	}

	@Override
	public void action() {
//		System.out.println(this.getAgent().getLocalName() + " is waiting for names !");

		otherAgents.clear();
		
		/** Receive name or names */
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("SAY-NAME"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		if (msgReceived!=null) {
			exitValue = 2;
		}
		else {
			exitValue = 1;
		}
		int cpt = 0;
		while (msgReceived != null)
		{

//			System.out.println(this.getAgent().getLocalName() + " has received " + cpt + " NAME(S) !" );
			System.out.println(this.getAgent() + " has received " + msgReceived.getContent() + "'s name !");
			cpt += 1;
			otherAgents.add(msgReceived.getContent());
			msgReceived=this.myAgent.receive(msgTemplate);
		}

	}


	public int onEnd()
	{
//		System.out.println("ReceiveName returns " + exitValue);
		return exitValue;
	}
	
}
