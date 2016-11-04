
public class Edge {
	public Node source;
	public Node target;
	public int weighting;
	public double pheromone;
	
	public Edge(Node source, Node target){
		this.pheromone = 1.0;
		this.source = source;
		this.target = target;
		this.weighting = 0;
	}
	
	public Edge(Node source, Node target, int weighting){
		this.pheromone = 1.0;
		this.source = source;
		this.target = target;
		this.weighting = weighting;
	}
}
