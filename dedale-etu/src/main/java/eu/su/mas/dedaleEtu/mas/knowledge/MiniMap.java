package eu.su.mas.dedaleEtu.mas.knowledge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

public class MiniMap extends HashMap<String, Set<String>> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1102143711180990090L;

	private Map<String, MapAttribute> attr;

	private MapAttribute ma;

	public MiniMap(MapAttribute ma) {
		super();
		attr = new HashMap<String, MapAttribute>();
		this.ma = ma;
	}
	
	public MiniMap() {
		this(MapAttribute.open);
	}
	
	

	public void addNode(String id, MapAttribute a) {
		if (attr.containsKey(id)) {
			attr.replace(id, a);
		}
		else {
			attr.put(id, a);
		}
		if (!this.containsKey(id)) {
			this.put(id, new HashSet<String>());
		}
	}

	public boolean addNewNode(String id) {
		boolean in = this.containsKey(id);
		if (!in) {
			this.addNode(id, ma);
		}
		return !in;
	}

	public void addEdge(String i, String j) {
		this.get(i).add(j);
		this.get(j).add(i);
	}

	public boolean hasOpenNode() {
		for (String i : attr.keySet()) {
			if (attr.get(i).equals(MapAttribute.open)) {
				return true;
			}
		}
		return false;
	}

	public Set<String> getNeighborsOf(String id){
		return this.get(id);
	}
	public List<String> getShortestPath(String i, String j){
		return getShortestPath(i, j, new HashSet<>());
	}
	public List<String> getShortestPath(String i, String j, Collection<String> avoid){

		ArrayList<String> path = new ArrayList<String>();
		if (i.equals(j)) {
			path.add(i);
			return path;
		}
		
		// Breadth first search from the end to build the path

		// If start and end exist
		if (this.containsKey(i) && this.containsKey(j)) {
			// Front : open nodes
			Set<String> front = new HashSet<String>();
			// newfront : open nodes for next round
			Set<String> newfront = new HashSet<String>();
			// closed : visited nodes
			Set<String> closed = new HashSet<String>();
			// childOf(x) useful to trace back the path that brought us to x
			Map<String, String> childOf = new HashMap<String, String>();
			// We start from the end
			front.add(j);
			boolean done = false;
			while (!done) {

				// Developed node
				String root;
				// Adding developed node to the visited ones
				if (front.iterator().hasNext()) {
					root = front.iterator().next();
					front.remove(root);
					closed.add(root);
				}
				else {
					if (newfront.isEmpty())
						return path;
					front.addAll(newfront);
					newfront.clear();
					continue;
				}
				// Developing developed node
				for (String child : this.get(root)) {
					// Only added to the front if not visited already
					if (!avoid.contains(child)) {
						if (!closed.contains(child)) 
							newfront.add(child);

						// Saving the parent only if child not reached before
						if (!childOf.containsKey(child))
							childOf.put(child, root);
					}
				}
				// We're done when i is open
				if (front.contains(i) || newfront.contains(i))
					done = true;
				
			}
			String current = childOf.get(i);
			while(!current.equals(j)) {
				path.add(current);
				current = childOf.get(current);
			}
			path.add(current);

		}
		//		System.out.println("Shortest path known from " + i + " to " + j + ":\n" + path);
		return path;
	}

	public List<String> getClosestOpenNodes(String myPosition, int _nb){
		return this.getClosestNodes(myPosition, _nb, this.getOpenNodes());
	}


	private void removeNode(Set<String> ids) {

		for(String i : ids) {
			this.remove(i);
		}
		for (String k : this.keySet()) {
			this.get(k).removeAll(ids);
		}
	}

	/**
	 * Will put the two closest ones at front
	 * @return
	 */
	public List<String> getOpenNodes(){
		List<String> res = new ArrayList<String>();
		for(String k : attr.keySet()) {
			if (attr.get(k).equals(MapAttribute.open)) {
				res.add(k);
			}
		}
		Collections.shuffle(res);
		return res;
	}

	public void mergeMap(MiniMap map) {
		// TODO optimize this
		for (String k : map.keySet()) {
			if (map.getAttr(k).equals(MapAttribute.closed))
				this.addNode(k, MapAttribute.closed);
			else
				this.addNode(k, MapAttribute.open); // TODO are there more than 2 attributes ?

		}
		for (String k : map.keySet()) {			
			for (String n : map.get(k)) {
				this.addEdge(k, n);
			}
		}

	}

	private MapAttribute getAttr(String k) {
		return attr.get(k);
	}
	@Override
	public String toString() {
		String res = super.toString() + "\n" + this.attr;
		return res;
	}
	public boolean isClosed(String id) {
		return attr.get(id).equals(MapAttribute.closed);
	}

	public List<String> getClosestNodes(String myPosition, int _nb, Collection<String> dest) {
		int nb = Math.min(_nb, dest.size());
		ArrayList<String> closestNodes = new ArrayList<String>(nb);
		if (nb <= 0)
			return closestNodes;
		// Breadth first search

		// If start exists
		if (this.containsKey(myPosition)) {
			// Front : open nodes
			Set<String> front = new HashSet<String>();
			// newfront : open nodes for next round
			Set<String> newfront = new HashSet<String>();
			// closed : visited nodes
			Set<String> closed = new HashSet<String>();
			front.add(myPosition);
			boolean done = false;
			while (!done) {

				// Developed node
				String root;
				// Adding developed node to the visited ones
				if (front.iterator().hasNext()) {
					root = front.iterator().next();
					front.remove(root);
					closed.add(root);
				}
				else {
					if (newfront.isEmpty())
						return closestNodes;
					front.addAll(newfront);
					newfront.clear();
					continue;
				}
				if (dest.contains(root))
					closestNodes.add(root);
				// We're done when enough nodes are found
				if (closestNodes.size() >= nb) {
					done = true;
				}
				if (!done) {
					// Developing developed node
					for (String child : this.get(root)) {
						// Only added to the front if not visited already
						if (!closed.contains(child)) {
							newfront.add(child);
						}
						if (dest.contains(child))
							closestNodes.add(child);
					}
				}
			}			
		}
		Collections.shuffle(closestNodes);
		return closestNodes;
	}

	public List<String> getTour() {

		List<String> tour = new ArrayList<String>();
		for (String k : this.keySet()) {
			if (this.get(k).size() > 1)
				tour.add(k);
		}
		return tour;
	}
}
