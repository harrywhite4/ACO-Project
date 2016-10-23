import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	public static int numIterations = 10;
	public static int numAnts = 10;
	public static Random randGen;
	public static long seed = 12334;
	public static void main(String[] args) {
		randGen = new Random(seed);
		Colony colony = new Colony(numAnts);
                colony.loadGraph();
		for(int i = 0; i < numIterations; i++){
			System.out.println("=======Start of Generation=======");
			colony.iterate();
			System.out.println("========End of Generation========");
		}

		JSSColony jss = new JSSColony(numAnts);
		jss.loadJSSP();
		for(int i = 0; i < numIterations; i++){
			System.out.println("=======Start of JSS Generation=======");
			jss.iterate();
			System.out.println("========End of JSS Generation========");
		}
		
		// Display best path
		
        System.out.println("--- Allocation to jobs ---");
        ArrayList<Node> order = new ArrayList<Node>();
        for (Edge e: jss.bestPath){
        	order.add(e.source);
        } 
        order.add(jss.bestPath.get(jss.bestPath.size()-1).target);
        List<List<Node>> machines = new ArrayList<List<Node>>(jss.numMachines);
        for (int i = 0; i < jss.numMachines; i++){
        	machines.add(new ArrayList<Node>());
        }
        
        for(Node n : order){
        	int index = jss.nodes.indexOf(n);
        	int machineNum = jss.machineNo[index];
        	machines.get(machineNum-1).add(n);
        }
        
        
        for (List<Node> machine : machines){
        	System.out.println("Machine #" + (machines.indexOf(machine)+1));
        	for (Node n : machine){
                int index = n.label.charAt(0) - 97;
        		System.out.printf("\tworking on job #%d for %d time\n", jss.jobNo[index], jss.runTimes[index]);
        	}
        }
        
        System.out.println("--- End allocation ---");
        
        
	}

}
