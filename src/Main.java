import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
	public static Random randGen;
	public static long seed = 12334;
	public static boolean verbose = false;
	public static int numTestMachines = 20;
	public static int numTestJobs = 20;
	public static void main(String[] args) throws IOException {
		randGen = new Random(seed);
		
		/*runColonyWith(3.5,1.0,0.5,100.0,5,100, "a");
		randGen = new Random(seed);
		runColonyWith(5,1.0,0.5,100.0,5,100, "b");
		randGen = new Random(seed);*/
		randGen = new Random(seed);
		runColonyWith(4,0,0.5,200.0,10,50, "a");
		randGen = new Random(seed);
		runColonyWith(3.5,0,0.5,200.0,10,50, "b");
		randGen = new Random(seed);
		runColonyWith(3.0,0,0.5,200.0,10,50, "c");
		randGen = new Random(seed);
		runColonyWith(2.5,0,0.5,200.0,10,50, "d");
		randGen = new Random(seed);
		runColonyWith(2.0,0,0.5,200.0,10,50, "e");
	}
	
	public static void runColonyWith(double a, double b, double r, double Q, int numAnts, int stagnationTime, String file){
		JSSColony jss = new JSSColony(numAnts);
		jss.alpha = a;
		jss.beta = b;
		jss.rho = r;
		jss.Q = Q;
		//jss.loadJSSP();
		jss.loadRandom(numTestMachines,numTestJobs,12);
		Charter charter = new Charter("ACO with a=" + a+ ", "+numTestMachines+" machines, " + numTestJobs+" jobs");
		
		
		int[] improvements = new int[stagnationTime];
		Arrays.fill(improvements, -1);
		int improvIndex = 0;
		int currentVal = jss.iterate();
		int time = 0;
		boolean hasStagnated = false;
		while (!hasStagnated){
			int currentBest = jss.iterate();
			charter.addValue(currentBest, 0, time);
			charter.addValue(jss.bestMakespan, 1, time);
			time++;
			improvements[improvIndex++%stagnationTime] = jss.bestMakespan - currentVal;
			currentVal = jss.bestMakespan;
			
			hasStagnated = true;
			for(int imp : improvements){
				if (imp != 0){
					hasStagnated = false;
				}
			}
		}
		try {
			charter.generateGraph(file);
		} catch (IOException e1) {
			System.out.println("Error creating image file");
			e1.printStackTrace();
		}
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
			int machineNum = n.machine;
			machines.get(machineNum-1).add(n);
			writer.printf("%d,%d,%d\n", machineNum-1, n.job, n.runTime);
		}
		for (List<Node> machine : machines){
			System.out.println("Machine #" + (machines.indexOf(machine)+1));
			for (Node n : machine){
		        int index = n.label.charAt(0) - 97;
				System.out.printf("\tworking on job #%d for %d time\n", n.job, n.runTime);
			}
		}
		writer.close();
		
		System.out.println("--- End allocation ---");
		System.out.println("Best allocation had makespan: " + jss.calculateMakespan(order));
	}

}
