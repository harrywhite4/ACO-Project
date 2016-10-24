import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	public static Random randGen;
	public static long seed = 12334;
	public static boolean verbose = false;
	public static void main(String[] args) {
		randGen = new Random(seed);

		runColonyWith(3.5,1.0,0.5,100.0,5,100, "a");
		randGen = new Random(seed);
		runColonyWith(5,1.0,0.5,100.0,5,100, "b");
		randGen = new Random(seed);
		runColonyWith(10,1.0,0.5,100.0,5,100, "c");
		randGen = new Random(seed);
		runColonyWith(5,5,0.5,100.0,5,100, "d");
		randGen = new Random(seed);
	}
	
	public static void runColonyWith(double a, double b, double r, double Q, int numAnts, int numIterations, String file){
		JSSColony jss = new JSSColony(numAnts);
		jss.alpha = a;
		jss.beta = b;
		jss.rho = r;
		jss.Q = Q;
		//jss.loadJSSP();
		jss.loadRandom(8,8,12);
		PrintWriter grapher;
		try {
			 grapher = new PrintWriter(file+"plot.csv");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		
		for(int i = 0; i < numIterations; i++){
			if (Main.verbose)
				System.out.println("=======Start of JSS Generation=======");
			int currentBest = jss.iterate();
			grapher.printf("%d,%d,%d\n", i, jss.bestMakespan, currentBest);

			if (Main.verbose)
				System.out.println("========End of JSS Generation========");
		}
		grapher.close();
		// Display best path
                jss.bestPath.remove(0); //remove edge from startNode from bestpath

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

		PrintWriter writer;
		try {
			writer  = new PrintWriter("python/" + file + "data.csv","UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} 
		writer.printf("%d,%d,%d\n", jss.numMachines, jss.calculateMakespan(order), jss.numNodes / jss.numMachines);
		for(Node n : order){
			int index = jss.nodes.indexOf(n);
			int machineNum = jss.machineNo[index];
			machines.get(machineNum-1).add(n);
			writer.printf("%d,%d,%d\n", machineNum-1, jss.jobNo[index], jss.runTimes[index]);
		}
		for (List<Node> machine : machines){
			System.out.println("Machine #" + (machines.indexOf(machine)+1));
			for (Node n : machine){
		        int index = n.label.charAt(0) - 97;
				System.out.printf("\tworking on job #%d for %d time\n", jss.jobNo[index], jss.runTimes[index]);
			}
		}
		writer.close();
		
		System.out.println("--- End allocation ---");
		System.out.println("Best allocation had makespan: " + jss.calculateMakespan(order));
	}

}
