//Daniel Sanandaj
//Tony Tong
//Young Jin Seo
//node object that stores if its known its path cost and the path is takes used for dijkstra's
public class Node {
	boolean known;
	int cost;
	String path = "";
	
	public Node() {
		known = true;
		cost = 0;
		path = "";
	}
	
	public Node(boolean known, int cost, String path) {
		this.known = known;
		this.cost = cost;
		this.path = path;
	}
	
	public Node(boolean known, int cost, String path, int prev) {
		this.known = known;
		this.cost = cost;
		this.path = path + prev;
	}
}
