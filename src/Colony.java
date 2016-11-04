import java.util.ArrayList;
import java.util.List;
// General Colony Class extended by JSSColony.java
public class Colony {
	public ArrayList<Edge> edgeList;
	public Node startNode;
	public Node endNode;
	public double rho;
	public double alpha;
	public double Q;
	public double beta;
	public int numAnts;
	
	public Colony(int numAnts) {
		rho = 1.0;
		alpha = 1.0;
		beta = 1.0;
		Q = 5.0;
		this.numAnts = numAnts;
	}
	
	public void loadGraph() {
		edgeList = new ArrayList<Edge>();
		// For demo, a static graph.
		/*
		 * 			a---b
		 * 			|   |                            
		 * 			|   d                           
		 * 			|   |                           
		 * 			c---e---f                           
		 * 
		 */
		Node a = new Node("A");
		Node b = new Node("B");
		Node c = new Node("C");
		Node d = new Node("D");
		Node e = new Node("E");
		Node f = new Node("F");
		Edge ab = new Edge(a,b,1);
		Edge bd = new Edge(b,d,1);
		Edge ca = new Edge(c,a,1);
		Edge ce = new Edge(c,e,1);
		Edge de = new Edge(d,e,1);
		Edge ef = new Edge(e,f,1);
		edgeList.add(ab);
		edgeList.add(bd);
		edgeList.add(ca);
		edgeList.add(ce);
		edgeList.add(de);
		edgeList.add(ef);
		startNode = c;
		endNode = f;
		
	}

	public int iterate() {
		
		ArrayList<List<Edge>> paths = new ArrayList<List<Edge>>();
		// Iterate through each ant
		for(int i = 0; i < numAnts; i++){
			paths.add(findPath(startNode, endNode));
		}
		
		// Evaporate Pheromone
		for(Edge e : edgeList){
			e.pheromone = (1 - rho) * e.pheromone;
		}
		
		// Add Pheromone
		for(List<Edge> path : paths){
			StringBuilder sb = new StringBuilder("Found path of: ");
			sb.append(startNode.label);
			// Add to each edge the same
			for(Edge e : path){
				sb.append("-" + e.target.label);
				e.pheromone += Q / path.size(); // But what?
			}
			System.out.println(sb.toString());
		}
		return 0;
		
	}
	private List<Edge> findPath(Node start, Node end) {
		Node currentNode = start;
		ArrayList<Edge> path = new ArrayList<Edge>();
		while (currentNode != end){
			// Probabilistically move to next node
			List<Edge> edges = edgesFromNode(currentNode);
			int numEdges = edges.size();
			List<Double> weights = new ArrayList<Double>(numEdges);
			double weightSum = 0.0;
			
			
			// Sum all weights
			for(int i = 0; i < numEdges; i++){
				double weight = Math.pow(edges.get(i).pheromone, alpha) + Math.pow(edges.get(i).weighting, beta);
				weights.add(weight);
				weightSum += weight;
			}
			
			// Move based on weights
			double cumProb = 0.0;
			double randRoll = Main.randGen.nextDouble();
			int i = 0;
			while (cumProb < randRoll){
				cumProb += weights.get(i++)/weightSum;
			}
			Edge selectedEdge = edges.get(i-1);
			
			currentNode = selectedEdge.target;
			path.add(selectedEdge);
		}
		return path;
		 
	}
	
	public List<Edge> edgesFromNode(Node node) {
		return new ArrayList<Edge>(node.localEdges);
	}
	
}
