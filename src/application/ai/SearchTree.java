package application.ai;

import foundation.data.Move;
import foundation.data.MultiPrioritySet;

import java.util.LinkedList;
import java.util.Iterator;

/**
 * <p>A class representing a simulation of a series of consecutive moves of both players.</p>
 * @author Julian Betz
 * @version 1.01
 */
class SearchTree {
	private Node root;
	
	/**
	 * <p>Constructs a tree only containing the root node. It represents a simulation in which no
	 * move has been carried out yet.</p>
	 */
	protected SearchTree() {
		root = new Node();
	}
	
	/**
	 * <p>A nested class representing a node in the tree.</p>
	 * @author Julian Betz
	 * @version 1.00
	 */
	protected class Node {
		private int depth;
		private Move move;
		private Node prnt;
		private LinkedList<Node> children;
		
		/**
		 * <p>Constructs an empty node.</p>
		 */
		private Node() {
			depth = 0;
			move = null;
			prnt = null;
			children = new LinkedList<Node>();
		}
		
		/**
		 * <p>Constructs a node containing the specified move object as a value and creates the
		 * linkage to its future parent node {@code prnt}.</p>
		 * @param prnt the future parent node
		 * @param move the move to store
		 */
		private Node(Node prnt, Move move) {
			this();
			depth = prnt.depth + 1;
			this.prnt = prnt;
			this.move = move;
		}
		
		/**
		 * <p>Returns the move stored in the node.</p>
		 * @return the move stored within
		 */
		protected Move getMove() {
			return move;
		}

		/**
		 * <p>Returns the parental node of the node.</p>
		 * @return the parental node of the node
		 */
		protected Node getParent() {
			return prnt;
		}
		
		/**
		 * <p>Returns the depth of the node in the tree.</p>
		 * @return the depth of the node
		 */
		protected int getDepth() {
			return depth;
		}

		/**
		 * <p>Returns if the node is linked to at least one child node.</p>
		 * @return true if the node has at least one child
		 */
		protected boolean hasChild() {
			return children.size() > 0;
		}
		
		/**
		 * <p>Adds the specified move as a child of the node.</p>
		 * @param move the move to add
		 */
		protected void add(Move move) {
			children.addLast(new Node(this, move));
		}

		/**
		 * <p>Adds all of the moves in the {@code MultiPrioritySet} as children of the node.</p>
		 * <p>The moves are sorted according to the {@code c}-th iterator of the set.</p>
		 * @param set the set to take the moves from
		 * @param c the index of the iterator to use
		 * @throws IllegalArgumentException if {@code c<0} or {@code c>=set.dimensions()}
		 */
		protected void addAll(MultiPrioritySet<Move> set, int c) {
			Iterator<Move> it = set.iterator(c);
			while (it.hasNext())
				add(it.next());
		}
		
		/**
		 * <p>Removes and returns the first child from the node, but leaves its parental link unchanged.</p>
		 * @return the first child of the node
		 */
		protected Node pop() {
			return children.pop();
		}
		
		/**
		 * <p>Clears the list of children of the node.</p>
		 */
		protected void clear() {
			children.clear();
		}
	}
	
	/**
	 * <p>Returns the root node of the tree.</p>
	 * @return the root node
	 */
	protected Node getRoot() {
		return root;
	}
}
