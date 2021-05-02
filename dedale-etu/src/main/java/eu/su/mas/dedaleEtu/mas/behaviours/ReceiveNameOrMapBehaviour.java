//package eu.su.mas.dedaleEtu.mas.behaviours;
//
//import dataStructures.serializableGraph.SerializableSimpleGraph;
//import eu.su.mas.dedale.mas.AbstractDedaleAgent;
//import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.FSMExploreCoopAgent;
//import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
//import jade.core.behaviours.SimpleBehaviour;
//import jade.lang.acl.ACLMessage;
//import jade.lang.acl.MessageTemplate;
//import jade.lang.acl.UnreadableException;
//
//public class ReceiveNameOrMapBehaviour extends SimpleBehaviour {
//
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 3246652715110083229L;
//	private int exitValue;
//
//	public ReceiveNameOrMapBehaviour(final AbstractDedaleAgent myagent)
//	{
//		super(myagent);
//	}
//	@Override
//	public void action() {
//		/** Receive name or names !!!TODO */
//		MessageTemplate msgTemplate=MessageTemplate.and(
//				MessageTemplate.MatchProtocol("SAY-NAME"),
//				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
//		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
//		if (msgReceived!=null) {
//			
//			exitValue = 1;
//		}
//		else {
//			exitValue = 0;
//		}
//
//
//		/** Receive map */
//		msgTemplate=MessageTemplate.and(
//				MessageTemplate.MatchProtocol("SHARE-TOPO"),
//				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
//		msgReceived=this.myAgent.receive(msgTemplate);
//		if (msgReceived!=null) {
//			SerializableSimpleGraph<String, MapAttribute> sgreceived=null;
//			try {
//				sgreceived = (SerializableSimpleGraph<String, MapAttribute>)msgReceived.getContentObject();
//			} catch (UnreadableException e) {
//				e.printStackTrace();
//			}
//			((FSMExploreCoopAgent)this.myAgent).getMap().mergeMap(sgreceived);
//		}
//	}
//
//	@Override
//	public boolean done() {
//		return false;
//	}
//	
//	public int onEnd()
//	{
//		return exitValue;
//	}
//
//
//}
