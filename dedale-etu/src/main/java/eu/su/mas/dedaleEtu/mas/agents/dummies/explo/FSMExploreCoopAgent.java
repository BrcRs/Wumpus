package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.CalcTrajectoryBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.MoveBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.MoveCalcBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ObserveBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveNameBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveStenchBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SendNameBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SendStenchBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.fsm.FSMReturnValues;
import eu.su.mas.dedaleEtu.mas.knowledge.MiniMap;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

/**
 * <pre>
 * ExploreCoop agent. 
 * Basic example of how to "collaboratively" explore the map
 *  - It explore the map using a DFS algorithm and blindly tries to share the topology with the agents within reach.
 *  - The shortestPath computation is not optimized
 *  
 * It stops when all nodes have been visited.
 * 
 * 
 *  </pre>
 *  
 * @author hc
 *
 */


public class FSMExploreCoopAgent extends AbstractDedaleAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3345416156819385033L;
	private List<MiniMap> myMapHolder;
	
	// path to follow when looking for the wumpus
	private List<String> tour;
	
	private Set<String> stenches;
	private Map<String, Set<String>> othersStenches;
	// positions to avoid
	private Map<String, String> avoid;
	
	
	private static final String SN = "SendName";
	private static final String RN = "ReceiveName";
	private static final String SM = "SendMap";
	private static final String RM = "ReceiveMap";
	private static final String MoCa = "MoveCalc";
	private static final String MOVEEXP = "MoveExplo";
	
	private static final String CT = "CalcTrajectory";
	private static final String OBS = "Observe";
	private static final String RStench = "ReceiveStench";
	private static final String SStench = "SendStench";

	private static final String MOVEH = "MoveHunt";
	private static final String RNH = "ReceiveNameHunt";
	private static final String SMH = "SendMapHunt";

	
	private List<String> mapRecipients;	
	private List<String> nextNodeHolder;
	
	/** FSM setup */	
	
	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();
		mapRecipients = new ArrayList<String>();
		myMapHolder = new ArrayList<MiniMap>(1);

		nextNodeHolder = new ArrayList<String>(1);
		avoid = new HashMap<String, String>();
		stenches = new HashSet<String>();
		othersStenches = new HashMap<String, Set<String>>();
		tour = new ArrayList<String>();
		FSMBehaviour fsm = new FSMBehaviour(this);
				
		// Define the different states and behaviours
		
		fsm.registerFirstState(new SendNameBehaviour(this), SN);
		
		// Exploration part
		fsm.registerState(new ReceiveNameBehaviour(this, mapRecipients), RN);
		fsm.registerState(new SendMapBehaviour(this, mapRecipients), SM);
		fsm.registerState(new ReceiveMapBehaviour(this), RM);
		fsm.registerState(new MoveCalcBehaviour(this, nextNodeHolder), MoCa);
		fsm.registerState(new MoveBehaviour(this, nextNodeHolder, 100), MOVEEXP);
		
		// Hunting part
		fsm.registerState(new CalcTrajectoryBehaviour(this, nextNodeHolder, tour, stenches, othersStenches, avoid), CT);
		fsm.registerState(new MoveBehaviour(this, nextNodeHolder, 100), MOVEH);
		fsm.registerState(new ReceiveNameBehaviour(this, mapRecipients), RNH);
		fsm.registerState(new SendMapBehaviour(this, mapRecipients), SMH);
		fsm.registerState(new ObserveBehaviour(this, stenches, othersStenches, avoid), OBS);
		fsm.registerState(new ReceiveStenchBehaviour(this, othersStenches, avoid), RStench);
		fsm.registerState(new SendStenchBehaviour(this, stenches), SStench);
		/**TODO**
		/**/



		
		
		// Define default transitions
		// Explo
		fsm.registerDefaultTransition(SN, RN);
		fsm.registerDefaultTransition(SM, RM);
		fsm.registerDefaultTransition(RM, MoCa);
		fsm.registerDefaultTransition(MOVEEXP, SN);
		
		// Hunt
		fsm.registerDefaultTransition(CT, MOVEH);
		fsm.registerDefaultTransition(MOVEH, RNH);
		fsm.registerDefaultTransition(SMH, RStench);
		fsm.registerDefaultTransition(SStench, CT);
		fsm.registerDefaultTransition(RStench, OBS);
		
		// Define labeled transitions
		fsm.registerTransition(RN, SM, FSMReturnValues.TRUE); // 2 <=> true
		fsm.registerTransition(RN, RM, FSMReturnValues.FALSE); // 1 <=> false
		// Labels correspond to the return value of ReceiveName: if we receive one, ReceiveNameBehaviour returns 1 (onEnd() method)
		
		fsm.registerTransition(MoCa, MOVEEXP, FSMReturnValues.FALSE);
		fsm.registerTransition(MoCa, CT, FSMReturnValues.TRUE);
		fsm.registerTransition(RNH, SMH, FSMReturnValues.TRUE);
		fsm.registerTransition(RNH, RStench, FSMReturnValues.FALSE);
		fsm.registerTransition(OBS, SStench, FSMReturnValues.TRUE);
		fsm.registerTransition(OBS, CT, FSMReturnValues.FALSE);
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		List<Behaviour> lb=new ArrayList<Behaviour>();
		lb.add(fsm);
		
		addBehaviour(new startMyBehaviours(this, lb));
//		System.out.println(fsm.stringifyTransitionTable());
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}

	public List<MiniMap> getMapHolder() {
		return myMapHolder;
	}
	
	@Override
	public String toString() {
		return this.getLocalName() + " (" + this.getCurrentPosition() + ")";
	}
	
}
