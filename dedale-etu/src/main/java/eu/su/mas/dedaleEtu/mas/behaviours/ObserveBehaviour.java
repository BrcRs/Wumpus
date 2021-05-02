package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.fsm.FSMReturnValues;
import jade.core.behaviours.OneShotBehaviour;
/**
 * this behavior manages the knowledge about the found and received stench positions
 * @author bruce
 *
 */
public class ObserveBehaviour extends OneShotBehaviour {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7332291974023971161L;
	private int returnValue;
	private Set<String> stenches;
	private Map<String, Set<String>> othersStenches;
	private HashSet<String> avoids;

	public ObserveBehaviour(AbstractDedaleAgent myAgent, Set<String> stenches, Map<String, Set<String>> othersStenches, Map<String, String> avoid) {
		super(myAgent);
		this.stenches = stenches;
		this.othersStenches = othersStenches;
		this.avoids = new HashSet<String>();
		for (String k : avoid.keySet())
			avoids.add(avoid.get(k));

	}

	/**
	 * Look for stenches
	 * 
	 */
	@Override
	public void action() {
		returnValue = FSMReturnValues.FALSE;
		List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

		// Retrieve stenches pos and update info

		for(Couple<String, List<Couple<Observation, Integer>>> o : lobs) {
			if (!o.getRight().isEmpty() && o.getRight().get(0).getLeft() == Observation.STENCH)
		
				stenches.add(o.getLeft());
		
			else {
				// Remove incorrect info from all knowledge
				// Received knowledge
				for (String k : othersStenches.keySet())
					othersStenches.get(k).remove(o.getLeft());
				// Own knowledges
				if (stenches.contains(o.getLeft())) {
					stenches.remove(o.getLeft());
					//System.out.println(this.myAgent + " removed " + o.getLeft() + " from personally known stenches");
				}
			}
			
		}
		// Can't reach already taken stenches TODO is that a problem for knowing the wumpus pos ?
		stenches.removeAll(avoids);

		
		if (!stenches.isEmpty()) {
			//System.out.println(this.getAgent() + " knows stenches ! " + stenches);

			returnValue = FSMReturnValues.TRUE;
		}
	}

	public int onEnd()
	{
		return returnValue;
	}

}
