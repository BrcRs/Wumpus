package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.OneShotBehaviour;


/**
 * <pre>
 * This behaviour allows an agent to explore the environment and learn the associated topological map.
 * The algorithm is a pseudo - DFS computationally consuming because its not optimised at all.
 * 
 * When all the nodes around him are visited, the agent randomly select an open node and go there to restart its dfs. 
 * This (non optimal) behaviour is done until all nodes are explored. 
 * 
 * Warning, this behaviour does not save the content of visited nodes, only the topology.
 * Warning, the sub-behaviour ShareMap periodically share the whole map
 * </pre>
 * @author hc
 *
 */
public class MoveBehaviour extends OneShotBehaviour {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8320806842435056156L;
	
	private String prevPos;

	private List<String> nextNodeHolder;
	
	private int thresVictory;
	private int vp; // victory points

	/**
	 * 
	 */
	public MoveBehaviour(final AbstractDedaleAgent myagent, List<String> nextNodeHolder, int thresVictory) {
		super(myagent);
		this.nextNodeHolder = nextNodeHolder;
		prevPos = "";
		this.thresVictory = thresVictory;
		vp = 0;
	}

	@Override
	public void action() {
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		if (myPosition.equals(prevPos)) {
			//System.out.println(this.myAgent + " seems to be blocked by something...");
			vp += 1;
		}
		else
			vp = 0;
		if (vp >= thresVictory)
			System.out.println("/!\\ " + this.myAgent + " thinks they captured a Wumpus /!\\");

		if (!nextNodeHolder.isEmpty()) {
			//System.out.println(this.getAgent() + " jumps to "+nextNodeHolder.get(0));
			prevPos = myPosition;
			((AbstractDedaleAgent)this.myAgent).moveTo(nextNodeHolder.get(0));

		}

	}



}
