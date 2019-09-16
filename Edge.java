//Daniel Sanandaj
//Tony Tong
//Young Jin Seo
//edge object that saves where its from and where it goes and its weight
public class Edge {
	int fromNode, toNode, weight;
	Edge dupe;
	boolean connected;
	
	public Edge(int fromNode, int toNode, int weight) {
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.weight = weight;
	}
	
	public String toString() {
		return toNode + " " + weight;
	}
}
