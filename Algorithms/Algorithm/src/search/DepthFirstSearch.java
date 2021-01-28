package search;

import environment.Arena;
import search.Node;

import java.util.Stack;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;


public class DepthFirstSearch {

	Node start;
	Node end;
	Arena arena;
	
	Stack<Node> stack = new Stack<Node>();
	HashSet<Node> visited = new HashSet<Node>();

	public void DepthFirstSearch(Arena arena) {
		
		this.arena = arena;

		this.start = new Node(1,18);
		this.end   = new Node(14,1);

	}

	
	private boolean node_valid(Node a) {
		
		return this.arena.arr[a.y][a.x].acc;		
		
	}

	public List<Node> get_adjacent_nodes(Node a){

		List<Node> adjacent = new ArrayList<Node>();
		
		

		return adjacent;

	}


}
