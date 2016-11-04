
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class JSSColony extends Colony {

    public ArrayList<Node> nodes; //order nodes are put into this array is important
    public int numNodes;
    public int numMachines;
    public int opsPerJob;
    public static boolean elitistAnt = true;
    public static boolean localEvap = true;

    //set of nodes that are able to be visited base on constraints of problem
    public ArrayList<Node> allowed;
    //set of nodes already visited
    public ArrayList<Node> visited;
    public List<Edge> bestPath;
    public int bestMakespan;

    public JSSColony(int numAnts) {
        super(numAnts);

        allowed = new ArrayList<Node>();
        visited = new ArrayList<Node>();
        nodes = new ArrayList<Node>();
        edgeList = new ArrayList<Edge>();
        bestMakespan = -1;
        startNode = new Node("start");
    }

    public void loadJSSP() {
        //example problem used for testing 
        int[] machineNo = {1, 2, 3, 1, 2, 3, 1, 2, 3};
        int[] jobNo =  {1, 1, 1, 2, 2, 2, 3, 3, 3};
        int[] runTimes = {3, 2, 2, 2, 4, 1, 0, 4, 3};
        opsPerJob = 3;

        numNodes = 9;
        numMachines = 3;

        char current = 'a';
        Node toadd;
        for (int i = 0; i < numNodes; i++) {
            toadd = new Node(Character.toString(current), machineNo[i], jobNo[i], (i%opsPerJob)+1, runTimes[i]);
            nodes.add(toadd); 
            current++;
        }

        //add edges
        connectNodes();

        //add edges from start node
        connectStart();
    }

    public void connectStart() {

        //add edges from start node
        Edge e;
        Node n;
        e = new Edge(startNode, nodes.get(0));
        startNode.localEdges = new ArrayList<Edge>();
        edgeList.add(e);
        for (int i = 1; i < numNodes; i++) {
            n = nodes.get(i);
            if (n.sequence == 1) { //if first in a job
                e = new Edge(startNode, n, 1);
                edgeList.add(e);
                startNode.localEdges.add(e);
            }
        }
    }
    
    public void loadRandom(int machines, int jobs, int timeRange) {
    	numMachines = machines;
    	int numJobs = jobs;
    	numNodes = numMachines * numJobs;
        opsPerJob = numMachines;
    	// Generate random requirements
    	int newMachineNo;
    	int newJobNo;
        int newSequence;
    	int newRunTime;
    	char current = 'a';
    	Node toadd;
    	for (int i = 0; i < numNodes; i++){
    		newRunTime = Main.randGen.nextInt(timeRange);
    		newJobNo = (i / numMachines)+1;
                newSequence = (i%opsPerJob)+1;
    		newMachineNo = (i % numMachines)+1;
    		System.out.printf("Generated randomly Job #%d on machine %d running for %d time\n", newJobNo, newMachineNo, newRunTime);
    		toadd = new Node(Character.toString(current), newMachineNo, newJobNo, newSequence, newRunTime);
    		nodes.add(toadd);
    		current++;
    	}

    	//add edges
    	connectNodes();
    	//add edges from start node
        connectStart();
    }

    public void resetJSS() {
        allowed.clear();
        visited.clear();

        //add first set of nodes to allowed (could replace with nodes coming from startNode)
        Node n;
        for (int i = 0; i < numNodes; i++) {
            n = nodes.get(i);
            if (n.sequence == 1) { //if first in a job
                allowed.add(n);
            }
        }

    }

    private void connectNodes() {

        Edge toadd;
        for (Node n:nodes){
        	n.localEdges = new ArrayList<Edge>();
        	for (Node m : nodes){
        		toadd = new Edge(n, m, 1);
        		n.localEdges.add(toadd);
        		edgeList.add(toadd);
        	}
        }
    }

    public int calculateMakespan(ArrayList<Node> order) {

        if (order.size() != numNodes) {
        	//debugging messages for incomplete solution
            System.out.println("incomplete solution generated!!!");
            System.out.println("soln of size " + order.size() + " it should be of size " + numNodes);
        }

        int[] machineEndtimes = new int[numMachines]; //completion time of last operation per machine
        Arrays.fill(machineEndtimes, 0);
        int[] endTimes = new int[numNodes]; //completion time of all operations
        Arrays.fill(endTimes, -1);
       
        int index, machine;
        Node n;

        for (int i=0; i < numNodes; i++) { //for each node
            n = order.get(i);
            index = nodes.indexOf(n); //index of node in the nodes arrayList;
            machine = n.machine - 1;
            if (n.sequence == 1) { //if first in a job
            	//complete in next available slot on machine
                endTimes[index] = machineEndtimes[machine] + n.runTime;
                machineEndtimes[machine] += n.runTime;
            } else {
                if (machineEndtimes[machine] > endTimes[index - 1]) { //if machine end time later than last operation in the same job
                	//complete in next available slot on machine
                    endTimes[index] = machineEndtimes[machine] + n.runTime;
                    machineEndtimes[machine] += n.runTime;
                } else {
                	//complete after previous operation in the sequence
                    endTimes[index] = endTimes[index-1] + n.runTime;
                    machineEndtimes[machine] = endTimes[index];
                }
            }
        }
        
        //find latest end time
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

            if (localEvap == true) {
                selectedEdge.pheromone = selectedEdge.pheromone * (1 - rho);
            }
            
            currentNode = selectedEdge.target;
            path.add(selectedEdge);

            int currentIndex = nodes.indexOf(currentNode);
            //if node is not last in it's sequence add the next to allowed
            if (currentNode.sequence != opsPerJob) {
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
    public int iterate() {
            
        ArrayList<List<Edge>> paths = new ArrayList<List<Edge>>();
        // Iterate through each ant
        int minMakespan = -1;
        int minMakespanIndex = 0;
        int makeSpan;
        HashMap<Integer,Integer> spans = new HashMap<Integer,Integer>();
        for(int i = 0; i < numAnts; i++){
            resetJSS();
            paths.add(findPathJSS(startNode));
            makeSpan = calculateMakespan(visited);
            Integer existingObj = spans.get(makeSpan);
            int existing;
            //set to zero if not in spans, had to remove getOrDefault as it's java 8 only
            if (existingObj == null) {
            	existing = 0;
            } else {
            	existing = existingObj.intValue();
            }
            spans.put(makeSpan, existing+1);
            if (makeSpan < minMakespan || minMakespan == -1) {
                minMakespan = makeSpan;
                minMakespanIndex = i;
            }
        }
        int maxIndex = 0;
        for (int i = 0; i < spans.size(); i++){
        	if ((Integer)(spans.keySet().toArray()[i]) > (Integer)(spans.keySet().toArray()[i])){
        		maxIndex = i;
        	}
        }
        int mode = (Integer) spans.keySet().toArray()[maxIndex];
        // Evaporate Pheromone
        for(Edge e : edgeList){
            e.pheromone = (1 - rho) * e.pheromone;
        }
        
        // Add Pheromone to path with min makespan proportional to makespan
        List<Edge> path = paths.get(minMakespanIndex);
        if (minMakespan < bestMakespan ||  bestMakespan == -1){
            bestPath = path;
            bestMakespan = minMakespan;
        }
        
        StringBuilder sb = new StringBuilder("Best found path of: ");
        sb.append(path.get(0).source.label);

        for (List<Edge> p : paths){
        	ArrayList<Node> visitedNodes = new ArrayList<Node>();
        	
        	for (Edge e : p){
                        if (e.source.label != startNode.label) { //if not start node
                                visitedNodes.add(e.source);
                        }
        	}
        	visitedNodes.add(p.get(p.size()-1).target);

        	for (Edge e : p){
        		e.pheromone += Q / calculateMakespan(visitedNodes);
        	}
        }
        // Add pheromone to Gbest (elitist ant modification)
        if (JSSColony.elitistAnt){
        	for (Edge e : bestPath){
        		e.pheromone += 1.0;
        	}
        }
        
        if (Main.verbose) {
                System.out.println(sb.toString());
        System.out.println("Makespan was " + minMakespan);
        }

        return mode;

    }
    
}
