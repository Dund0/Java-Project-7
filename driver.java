//Daniel Sanandaj
//Tony Tong
//Young Jin Seo
import java.util.*;
import java.io.*;

public class driver {
	public static void main (String[] args) throws IOException{
		int[][] origGraph; //Original Graph from file
		Edge[][] spanningTree; //MST graph
		Integer temp; //temp int to input string integers
		int numVert, largestNeighbor = -1, numEdge; //number of verticies, largest amount of neighbors, number of edges
		double totalWeight = 0; //total weight of the graph
		String line; //input line from graph
		
		//scanners file input and string toeknizer
		Scanner scan = new Scanner(System.in);
		Scanner input = null;
		File graphFile = new File("graph.txt");
		StringTokenizer lineBreak;
		
		//check for input file
		try {
			input = new Scanner(graphFile);
		} catch(FileNotFoundException e) {
			System.out.println("graph.txt not found!");
			System.exit(0);
		}
		
		temp = Integer.parseInt(input.nextLine());
		numVert = temp.intValue();
		
		//count total edges
		while(input.hasNext()) {
			line = input.nextLine();
			lineBreak = new StringTokenizer(line, "  ", false);
			temp = Integer.parseInt(lineBreak.nextToken());
			totalWeight+=temp;
			if(temp.intValue() > largestNeighbor)
				largestNeighbor = temp.intValue();
		}
		
		//calculations
		numEdge = (int) Math.round((totalWeight)/2);
		origGraph = new int[numVert][largestNeighbor*2];
		spanningTree = new Edge[numVert][numEdge];
		input = new Scanner(graphFile);
		input.nextLine();
				
		//graph file saving
		for(int i = 0; i < numVert; i++) {
			line = input.nextLine();
			lineBreak = new StringTokenizer(line, "  ", false);
			lineBreak.nextToken();
			for(int j = 0; j < largestNeighbor*2; j++) {
				if(!lineBreak.hasMoreTokens()) {
					origGraph[i][j] = -1;
				}
				else {
					temp = Integer.parseInt(lineBreak.nextToken());
					origGraph[i][j] = temp.intValue();
				}
			}
		}

		Neighbor[][] neighbors = getNeighbors(origGraph, numVert, largestNeighbor);
		
		//menu
		String choice = "";
		while (choice != "quit") {
			System.out.println("\nThe choices for the menu are:");
			System.out.println("1. Is Connected");
			System.out.println("2. Minimum Spanning Tree");
			System.out.println("3. Shortest Path ");
			System.out.println("4. Quit");
			System.out.println("Make your choice (1-4):\n");
			scan.hasNextInt();
	        choice = scan.nextLine();
			
			switch(choice) {
			case "1":
				if(connected(origGraph, numVert))
					System.out.println("\nGraph is connected");
				else
					System.out.println("\nGraph is not connected");
				break;
			case "2":
				if(!connected(origGraph, numVert))
					System.out.println("\nThe graph isn't connected, MST not possible");
				else
					MST(origGraph, spanningTree, numVert, numEdge);
				break;
			case "3":
				System.out.println("From which node would you like to find the shortest paths (0 - 5): ");
				int node = scan.nextInt();
				shortest(node, origGraph, numVert, neighbors);
				break;
			case "4":
			case "Quit":
			case "quit":
			case "exit":
			case "stop":
				System.out.println("\nquitting");
				input.close();
				scan.close();
	            System.exit(0);
			default:
	            System.out.println("not a correct command\n");
			
			}
		}
	}

	//find shortest path
	private static void shortest(int node, int[][] origGraph, int numVert, Neighbor[][] neighbors) {
		//set cost values to infinity
		double inf = Double.POSITIVE_INFINITY;
		Node[] nodes = new Node[numVert];
		//initialize dijkstra table
		for(int w = 0; w < nodes.length; w++) {
			nodes[w] = new Node(false, (int)inf, "");
		}
		nodes[node] = new Node(true, 0, "" + node);
		boolean done = false;
		int i = node;
		//loop through neighbors
		while(!done) {
			for(int j = 0; j < neighbors[i].length && neighbors[i][j] != null; j++) {
				if(!nodes[neighbors[i][j].neighbor].known && neighbors[i][j].neighbor != i)
					if(nodes[i].cost + neighbors[i][j].weight < nodes[neighbors[i][j].neighbor].cost)
						nodes[neighbors[i][j].neighbor] = new Node(false, nodes[i].cost + neighbors[i][j].weight, nodes[i].path, neighbors[i][j].neighbor);
			}
			nodes[i].known = true;
			//next not known node with smallest cost
			i = smallestNode(nodes, node);
			
			//check if all nodes are known
			for(Node b : nodes) {
				if(b == null);
				else if(!b.known) {
					done = false;
					break;
				}
				else done = true;
			}
		}
		
		//print table
		printShortest(node, nodes, (int)inf);
	}

	//print shortest path table
	private static void printShortest(int node, Node[] nodes, int inf) {
		for(int i = 0; i < nodes.length; i++) {
			System.out.print(i + ": ");
			if(nodes[i].path.compareTo("") == 0)
				System.out.print("Not connected: no path");
			else
				System.out.print("(" + nodes[i].cost + ") ");
			for(int j = 0; j < nodes[i].path.length(); j++) {
				if(j == nodes[i].path.length()-1)
					System.out.print(nodes[i].path.charAt(j));
				else
					System.out.print(nodes[i].path.charAt(j) + " -> ");
			}
			System.out.print("\n");
		}
	}

	//find the current smallest nunkown node in the dijkstra table
	private static int smallestNode(Node[] nodes, int node) {
		int tempCost = -1;
		Node min = null;
		int minIndex = 0;
		for(int i = 0; i < nodes.length; i++) {
			if(nodes[i] != null && tempCost == -1 && i != node && !nodes[i].known) {
				min = nodes[i];
				minIndex = i;
				tempCost = 0;
			}
			else if(nodes[i] != null && i != node && min != null && !nodes[i].known) {
				if(nodes[i].cost < min.cost) {
					min = nodes[i];
					minIndex = i;
				}
			}
		}
		return minIndex;
	}

	//get the neighbors of a node and save it into a 2d matrix
	private static Neighbor[][] getNeighbors(int[][] origGraph, int vert, int maxNeighbor) {
		Neighbor[][] neighbors = new Neighbor[vert][maxNeighbor];
		for(int i = 0; i < neighbors.length; i++) {
			boolean done = false;
			for(int j = 0; j < neighbors[i].length && !done; j++) {
				for(int k = 0; k < origGraph[i].length && origGraph[i][k] != -1; k+=2) {
					neighbors[i][j] = new Neighbor(origGraph[i][k], origGraph[i][k+1]);
					j++;
				}
				done = true;
			}
		}
		return neighbors;
	}
	
	//run methods for the min spanning tree
	private static void MST(int[][] oGraph, Edge[][] spanningTree, int vert, int edge) {
		Edge[] edges = new Edge[edge];
		//create array for MST
		for(int k = 0; k < edges.length; k++) {
			for(int i = 0; i < vert; i++) {
				for(int j = 0; j < oGraph[i].length && oGraph[i][j] != -1; j+=2) {
					Edge temp = new Edge(i, oGraph[i][j], oGraph[i][j+1]);
					if(edges[0] == null) {
						edges[0] = temp;
						k++;
					}
					else {
						if(!search(edges, temp)) {
							edges[k] = temp;
							k++;
						}
					}
				}
			}
		}
		//sort by edge least to greatest weight
		sort(edges);
		//create the spanning tree maxtrix
		createSpanningTree(spanningTree, edges, vert);
		//print
		printSpanningTree(spanningTree, vert);
	}
	
	//print the min spanning tree matrix
	private static void printSpanningTree(Edge[][] spanningTree, int vert) {
		int totalCon;
		System.out.print("\n" + vert);
		for(int i = 0; i < vert; i++) {
			spanSort(spanningTree[i]);
			totalCon = 0;
			for(int k = 0; k < spanningTree.length && spanningTree[i][k] != null; k++) {
				totalCon++;
			}

			System.out.print("\n" + totalCon + " ");
			
			for(int k = 0; k < spanningTree.length && spanningTree[i][k] != null; k++) {
				System.out.print(spanningTree[i][k] + " ");
			}
		}
		System.out.print("\n");
	}

	//sort the edges from least to greatest weight
	private static void spanSort(Edge[] edges) {
		int n = edges.length; 
        for (int i = 0; i < n-1 && edges[i] != null; i++) 
            for (int j = 0; j < n-i-1 && edges[j+1] != null; j++) 
                if (edges[j].toNode > edges[j+1].toNode) 
                { 
                    // swap
                    Edge temp = edges[j]; 
                    edges[j] = edges[j+1]; 
                    edges[j+1]= temp; 
                }
	}

	//build the spanning tree matrix save path into queue
	private static void createSpanningTree(Edge[][] spanTree, Edge[] edges, int vert) {
		int totalVert = 1;
		Queue<Edge> path = new LinkedList<>();
		boolean inserted;
		for(int i = 0; i < edges.length && (totalVert != vert || !connected(spanTree, vert)); i++) {
			inserted = false;
			for(int k = 0; k < spanTree.length && !inserted ; k++) {
				if(spanTree[edges[i].fromNode][k] == null && !findPath(edges[i], path, vert, spanTree)) {
					spanTree[edges[i].fromNode][k] = edges[i];
					k = 0;
					while(spanTree[edges[i].dupe.fromNode][k] != null)
						k++;
					spanTree[edges[i].dupe.fromNode][k] = edges[i].dupe;
					path.add(edges[i]);
					inserted = true;
					totalVert++;
				}
			}
		}
	}

	//find if there is a path between nodes in the current MST uses queue
	private static boolean findPath(Edge edges, Queue<Edge> path, int vert, Edge[][] spanTree) {
		Queue<Edge> usePath = new LinkedList<>(path);
		boolean[] connected = new boolean[vert];
		while(!usePath.isEmpty()) {
			Edge edge = usePath.poll();
			connected[edge.fromNode] = true;
			connected[edge.toNode] = true;
		}
		if(connected[edges.fromNode] && connected[edges.toNode] && connected(spanTree, vert))
			return true;
		else
			return false;
	}

	//check if MST is currently connected
	private static boolean connected(Edge[][] spanTree, int vert) {
		boolean[] check = new boolean[vert];
		check[0] = true;
		int total = 1;
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        while(!stack.isEmpty()){
        	int j = stack.pop();
            for(int i = 0; i < spanTree[j].length; i++) {
            	if(i != spanTree[j].length && spanTree[j][i] != null) {
            		if(!check[spanTree[j][i].toNode]) {
                		check[spanTree[j][i].toNode] = true;
                		total++;
                		stack.push(j);
                		stack.push(spanTree[j][i].toNode);
                		j = spanTree[j][i].toNode;
                		i = spanTree[j].length;
                	}
            	}
            	else {
            		i = spanTree[j].length;
            	}
            }
        }
        
        if(total == vert)
        	return true;
        else
        	return false;
	}

	//sort edges
	private static void sort(Edge[] edges) {
		int n = edges.length; 
        for (int i = 0; i < n-1; i++) 
            for (int j = 0; j < n-i-1; j++) 
                if (edges[j].weight > edges[j+1].weight) 
                { 
                    // swap
                    Edge temp = edges[j]; 
                    edges[j] = edges[j+1]; 
                    edges[j+1]= temp; 
                } 
	}

	//if an edge is counted dont count it again and save it as its duplicate 
	private static boolean search(Edge[] edges, Edge temp) {
		for(int i = 0; i < edges.length && edges[i] != null; i++) {
			if(temp.fromNode == edges[i].toNode && temp.toNode == edges[i].fromNode) {
				edges[i].dupe = temp;
				return true;
			}
		}
		return false;
	}

	//find if the graph is connected
	private static boolean connected(int[][] graph, int vert) {
		boolean[] check = new boolean[vert];
		check[0] = true;
		int total = 1;
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        while(!stack.isEmpty()){
        	int j = stack.pop();
            for(int i = 0; i < graph[j].length; i++) {
            	if(2*i != graph[j].length && graph[j][2*i] != -1) {
            		if(!check[graph[j][2*i]]) {
                		check[graph[j][2*i]] = true;
                		total++;
                		stack.push(j);
                		stack.push(graph[j][2*i]);
                		j = graph[j][2*i];
                		i = graph[j].length;
                	}
            	}
            	else {
            		i = graph[j].length;
            	}
            }
        }
        
        if(total == vert)
        	return true;
        else
        	return false;
	}
}
