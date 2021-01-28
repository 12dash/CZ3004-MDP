package search;

public class Node {
	
	int x;
	int y;
	
	public Node(int x, int y) {		
		this.x = x;
		this.y = y;		
	}
	
	public boolean check_equal(Node a, Node b) {
		if ((a.x == b.x)&&(a.y == b.y)) {
			return true;
		}
		else
			return false;
	}

}
