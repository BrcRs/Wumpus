package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Map;
import java.util.Set;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveStenchBehaviour extends OneShotBehaviour {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1799276117141848602L;
	private Map<String, String> avoid;
	private Map<String, Set<String>> othersStenches;
	
	public ReceiveStenchBehaviour(final AbstractDedaleAgent myagent, Map<String, Set<String>> othersStenches, Map<String, String> avoid)
	{
		super(myagent);
		this.othersStenches = othersStenches;
		this.avoid = avoid;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
//		System.out.println(this.getAgent().getLocalName() + " is waiting for names !");

		
		/** Receive name or names */
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("NAME-STENCH-AVOID"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		Map<String, Object> data;
		if (msgReceived!=null) {
			try {
				data = (Map<String, Object>) msgReceived.getContentObject();
				String name = (String) data.get("NAME");
				Set<String> stenchesReceived = (Set<String>) data.get("STENCH");
				String avoidReceived = (String) data.get("AVOID");
				
				othersStenches.put(name, stenchesReceived);
				avoid.put(name, avoidReceived);
				
				//System.out.println(this.getAgent() + " has received [" + stenchesReceived + " ; " + avoidReceived + "] from " + msgReceived.getSender().getLocalName());
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	
}
