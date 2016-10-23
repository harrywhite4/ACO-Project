import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

public class JSSColony extends Colony {

    public int[] machineNo = {1, 2, 3, 3, 2, 1, 2, 3, 1, 1, 3, 2};
    public int[] jobNo =  {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4};
    public int[] runTimes = {2, 3, 4, 4, 4, 1, 2, 2, 3, 3, 3, 1};
    public ArrayList<Node> nodes;
    public int numNodes;
    public int numMachines;
    public int opsPerJob;

    //set of nodes that are able to be visited base on constraints of problem
    public ArrayList<Node> allowed;
    //set of nodes already visited
    public ArrayList<Node> visited;

    public JSSColony(int numAnts) {
        super(numAnts);

        allowed = new ArrayList<Node>();
        visited = new ArrayList<Node>();
        nodes = new ArrayList<Node>();
        edgeList = new ArrayList<Edge>();
    }

    public void loadJSSP() {
        //example problem used for testing 
        numNodes = 12;
        numMachines = 3;

        char current = 'a';

        Node toadd;
        for (int i = 0; i < numNodes; i++) {
            toadd = new Node(Character.toString(current));
            nodes.add(toadd); 
            current++;
        }

        //add edges
        connectNodes();

    }

    public void resetJSS() {
        allowed.clear();
        visited.clear();

        //add first set of nodes to allowed
        allowed.add(nodes.get(0));
        for (int i = 1; i < numNodes; i++) {
            if (jobNo[i-1] != jobNo[i]) { //if first in a job
                allowed.add(nodes.get(i));
            }
        }

        int choice = (int)(Main.randGen.nextDouble() * allowed.size());
        startNode = allowed.get(choice);
    }

    private void connectNodes() {

        Edge toadd;

        for (int i=0; i < numNodes; i++) {
            for(int j=0; j< numNodes; j++) {
                if (j != i) {
                    toadd = new Edge(nodes.get(i), nodes.get(j), 1);
                    edgeList.add(toadd);
                }
            }
        }
    }

    private int calculateMakespan(ArrayList<Node> order) {

        if (order.size() != numNodes) {
            System.out.println("incomplete solution generated!!!");
        }

        int[] machineEndtimes = new int[numMachines];
        Arrays.fill(machineEndtimes, 0);
        int[] endTimes = new int[numNodes];
        Arrays.fill(endTimes, -1);
       
        int index, machine;

        for (int i=0; i < numNodes; i++) {
            index = nodes.indexOf(order.get(i)); 
            machine = machineNo[index] - 1;
            if (index == 0 || jobNo[index-1] != jobNo[index]) { //if first in a job
                endTimes[index] = machineEndtimes[machine] + runTimes[index];
                machineEndtimes[machine] += runTimes[index];
            } else {
                if (endTimes[index - 1] == -1) { System.out.println("error"); } //for debugging
                if (machineEndtimes[machine] > endTimes[index - 1]) { //if machine end time later than last operation in the same job
                    endTimes[index] = machineEndtimes[machine] + runTimes[index];
                    machineEndtimes[machine] += runTimes[index];
                } else {
                    endTimes[index] = endTimes[index-1] + runTimes[index];
                    machineEndtimes[machine] = endTimes[index];
                }
            }
        }
        
        int max = 0;
        for (int i=0; i < numMachines; i++) {
            if (machineEndtimes[i] > max) {
                max = machineEndtimes[i];
            }
        }

        return max;
    }

    //modification of findPath from Colony.java
    private List<Edge> findPathJSS(Node start) {

        Node currentNode = start;
        allowed.remove(currentNode);
        allowed.add(nodes.get(nodes.indexOf(currentNode)+1));
        visited.add(currentNode);
        ArrayList<Edge> path = new ArrayList<Edge>();

        while (!allowed.isEmpty()) {
            List<Edge> edges = edgesFromNode(currentNode);
            Iterator<Edge> edgeit = edges.iterator();

            //trim edge list to only contain edges to allowed nodes
            Edge e;
            while (edgeit.hasNext()) {
                e = edgeit.next();
                if (!allowed.contains(e.target)) {
                    edgeit.remove();
                }
            }

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

            int currentIndex = nodes.indexOf(currentNode);
            //if node is not last in it's sequence add the next to allowed
            if ((currentIndex != numNodes -1) && (jobNo[currentIndex+1] == jobNo[currentIndex])) {
                allowed.add(nodes.get(currentIndex+1));
            }

            //remove from allowed
            allowed.remove(currentNode);

            //add to visited
            visited.add(currentNode);

        }

        return path;
    }

    //override
    public void iterate() {
            
        ArrayList<List<Edge>> paths = new ArrayList<List<Edge>>();
        // Iterate through each ant
        int minMakespan = -1;
        int minMakespanIndex = 0;
        int makeSpan;
        for(int i = 0; i < numAnts; i++){
            resetJSS();
            paths.add(findPathJSS(startNode));
            makeSpan = calculateMakespan(visited);
            if (makeSpan < minMakespan || minMakespan == -1) {
                minMakespan = makeSpan;
                minMakespanIndex = i;
            }
        }
        
        // Evaporate Pheromone
        for(Edge e : edgeList){
            e.pheromone = (1 - rho) * e.pheromone;
        }
        
        // Add Pheromone to path with min makespan proportional to makespan
        List<Edge> path = paths.get(minMakespanIndex);
        StringBuilder sb = new StringBuilder("Best found path of: ");
        sb.append(path.get(0).source.label);
        // Add to each edge the same
        for(Edge e : path){
            sb.append("-" + e.target.label);
            e.pheromone += Q / minMakespan;
        }
        System.out.println(sb.toString());
        System.out.println("Makespan was " + minMakespan);
        
        System.out.println("--- Allocation to jobs ---");
        ArrayList<Node> order = new ArrayList<Node>();
        for (Edge e: path){
        	order.add(e.source);
        } 
        order.add(path.get(path.size()-1).target);
        List<List<Node>> machines = new ArrayList<List<Node>>(numMachines);
        for (int i = 0; i < numMachines; i++){
        	machines.add(new ArrayList<Node>());
        }
        
        for(Node n : order){
        	int index = nodes.indexOf(n);
        	int machineNum = machineNo[index];
        	machines.get(machineNum-1).add(n);
        }
        
        
        for (List<Node> machine : machines){
        	System.out.println("Machine #" + (machines.indexOf(machine)+1));
        	for (Node n : machine){
                int index = n.label.charAt(0) - 97;
        		System.out.printf("\tworking on job #%d for %d time\n", jobNo[index], runTimes[index]);
        	}
        }
        
        System.out.println("--- End allocation ---");
        
    }

}
