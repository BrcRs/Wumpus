package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
/**
 * This behavior determines where to go for a FSMAgent depending on stenches and other information
 * @author bruce
 *
 */
public class CalcTrajectoryBehaviour extends MoveCalcBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7364498568262811318L;
	private List<String> tour;
	private HashSet<String> allOthersStenches;
	private HashSet<String> avoids;
	private Set<String> stenches;
	private Map<String, String> avoid;
	private Map<String, Set<String>> othersStenches;

	public CalcTrajectoryBehaviour(AbstractDedaleAgent myagent, List<String> nextNodeHolder, 
			List<String> tour, Set<String> stenches, Map<String, Set<String>> othersStenches, Map<String, String> avoid) {
		
		super(myagent, nextNodeHolder);
		
		this.tour = tour;
		this.avoid = avoid;
		this.avoids = new HashSet<String>();

		
		this.stenches = stenches;
		this.othersStenches = othersStenches;
		this.allOthersStenches = new HashSet<String>();

	}
	@Override
	public void action() {
		//System.out.println(this.getAgent().getName() + " is calculating their next move !");
		
		// Compiling all positions to avoid
		this.avoids = new HashSet<String>();
		for (String k : this.avoid.keySet())
			avoids.add(this.avoid.get(k));
		
		// Compiling all other stenches known
		this.allOthersStenches = new HashSet<String>();
		if (this.othersStenches != null) {
			for (String k : this.othersStenches.keySet())
				allOthersStenches.addAll(this.othersStenches.get(k));
		}
		
		/**
		 * Just added here to let you see what the agent is doing, otherwise he will be too quick
		 */
		try {
			this.myAgent.doWait(500);//250);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		// Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition!=null){
			
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition


			// nextnode is the next node to go to, we reset it
			nextNodeHolder.clear();
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				String nodeId=iter.next().getLeft(); // TODO what if we miss the neighbors of those nodes from the tour we just removed?
				tour.remove(nodeId);
			}
			// Generate a new tour if done following it
			if (tour.isEmpty())
				tour.addAll(myMapHolder.get(0).getTour());

			//	 otherwise choose the next node to go after
			boolean again = nextNodeHolder.isEmpty();
			while (again){
				
				// Choose in priority its own stenches, then the ones it received, finally just explore if no stench known
				List<String> goToNodes;
				if (!stenches.isEmpty()) {
					goToNodes = this.myMapHolder.get(0).getClosestNodes(myPosition, 1, stenches);
				}else {
					if (!allOthersStenches.isEmpty()) {
						goToNodes = this.myMapHolder.get(0).getClosestNodes(myPosition, 2, allOthersStenches);
//							this.avoid.clear();
					}
					else
						// explore = go to open nodes from tour
						goToNodes = this.myMapHolder.get(0).getClosestNodes(myPosition, 2, tour);
					
				}
				List<String> path = this.myMapHolder.get(0).getShortestPath(myPosition,goToNodes.get(0), avoids);
				//System.out.println(this.myAgent + " path to " + goToNodes.get(0) + " avoiding " + avoids + " : \n" + path);
				again = path.isEmpty();
				// if no path found, it can be caused by too much positions to avoid, so we discard those in that case
				if (again) {
					avoids.clear();
					continue;
				}
				nextNodeHolder.add(path.get(0));
				//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
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

}
