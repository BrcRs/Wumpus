package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Iterator;
import java.util.List;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.FSMExploreCoopAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.fsm.FSMReturnValues;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.MiniMap;
import jade.core.behaviours.OneShotBehaviour;


/**
 * Behavior that returns a the next node to go to in the exploration phase
 * Is almost the same as ExploSoloBehaviour by Cédric Herpson
 * @author Bruce Rose
 *
 */
public class MoveCalcBehaviour extends OneShotBehaviour {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1771476595429522355L;


	/**
	 * Current knowledge of the agent regarding the environment
	 */
	protected List<MiniMap> myMapHolder;

	
	protected List<String> nextNodeHolder;


	private int exitValue;

/**
 * 
 * @param myagent
 * @param nextNodeHolder
 */
	public MoveCalcBehaviour(final AbstractDedaleAgent myagent, List<String> nextNodeHolder) {
		super(myagent);
		this.myMapHolder=((FSMExploreCoopAgent)myagent).getMapHolder();

		this.nextNodeHolder = nextNodeHolder;
		this.exitValue = FSMReturnValues.FALSE;
		
	}

	@Override
	public void action() {
		//System.out.println(this.getAgent().getName() + " is calculating their next move !");
		
		if(this.myMapHolder.isEmpty()) {
			this.myMapHolder.add(new MiniMap());
		}
		/**
		 * Just added here to let you see what the agent is doing, otherwise he will be too quick
		 */
		try {
			this.myAgent.doWait(500);//250);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		System.out.println(this.myAgent + " minimap : " + this.myMapHolder.get(0));

		//0) Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition!=null & !myMapHolder.isEmpty()){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition


			//1) remove the current node from openlist and add it to closedNodes.
			this.myMapHolder.get(0).addNode(myPosition, MapAttribute.closed);

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
//			nextNodeHolder=null;
			nextNodeHolder.clear();
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				String nodeId=iter.next().getLeft();
				boolean isNewNode=this.myMapHolder.get(0).addNewNode(nodeId);
				//the node may exist, but not necessarily the edge
				if (myPosition!=nodeId) {
					this.myMapHolder.get(0).addEdge(myPosition, nodeId);
					if (nextNodeHolder.isEmpty() && isNewNode) nextNodeHolder.add(nodeId);
				}
			}

			//3) while openNodes is not empty, continues.
			if (!this.myMapHolder.get(0).hasOpenNode()){
				//Explo finished
//				finished=true;
				System.out.println(this.myAgent.getLocalName()+" - Exploration successufully done.");
				this.exitValue = FSMReturnValues.TRUE;
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNodeHolder.isEmpty()){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					List<String> openNodes = this.myMapHolder.get(0).getClosestOpenNodes(myPosition, 2);
					nextNodeHolder.add(this.myMapHolder.get(0).getShortestPath(myPosition,openNodes.get(0)).get(0));
					//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
				}else {
					//System.out.println("nextNode notNUll - "+this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"\n -- nextNode: "+nextNode);
				}

			}

		}
		else
		
			System.out.println(this.getAgent() + "'s map holder is empty :'(");
		
		if (nextNodeHolder.isEmpty()) {
			//System.out.println(this.getAgent().getLocalName() + " considers staying at " +myPosition);
		}
		else {
			//System.out.println(this.getAgent().getLocalName() + " considers going from " +myPosition+" to "+ nextNodeHolder.get(0));
		}
	}

	public int onEnd()
	{
		return exitValue;
	}

}
