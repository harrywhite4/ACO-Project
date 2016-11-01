

import java.util.List;

public class Node {
	public double x;
	public double y;
	public String label;
        public int machine;
        public int job;
        public int sequence;
        public int runTime;
    public List<Edge> localEdges;
	public Node(){
		this.x = 0;
		this.y = 0;
                this.job = 0;
                this.sequence = 0;
                this.machine = 0;
                this.runTime = 0;
	}

	public Node(String label){
		this.x = 0;
		this.y = 0;
		this.label = label;
                this.job = 0;
                this.sequence = 0;
                this.machine = 0;
                this.runTime = 0;
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
