package foundation.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>A priority queue that is iterable using an arbitrary number of total orders defined on the data stored within.</p>
 * <p>This data structure ensures that no duplicate elements are stored regarding the standard comparator specified at
 * construction time.</p>
 * <p>The data is internally stored in as many red-black trees as there are comparators ordering this set. Therefore,
 * dictionary methods run in logarithmic time per comparator. Furthermore, this data structure is augmented to support an
 * iterator able to iterate the whole set of data in linear time.</p>
 * <p><i><b>Caution:</b> This set does not support {@code null} elements. It is not thread-safe and may thus be prone to errors
 * due to use in multiple threads. A change to the comparators specified at construction time will result in undefined behavior.
 * </i></p>
 * @author Julian Betz
 * @version 1.03
 */
public class MultiPrioritySet<E extends Comparable<E>> implements Set<E> {
	private static final boolean RED = false, BLACK = true;
	/**
	 * <p>The list of comparators that define the total orders used to navigate this set.</p>
	 */
	private ArrayList<Comparator<E>> comparators;
	/**
	 * <p>The list of first elements to start in order to search for a specific element in this set.</p>
	 */
	private ArrayList<Node> root;
	/**
	 * <p>The sentinel element always present in this data structure. The sentinel is not visible through external access by any
	 * means and not counted while computing the value of {@link #size() size()}.</p>
	 * <p>It does not only form the border for root and leaf nodes, but is also the element before the first and after the last
	 * value in regard to predecessor and successor linkages. <i>The parental connections (i.e. {@code left}, {@code rght} and
	 * {@code prnt}) are only supported from other nodes towards {@code nil} while any such linkages from {@code nil} lead to
	 * arbitrary nodes.</i></p>
	 */
	private Node nil;
	/**
	 * <p>Determines the size of this set. Every node except for {@code nil} is counted.</p>
	 */
	private int size;
	
	/**
	 * <p>Constructs a {@code MultiPrioritySet} that uses a standard comparator based on the {@link Comparable#compareTo(Object)
	 * compareTo} method of the runtime types of the inserted elements to navigate the data.</p>
	 */
	public MultiPrioritySet() {
		this.comparators = new ArrayList<Comparator<E>>(1);
		this.comparators.add((E a, E b) -> a.compareTo(b)); //Add the standard comparator
		nil = new Node();
		root = new ArrayList<Node>(this.comparators.size());
		for (int i = 0; i < this.comparators.size(); i++)
			root.add(nil);
		size = 0;
	}
	
	/**
	 * <p>Constructs a {@code MultiPrioritySet} that uses the specified comparators to navigate the data.</p>
	 * <p>If the list is a {@code null} reference, a standard comparator based on the {@link Comparable#compareTo(Object)
	 * compareTo} method of the runtime types of the inserted elements will be used. Otherwise, every comparator in the
	 * list will be used for comparison. The comparators will be indexed in the same order as they appear in the specified list.
	 * The standard comparator will be the first element.</p>
	 * <p><i><b>Caution:</b> A change to the specified comparators or an empty list will result in undefined behavior.</i></p>
	 * @param comparators the comparators to use navigating this set
	 */
	public MultiPrioritySet(Collection<Comparator<E>> comparators) {
		if (comparators == null) {
			this.comparators = new ArrayList<Comparator<E>>(1);
			this.comparators.add((E a, E b) -> a.compareTo(b)); //Add the standard comparator
		}
		else {
			this.comparators = new ArrayList<Comparator<E>>(comparators.size());
			for (Comparator<E> c : comparators)
				this.comparators.add(c); //Add the given comparators
		}
		nil = new Node();
		root = new ArrayList<Node>(this.comparators.size());
		for (int i = 0; i < this.comparators.size(); i++)
			root.add(nil);
		size = 0;
	}
	
	/**
	 * <p>A nested class used to implement the {@link MultiPrioritySet MultiPrioritySet} data structure.</p>
	 * <p>Every {@code Node} object represents a value inserted into the set and implements the linkages needed to navigate the
	 * data.</p>
	 * @author Julian Betz
	 * @version 1.01
	 */
	private class Node {
		private E value;
		private ArrayList<Node> prnt, left, rght, pred, succ;
		private boolean[] color;
		
		/**
		 * <p>Constructs the sentinel and creates its initial linkages.</p>
		 */
		private Node() {
			value = null;
			prnt = new ArrayList<Node>(dimensions());
			left = new ArrayList<Node>(dimensions());
			rght = new ArrayList<Node>(dimensions());
			pred = new ArrayList<Node>(dimensions());
			succ = new ArrayList<Node>(dimensions());
			color = new boolean[dimensions()];
			for (int i = 0; i < dimensions(); i++) {
				prnt.add(this);
				left.add(this);
				rght.add(this);
				pred.add(this);
				succ.add(this);
				color[i] = BLACK;
			}
		}
		
		/**
		 * <p>Constructs the node and creates its linkages.</p>
		 * <p>After termination of the constructor, the node resulting will be isolated.</p> 
		 * @param value the value represented by this node
		 */
		private Node(E value) {
			this.value = value;
			prnt = new ArrayList<Node>(dimensions());
			left = new ArrayList<Node>(dimensions());
			rght = new ArrayList<Node>(dimensions());
			pred = new ArrayList<Node>(dimensions());
			succ = new ArrayList<Node>(dimensions());
			color = new boolean[dimensions()];
			for (int i = 0; i < dimensions(); i++) {
				prnt.add(nil);
				left.add(nil);
				rght.add(nil);
				pred.add(nil);
				succ.add(nil);
				color[i] = RED;
			}
		}
		
		@Override
		public String toString() {
			if (this == nil)
				return "";
			return "(" + left.get(0).toString() +  " <" + value.toString() + " | " + (prnt.get(0) == nil ? "*"
					: prnt.get(0).value.toString()) + "> " + rght.get(0).toString() + ")";
		}
	}
	
	/**
	 * <p>A nested class representing an iterator on the data stored within the enclosing {@link MultiPrioritySet
	 * MultiPrioritySet}.</p>
	 * @author Julian Betz
	 * @version 1.01
	 */
	private class NodeIterator implements Iterator<E> {
		private Node i;
		private boolean removable;
		private int c;
		
		/**
		 * <p>Constructs an iterator able to iterate the {@code c}-th internal red-black tree.</p>
		 * @param c the internal red-black tree to iterate
		 */
		private NodeIterator(int c) {
			i = nil;
			removable = false;
			this.c = c;
		}
		
		@Override
		public boolean hasNext() {
			return i.succ.get(c) != nil;
		}
		
		@Override
		public E next() {
			if (i.succ.get(c) == nil) {
				removable = false;
				throw new NoSuchElementException("The iteration does not contain any further elements");
			}
			removable = true;
			return (i = i.succ.get(c)).value;
		}
		
		@Override
		public void remove() {
			if (removable) {
				Node node = i;
				i = i.pred.get(c);
				for (int j = 0; j < dimensions(); j++)
					discard(node, j);
				size--;
				removable = false;
			}
			else
				throw new IllegalStateException("The remove operation cannot be called before the next call of the next method");
		}
	}
	
	/**
	 * <p>Adds the specified element to this set if it is not already present.</p>
	 * <p>An element will only be inserted if it is non-{@code null} and considered not to be contained in this set by the
	 * standard comparator.</p>
	 * @param element the element to be inserted
	 * @return true if this set did not formerly contain the element
	 * @throws NullPointerException if the specified element is null
	 * @see #addAll(Collection)
	 * @see #integrate(Comparable)
	 * @see #integrateAll(Collection)
	 */
	@Override
	public boolean add(E element) {
		if (element == null)
			throw new NullPointerException("Only non-null elements are supported");
		Node node = new Node(element);
		if (insertChecked(node)) {
			for (int i = 1; i < dimensions(); i++)
				insertForced(node, i);
			size++;
			return true;
		}
		return false;
	}

	/**
	 * <p>Adds all of the elements in the specified collection to this set that are non-{@code null} and considered not to be
	 * contained in this set by the standard comparator.</p>
	 * @param c the collection to chose the elements from
	 * @return true if an element was newly inserted into this set
	 * @throws NullPointerException if one of the elements contained in the specified collection is null
	 * @see #add(Comparable)
	 * @see #integrate(Comparable)
	 * @see #integrateAll(Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (E element : c)
			changed = add(element) | changed;
		return changed;
	}
	
	/**
	 * <p>Searches for an element resembling the one specified regarding the standard comparator. If such an element is already
	 * present in this set, the element will be returned. Otherwise, the specified element will be added to this set.</p>
	 * @param element the element to search for
	 * @return the element in this set corresponding the one specified
	 * @throws NullPointerException if the specified element is null
	 * @see #integrateAll(Collection)
	 * @see #add(Comparable)
	 * @see #addAll(Collection)
	 */
	public E integrate(E element) {
		if (element == null)
			throw new NullPointerException("Only non-null elements are supported");
		Node node = search(element, 0);
		if (node == nil) {
			node = new Node(element);
			for (int i = 0; i < dimensions(); i++)
				insertForced(node, i);
			size++;
		}
		return node.value;
	}
	
	/**
	 * <p>Integrates all elements of the specified collection into this set, returning a list of the elements integrated. If an
	 * element from the collection is already present in this set before integration, its counterpart from this set will be
	 * present in the list. Otherwise, the original element will be contained therein.</p>
	 * <p>The returned list will be ordered according to the iterator of the specified collection.</p>
	 * @param c the collection to choose the elements from
	 * @return a list of elements in this set corresponding the ones specified
	 * @throws NullPointerException if one of the elements contained in the specified collection is null
	 * @see #integrate(Comparable)
	 * @see #add(Comparable)
	 * @see #addAll(Collection)
	 */
	public ArrayList<E> integrateAll(Collection<? extends E> c) {
		ArrayList<E> list = new ArrayList<E>(c.size());
		for (E element : c)
			list.add(integrate(element));
		return list;
	}
	
	/**
	 * <p>Inserts the specified node into this set regarding <i>only</i> the total order defined by the standard comparator.</p>
	 * @param node the node to be inserted
	 * @return true if this set did not formerly contain the element represented by the node
	 */
	private boolean insertChecked(Node node) {
		//Search for the proper position to insert the new node
		Node x = nil, y = root.get(0);
		while (y != nil) {
			x = y;
			if (comparators.get(0).compare(node.value, x.value) < 0)
				y = x.left.get(0);
			else if (comparators.get(0).compare(node.value, x.value) > 0)
				y = x.rght.get(0);
			//In any other case, the node to be inserted is already present
			else
				return false;
		}
		insert(node, x, 0);
		return true;
	}
	
	/**
	 * <p>Inserts the specified node into this set regarding only the total order defined by the {@code c}-th comparator. This
	 * method does <i>not</i> check if the element represented by the node is already present in this set.</p>
	 * @param node the node to be inserted
	 * @param c the number of the total order to be considered
	 */
	private void insertForced(Node node, int c) {
		assert c >= 0 : "The index c must not be negative";
		assert c < dimensions() : "The index c must not exceed dimensions() - 1";
		//Search for the proper position to insert the node
		Node x = nil, y = root.get(c);
		while (y != nil) {
			x = y;
			if (comparators.get(c).compare(node.value, x.value) < 0)
				y = x.left.get(c);
			else
				y = x.rght.get(c);
		}
		insert(node, x, c);
	}
	
	/**
	 * <p>Inserts the specified node into the {@code c}-th internal red-black tree so that {@code x} is its parent.</p>
	 * @param node the node to insert
	 * @param x the node's prospective parent
	 * @param c the index of the internal red-black tree
	 */
	private void insert(Node node, Node x, int c) {
		assert c >= 0 : "The index c must not be negative";
		assert c < dimensions() : "The index c must not exceed dimensions() - 1";
		node.prnt.set(c, x);
		//Check if the node to be inserted will be a root element
		if (x == nil) {
			root.set(c, node);
			node.pred.set(c, nil);
			node.succ.set(c, nil);
		}
		//Check if the node to be inserted will be a left child
		else if (comparators.get(c).compare(node.value, x.value) < 0) {
			x.left.set(c, node);
			node.pred.set(c, x.pred.get(c));
			node.succ.set(c, x);
		}
		//In any other case, the node to be inserted will be a right child
		else {
			x.rght.set(c, node);
			node.pred.set(c, x);
			node.succ.set(c, x.succ.get(c));
		}
		//Update the linkages and color variable
		node.left.set(c, nil);
		node.rght.set(c, nil);
		node.pred.get(c).succ.set(c, node);
		node.succ.get(c).pred.set(c, node);
		node.color[c] = RED;
		//Restore the features of the internal red-black tree
		restoreInsert(node, c);
	}
	
	/**
	 * <p>Restores the features of the internal red-black tree in regard to the {@code c}-th comparator after
	 * the execution of the {@code #insert(Node, Node, int) insert} method.</p>
	 * @param node the node that has been inserted
	 * @param c the index of the internal red-black tree to restore
	 */
	private void restoreInsert(Node node, int c) {
		assert c >= 0 : "The index c must not be negative";
		assert c < dimensions() : "The index c must not exceed dimensions() - 1";
		while (node.prnt.get(c).color[c] == RED) {
			if (node.prnt.get(c) == node.prnt.get(c).prnt.get(c).left.get(c)) {
				Node x = node.prnt.get(c).prnt.get(c).rght.get(c);
				if (x.color[c] == RED) {
					node.prnt.get(c).color[c] = BLACK;
					x.color[c] = BLACK;
					node.prnt.get(c).prnt.get(c).color[c] = RED;
					node = node.prnt.get(c).prnt.get(c);
				}
				else {
					if (node == node.prnt.get(c).rght.get(c)) {
						node = node.prnt.get(c);
						rotateLeft(node, c);
					}
					node.prnt.get(c).color[c] = BLACK;
					node.prnt.get(c).prnt.get(c).color[c] = RED;
					rotateRight(node.prnt.get(c).prnt.get(c), c);
				}
			}
			else {
				Node x = node.prnt.get(c).prnt.get(c).left.get(c);
				if (x.color[c] == RED) {
					node.prnt.get(c).color[c] = BLACK;
					x.color[c] = BLACK;
					node.prnt.get(c).prnt.get(c).color[c] = RED;
					node = node.prnt.get(c).prnt.get(c);
				}
				else {
					if (node == node.prnt.get(c).left.get(c)) {
						node = node.prnt.get(c);
						rotateRight(node, c);
					}
					node.prnt.get(c).color[c] = BLACK;
					node.prnt.get(c).prnt.get(c).color[c] = RED;
					rotateLeft(node.prnt.get(c).prnt.get(c), c);
				}
			}
		}
		root.get(c).color[c] = BLACK;
	}

	/**
	 * <p>Removes an element from this set if it is considered to be contained in this set by the standard comparator.</p>
	 * @param element the element to remove
	 * @return true if this set did formerly contain this element
	 * @throws ClassCastException if the type of the specified element is incompatible with this set
	 * @throws NullPointerException if the specified element is null
	 * @see #removeAll(Collection)
	 * @see #retainAll(Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object element) {
		if (element == null)
			throw new NullPointerException("Only non-null elements are supported");
		Node node = search((E) element, 0); //Implicitly check type compatibility
		if (node == nil)
			return false;
		for (int i = 0; i < dimensions(); i++)
			discard(node, i);
		size--;
		return true;
	}

	/**
	 * <p>Removes all of the elements in the specified collection from this set that are considered to be contained in this set
	 * by the standard comparator.</p>
	 * @param c the collection to chose the elements from
	 * @return true if an element was removed from this set
	 * @throws ClassCastException if the type of an element contained in the specified collection is incompatible with this set
	 * @throws NullPointerException if one of the elements contained in the specified collection is null
	 * @see #remove(Object)
	 * @see #retainAll(Collection)
	 * @see #clear()
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c)
			changed = remove(o) | changed;
		return changed;
	}

	/**
	 * <p>Removes all of the elements not present in the specified collection from this set.</p>
	 * <p>Elements are deemed to be present in both collections if the element from the specified collection is considered to
	 * be contained in this set by the standard comparator.</p>
	 * @param c the collection to chose the elements from
	 * @return true if an element was removed from this set
	 * @throws ClassCastException if the class of an element of this set is incompatible with the specified collection
	 * @throws NullPointerException if the specified collection is null
	 * @see #remove(Object)
	 * @see #removeAll(Collection)
	 * @see #clear()
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		NodeIterator it = new NodeIterator(0);
		while (it.hasNext()) {
			if (!c.contains(it.next())) {
				it.remove();
				changed = true;
			}
		}
		return changed;
	}

	/**
	 * <p>Discards a node from an internal red-black tree.</p>
	 * @param node the node to discard
	 * @param c the index of the internal red-black tree
	 */
	private void discard(Node node, int c) {
		assert c >= 0 : "The index c must not be negative";
		assert c < dimensions() : "The index c must not exceed dimensions() - 1";
		node.pred.get(c).succ.set(c, node.succ.get(c));
		node.succ.get(c).pred.set(c, node.pred.get(c));
		Node x, y = node;
		boolean color = y.color[c];
		if (node.left.get(c) == nil) {
			x = node.rght.get(c);
			transplant(node, x, c);
		}
		else if (node.rght.get(c) == nil) {
			x = node.left.get(c);
			transplant(node, x, c);
		}
		else {
			y = minimum(node.rght.get(c), c);
			x = y.rght.get(c);
			color = y.color[c];
			if (y.prnt.get(c) != node) {
				transplant(y, x, c);
				y.rght.set(c, node.rght.get(c));
				y.rght.get(c).prnt.set(c, y);
			}
			else
				x.prnt.set(c, y);
			transplant(node, y, c);
			y.left.set(c, node.left.get(c));
			y.left.get(c).prnt.set(c,  y);
			y.color[c] = node.color[c];
		}
		if (color == BLACK)
			restoreDiscard(x, c);
	}
	
	/**
	 * <p>Restores the features of the internal red-black tree in regard to the {@code c}-th comparator after the execution of
	 * the {@link #discard(Node, int) discard} method.</p>
	 * @param x the node to start the restoration at
	 * @param c the index of the internal red-black tree to restore
	 */
	private void restoreDiscard(Node x, int c) {
		assert c >= 0 : "The index c must not be negative";
		assert c < dimensions() : "The index c must not exceed dimensions() - 1";
		while (x != root.get(c) && x.color[c] == BLACK) {
			if (x == x.prnt.get(c).left.get(c)) {
				Node y = x.prnt.get(c).rght.get(c);
				if (y.color[c] == RED) {
					y.color[c] = BLACK;
					x.prnt.get(c).color[c] = RED;
					rotateLeft(x.prnt.get(c), c);
					y = x.prnt.get(c).rght.get(c);
				}
				if (y.left.get(c).color[c] == BLACK && y.rght.get(c).color[c] == BLACK) {
					y.color[c] = RED;
					x = x.prnt.get(c);
				}
				else {
					if (y.rght.get(c).color[c] == BLACK) {
						y.left.get(c).color[c] = BLACK;
						y.color[c] = RED;
						rotateRight(y, c);
						y = x.prnt.get(c).rght.get(c);
					}
					y.color[c] = x.prnt.get(c).color[c];
					x.prnt.get(c).color[c] = BLACK;
					y.rght.get(c).color[c] = BLACK;
					rotateLeft(x.prnt.get(c), c);
					x = root.get(c);
				}
			}
			else {
				Node y = x.prnt.get(c).left.get(c);
				if (y.color[c] == RED) {
					y.color[c] = BLACK;
					x.prnt.get(c).color[c] = RED;
					rotateRight(x.prnt.get(c), c);
					y = x.prnt.get(c).left.get(c);
				}
				if (y.left.get(c).color[c] == BLACK && y.rght.get(c).color[c] == BLACK) {
					y.color[c] = RED;
					x = x.prnt.get(c);
				}
				else {
					if (y.left.get(c).color[c] == BLACK) {
						y.rght.get(c).color[c] = BLACK;
						y.color[c] = RED;
						rotateLeft(y, c);
						y = x.prnt.get(c).left.get(c);
					}
					y.color[c] = x.prnt.get(c).color[c];
					x.prnt.get(c).color[c] = BLACK;
					y.left.get(c).color[c] = BLACK;
					rotateRight(x.prnt.get(c), c);
					x = root.get(c);
				}
			}
		}
		x.color[c] = BLACK;
	}
	
	/**
	 * <p>Replaces the node {@code x} by the node {@code y} in the {@code c}-th internal red-black tree.</p>
	 * @param x the node to replace
	 * @param y the node to replace the other one by
	 * @param c the internal red-black tree to process
	 */
	private void transplant(Node x, Node y, int c) {
		assert c >= 0 : "The index c must not be negative";
		assert c < dimensions() : "The index c must not exceed dimensions() - 1";
		if (x.prnt.get(c) == nil)
			root.set(c, y);
		else if (x == x.prnt.get(c).left.get(c))
			x.prnt.get(c).left.set(c, y);
		else
			x.prnt.get(c).rght.set(c, y);
		y.prnt.set(c, x.prnt.get(c));
	}
	
	/**
	 * <p>Exchanges the child-parent roles of {@code x} and its right child while preserving the conformity of the data's
	 * structure with its ordering regarding the {@code c}-th comparator.</p>
	 * <p><b>Requires:</b> {@code x.rght.get(c) != nil}</p>
	 * @param x the node to replace with its right child
	 * @param c the index of the internal red-black tree to process
	 */
	private void rotateLeft(Node x, int c) {
		assert c >= 0 : "The index c must not be negative";
		assert c < dimensions() : "The index c must not exceed dimensions() - 1";
		assert x.rght.get(c) != nil;
		Node y = x.rght.get(c);
		x.rght.set(c,  y.left.get(c));
		if (y.left.get(c) != nil)
			y.left.get(c).prnt.set(c, x);
		y.prnt.set(c, x.prnt.get(c));
		if (x.prnt.get(c) == nil)
			root.set(c, y);
		else if (x == x.prnt.get(c).left.get(c))
			x.prnt.get(c).left.set(c, y);
		else
			x.prnt.get(c).rght.set(c, y);
		y.left.set(c, x);
		x.prnt.set(c, y);
	}
	
	/**
	 * <p>Exchanges the child-parent roles of {@code x} and its left child while preserving the conformity of the data's
	 * structure with its ordering regarding the {@code c}-th comparator.</p>
	 * <p><b>Requires:</b> {@code x.left.get(c) != nil}</p>
	 * @param x the node to replace with its left child
	 * @param c the index of the internal red-black tree to process
	 */
	private void rotateRight(Node x, int c) {
		assert c >= 0 : "The index c must not be negative";
		assert c < dimensions() : "The index c must not exceed dimensions() - 1";
		assert x.left.get(c) != nil;
		Node y = x.left.get(c);
		x.left.set(c, y.rght.get(c));
		if (y.rght.get(c) != nil)
			y.rght.get(c).prnt.set(c, x);
		y.prnt.set(c, x.prnt.get(c));
		if (x.prnt.get(c) == nil)
			root.set(c, y);
		else if (x == x.prnt.get(c).left.get(c))
			x.prnt.get(c).left.set(c, y);
		else
			x.prnt.get(c).rght.set(c, y);
		y.rght.set(c, x);
		x.prnt.set(c, y);
	}
	
	/**
	 * <p>Returns the node representing the minimal element of a subtree of the {@code c}-th internal red-black tree, that is
	 * defined by {@code node}, being its root element.</p>
	 * @param node the root element of the subtree of the internal red-black tree
	 * @param c the index of the internal red-black tree
	 * @return the node representing the minimal element of the subtree
	 */
	private Node minimum(Node node, int c) {
		assert c >= 0 : "The index c must not be negative";
		assert c < dimensions() : "The index c must not exceed dimensions() - 1";
		while (node.left.get(c) != nil)
			node = node.left.get(c);
		return node;
	}
	
	@Override
	public void clear() {
		for (int i = 0; i < this.dimensions(); i++) {
			nil.prnt.set(i, nil);
			nil.left.set(i, nil);
			nil.rght.set(i, nil);
			nil.pred.set(i, nil);
			nil.succ.set(i, nil);
			root.set(i, nil);
		}
		size = 0;
	}

	/**
	 * <p>Returns true if this set contains an element equal to the specified element regarding the standard comparator.</p>
	 * <p>An element {@code a} will be considered to be contained in this set if there is an element {@code b} in this set so
	 * that the {@link java.util.Comparator#compare(Object, Object) compare} method of the standard comparator returns {@code 0}
	 * for {@code a} and {@code b}.</p>
	 * @param element the element to find
	 * @return true if an equivalent was found for the element
	 * @throws ClassCastException if the type of the specified element is incompatible with this set
	 * @throws NullPointerException if the specified element is null
	 * @see #containsAll(Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object element) {
		if (element == null)
			throw new NullPointerException("Only non-null elements are supported");
		Node node = search((E) element, 0); //Implicitly check type compatibility
		if (node == nil)
			return false;
		return true;
	}
	
	/**
	 * <p>Returns true if this set contains all elements contained in the specified collection.</p>
	 * <p>An element {@code a} will be considered to be contained in this set if there is an element {@code b} in this set so
	 * that the {@link java.util.Comparator#compare(Object, Object) compare} method of the standard comparator returns {@code 0}
	 * for {@code a} and {@code b}.</p>
	 * @param c the collection to chose the elements from
	 * @return true if an equivalent was found for every element in the collection
	 * @throws ClassCastException if the type of an element contained in the specified collection is incompatible with this set
	 * @throws NullPointerException if one of the elements contained in the specified collection is null
	 * @see #contains(Object)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}

	/**
	 * <p>Returns a node representing the specified element according to the {@code c}-th comparator.</p>
	 * <p>If no such node was found, {@code nil} will be returned.</p>
	 * <p><b>Requires:</b> {@code element != null}</p>
	 * @param element the element to find a representing node for
	 * @param c the index of the internal red-black tree to search
	 * @return a node representing the specified element
	 */
	private Node search(E element, int c) {
		assert element != null : "Only non-null elements are supported";
		assert c >= 0 : "The index c must not be negative";
		assert c < dimensions() : "The index c must not exceed dimensions() - 1";
		Node x = root.get(c);
		while (x != nil && comparators.get(c).compare(element, x.value) != 0) {
			if (comparators.get(c).compare(element, x.value) < 0)
				x = x.left.get(c);
			else
				x = x.rght.get(c);
		}
		return x;
	}
	
	@Override
	public boolean isEmpty() {
		return root.get(0) == nil;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	/**
	 * <p>Returns the number of total orders applied to this set. This number equals the number of internal red-black trees as
	 * well as the number of comparators specified at construction time (or {@code 1} if only the standard comparator is used).</p>
	 * @return the number of total orders applied to this set
	 * @see #size()
	 */
	public int dimensions() {
		return comparators.size();
	}
	
	/**
	 * <p>Returns the standard comparator for this set.</p>
	 * @return the standard comparator for this set
	 * @see #comparator(int)
	 * @see #iterator()
	 */
	public Comparator<E> comparator() {
		return comparator(0);
	}
	
	/**
	 * <p>Returns the {@code c}-th comparator for this set.</p>
	 * <p>Comparators are guaranteed to be ordered and indexed the same way as they appeared in the collection specified in the
	 * constructor.</p>
	 * @param c the index of the comparator to be returned
	 * @return the {@code c}-th comparator for this set
	 * @throws IllegalArgumentException if {@code c<0} or {@code c>=dimensions()}
	 * @see #comparator()
	 * @see #iterator(int)
	 */
	public Comparator<E> comparator(int c) {
		if (c < 0)
			throw new IllegalArgumentException("The index c must not be negative");
		if (c >= dimensions())
			throw new IllegalArgumentException("The index c must not exceed dimensions() - 1");
		return comparators.get(c);
	}

	/**
	 * <p>Returns an iterator based on the standard comparator.</p>
	 * @return the standard iterator for this set
	 * @see #iterator(int)
	 */
	@Override
	public Iterator<E> iterator() {
		return iterator(0);
	}
	
	/**
	 * <p>Returns an iterator based on the {@code c}-th comparator.</p>
	 * @param c the index of the comparator to determine the iteration order
	 * @return the {@code c}-th iterator for this set
	 * @throws IllegalArgumentException if {@code c<0} or {@code c>=dimensions()}
	 * @see #iterator()
	 */
	public Iterator<E> iterator(int c) {
		if (c < 0)
			throw new IllegalArgumentException("The index c must not be negative");
		if (c >= dimensions())
			throw new IllegalArgumentException("The index c must not exceed dimensions() - 1");
		return new NodeIterator(c);
	}
	
	/**
	 * <p>Converts this set into an {@link Object Object} array.</p>
	 * <p>The array will be sorted according to the standard comparator and its length will be this set's {@link #size() size}.
	 * The method runs in linear time regarding the amount of elements stored within this set.</p>
	 * @return an {@code Object} array containing all of the elements in this set
	 * @see #toArray(Object[])
	 */
	@Override
	public Object[] toArray() {
		Object[] array = new Object[size()];
		int i = 0;
		for (E e : this)
			array[i++] = e;
		return array;
	}
	
	/**
	 * <p>Fills the elements of this set into an array of the runtime type of the specified array and returns this array.</p>
	 * <p>If all elements fit in the specified array, this array will be used. Furthermore, if this process leaves room to spare
	 * in the array, the element in the array immediately following the last element inserted will be {@code null}. In any other
	 * case a new array of the runtime type of the specified array is created so that its length is the {@link #size() size} of
	 * this set.</p>
	 * <p>All elements stored in the array will be ordered according to the standard comparator.</p>
	 * @param array the array to define the array to be returned
	 * @return an array of the runtime type of the specified array containing the elements in this set
	 * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime type of every
	 * element in this set
	 * @throws NullPointerException if the specified array is null
	 * @see #toArray()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] array) {
		if (array.length < size())
			array = (T[]) Array.newInstance(array.getClass().getComponentType(), size());
		Iterator<E> it = new NodeIterator(0);
		try {
			for (int i = 0; it.hasNext(); i++)
				array[i] = (T) it.next();
		}
		catch (ClassCastException exc) {
			throw new ArrayStoreException("The type of the specified array is not a supertype of the set's generic parameter");
		}
		if (array.length > size())
			array[size()] = null;
		return array;
	}
	
	@Override
	public String toString() {
		return root.get(0).toString();
	}
}
