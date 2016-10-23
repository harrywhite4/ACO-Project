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
	}

}
