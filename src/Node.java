

import java.util.List;

public class Node {
	public double x=0;
	public double y=0;
	public String label;
    public int machine=0;
    public int job=0;
    public int sequence=0;
    public int runTime=0;
    public List<Edge> localEdges;
    
	public Node(){

	}

	public Node(String label){
		this.label = label;
	}

	public Node(String label, int machine, int job, int sequence, int runtime){
		this.x = 0;
		this.y = 0;
		this.label = label;
        this.job = job;
        this.sequence = sequence;
        this.machine = machine;
        this.runTime = runtime;
	}
	
}
