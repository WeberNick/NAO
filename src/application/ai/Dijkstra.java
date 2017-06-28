package application.ai;

import foundation.exception.*;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Collections;

/**
 * This class implements the Dijkstra algorithm in order to find the shortest path from a source vertex to any other vertex.
 * <p>
 * In this case it is needed to find the best route for NAO to walk from one colored point to another colored point within the game world.
 * @author Nick Weber
 * @version 2.0
 * @see Vertex
 */
public class Dijkstra 
{
	/**
	 * The nine vertices in the game world.
	 * <p>
	 * The order is as follow:<br>
	 * First vertex: <Code>v0</Code> south side (of the play board) on the left.<br>
	 * Now anti clockwise around the play board, with two vertices on every side of the play board.<br>
	 * The ninth vertex <Code>v8</Code> is in the middle of the play board.
	 * @see Vertex
	 */
	private static Vertex v0 = new Vertex("pink"), 
			v1 = new Vertex("orange"), 
			v2 = new Vertex("blue"),
			v3 = new Vertex("purple"),
			v4 = new Vertex("red"),
			v5 = new Vertex("khaki"),
			v6 = new Vertex("grey"),
			v7 = new Vertex("green"),
			v8 = new Vertex("beige");
	
	/**
	 * Computes the shortest paths from a source vertex to every other vertex.
	 * @param source the source vertex
	 * @see Vertex
	 */
	private static void computePaths(Vertex source) 
	{		
		assignAdjacencyList();
		source.setDistance(0);
		PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>();
		queue.add(source);
		while (!queue.isEmpty()) 
		{
			Vertex help = queue.poll();
			for(Edge edge : help.getAdjacencyList()) 
			{
				Vertex temp = edge.getTarget();
				double weight = edge.getWeight();
				double distance = help.getDistance() + weight;
				if(distance < temp.getDistance()) 
				{
					queue.remove(temp);
					temp.setDistance(distance);
					temp.setPredecessor(help);
					queue.add(temp);
				}
			}
		}
	}
	
	/**
	 * Fills a <Code>List</Code> with the shortest path from the current source vertex to the target vertex.
	 * @param target the target vertex to reach with shortest path
	 * @return the list containing the vertices on the shortest path route
	 * @see List
	 * @see Vertex
	 */
	private static List<Vertex> getShortestPath(Vertex target) 
	{
		List<Vertex> shortestPath = new ArrayList<Vertex>();
		for(Vertex v = target; v != null; v = v.getPredecessor())
			shortestPath.add(v);
		Collections.reverse(shortestPath);
		return shortestPath;
	}
	
	/**
	 * All this method does is setting the <Code>Edges</Code> for every vertex.
	 * @see Edge
	 */
	private static void assignAdjacencyList()
	{
		v0.setAdjacencyList(new Edge[] { new Edge(v7, 1), new Edge(v1, 1), new Edge(v8, 1.1) });
		v1.setAdjacencyList(new Edge[] { new Edge(v0, 1), new Edge(v2, 1), new Edge(v8, 1.1) });
		v2.setAdjacencyList(new Edge[] { new Edge(v1, 1), new Edge(v3, 1), new Edge(v8, 1.1) });
		v3.setAdjacencyList(new Edge[] { new Edge(v2, 1), new Edge(v4, 1), new Edge(v8, 1.1) });
		v4.setAdjacencyList(new Edge[] { new Edge(v3, 1), new Edge(v5, 1), new Edge(v8, 1.1) });
		v5.setAdjacencyList(new Edge[] { new Edge(v4, 1), new Edge(v6, 1), new Edge(v8, 1.1) });
		v6.setAdjacencyList(new Edge[] { new Edge(v5, 1), new Edge(v7, 1), new Edge(v8, 1.1) });
		v7.setAdjacencyList(new Edge[] { new Edge(v6, 1), new Edge(v8, 1), new Edge(v8, 1.1) });
		v8.setAdjacencyList(new Edge[] { new Edge(v0, 1.1), new Edge(v1, 1.1),
				new Edge(v2, 1.1), new Edge(v3, 1.1), new Edge(v4, 1.1),
				new Edge(v5, 1.1), new Edge(v6, 1.1), new Edge(v7, 1.1), });
	}
	
	/**
	 * This class method calls a method to compute the shortest paths from the source vertex to any other vertex
	 * and then calls a method to get the shortest path from the source to the target vertex.<br>
	 * This path is represented by the returned <Code>String</Code> array.
	 * @param source the source vertex where NAO wants to move from
	 * @param target the target vertex where NAO wants to move to
	 * @return an <Code>String</Code> array containing the vertex's color that are on the way of the route to walk
	 * @throws InvalidInputException if the source or target vertex does not exist
	 * @see Vertex
	 * @see #computePaths(Vertex)
	 * @see #getShortestPath(Vertex)
	 * @see InvalidInputException
	 */
	public static String[] getPath(String source, String target) throws InvalidInputException
	{
		switch(source)
		{
			case "pink":
				computePaths(v0);
				break;
			case "orange":
				computePaths(v1);
				break;
			case "blue":
				computePaths(v2);
				break;
			case "purple":
				computePaths(v3);
				break;
			case "red":
				computePaths(v4);
				break;
			case "khaki":
				computePaths(v5);
				break;
			case "grey":
				computePaths(v6);
				break;
			case "green":
				computePaths(v7);
				break;
			case "beige": 
				computePaths(v8);
				break;
			default: 
				throw new InvalidInputException("Invalid source vertex color: Vertex " + source + " does not exist.");
		}
	
		List<Vertex> shortestPath;
		
		switch(target)
		{
			case "pink":
//				System.out.println("Distance from vertex " + source + " to vertex " + target + ": " + v0.getDistance());
				shortestPath = getShortestPath(v0);
				System.out.println("Path: " + shortestPath);
				break;
			case "orange": 
//				System.out.println("Distance from vertex " + source + " to vertex " + target + ": " + v1.getDistance());
				shortestPath = getShortestPath(v1);
				System.out.println("Path: " + shortestPath);
				break;
			case "blue":
//				System.out.println("Distance from vertex " + source + " to vertex " + target + ": " + v2.getDistance());
				shortestPath = getShortestPath(v2);
				System.out.println("Path: " + shortestPath);
				break;
			case "purple":
//				System.out.println("Distance from vertex " + source + " to vertex " + target + ": " + v3.getDistance());
				shortestPath = getShortestPath(v3);
				System.out.println("Path: " + shortestPath);
				break;
			case "red":
//				System.out.println("Distance from vertex " + source + " to vertex " + target + ": " + v4.getDistance());
				shortestPath = getShortestPath(v4);
				System.out.println("Path: " + shortestPath);
				break;
			case "khaki":
//				System.out.println("Distance from vertex " + source + " to vertex " + target + ": " + v5.getDistance());
				shortestPath = getShortestPath(v5);
				System.out.println("Path: " + shortestPath);
				break;
			case "grey":
//				System.out.println("Distance from vertex " + source + " to vertex " + target + ": " + v6.getDistance());
				shortestPath = getShortestPath(v6);
				System.out.println("Path: " + shortestPath);
				break;
			case "green":
//				System.out.println("Distance from vertex " + source + " to vertex " + target + ": " + v7.getDistance());
				shortestPath = getShortestPath(v7);
				System.out.println("Path: " + shortestPath);
				break;
			case "beige":  
//				System.out.println("Distance from vertex " + source + " to vertex " + target + ": " + v8.getDistance());
				shortestPath = getShortestPath(v8);
				System.out.println("Path: " + shortestPath);
				break;
			default: 
				shortestPath = null;
				throw new InvalidInputException("Invalid target vertex color: Vertex " + target + " does not exist.");
		}
		
		String[] path = new String[shortestPath.size()];
		for (int i = 0; i < path.length; i++) 
		{
			path[i] = shortestPath.get(i).getColor();
		}
		dijkstraReset();
		return path;
	}
	
	private static void dijkstraReset()
	{
		Vertex[] vertices = {v0,v1,v2,v3,v4,v5,v6,v7,v8};
		for (Vertex v : vertices) 
		{
			v.setDistance(Double.POSITIVE_INFINITY);
			v.setPredecessor(null);
		}
	}
	
	/**
	 * This class method calls a method to compute the shortest paths from the source vertex to any other vertex
	 * and then returns a <Code>String</Code> representing the color of the vertex which is reachable in the shortest time
	 * @param currentPosition a <Code>String</Code> representing the color of the circle NAO stands on
	 * @param target a <Code>String</Code> array representing the colors of the target vertices
	 * @return the color of the vertex which is reachable in a shorter time from NAO's source
	 * @throws InvalidInputException if the source or target vertex does not exist
	 * @see Vertex
	 * @see InvalidInputException
	 */
	public static String getShorterPathColor(String currentPosition, String[] target) throws InvalidInputException
	{
		switch(currentPosition)
		{
			case "pink":
				computePaths(v0);
				break;
			case "orange":
				computePaths(v1);
				break;
			case "blue":
				computePaths(v2);
				break;
			case "purple":
				computePaths(v3);
				break;
			case "red":
				computePaths(v4);
				break;
			case "khaki":
				computePaths(v5);
				break;
			case "grey":
				computePaths(v6);
				break;
			case "green":
				computePaths(v7);
				break;
			case "beige": 
				computePaths(v8);
				break;
			default: 
				throw new InvalidInputException("Invalid vertex color: Vertex " + target[0] + " does not exist.");
		}
		
		double min = Double.POSITIVE_INFINITY;
		String color = null;
		
		for(int i = 0; i < target.length; i++) 
		{
			switch(target[i])
			{
				case "pink": 
					if(min > v0.getDistance())
					{
						min = v0.getDistance();
						color = target[i];
					}
					break;
				case "orange": 
					if(min > v1.getDistance())
					{
						min = v1.getDistance();
						color = target[i];
					}
					break;
				case "blue": 
					if(min > v2.getDistance())
					{
						min = v2.getDistance();
						color = target[i];
					}
					break;
				case "purple": 
					if(min > v3.getDistance())
					{
						min = v3.getDistance();
						color = target[i];
					}
					break;
				case "red": 
					if(min > v4.getDistance())
					{
						min = v4.getDistance();
						color = target[i];
					}
					break;
				case "khaki": 
					if(min > v5.getDistance())
					{
						min = v5.getDistance();
						color = target[i];
					}
					break;
				case "grey": 
					if(min > v6.getDistance())
					{
						min = v6.getDistance();
						color = target[i];
					}
					break;
				case "green": 
					if(min > v7.getDistance())
					{
						min = v7.getDistance();
						color = target[i];
					}
					break;
				case "beige": 
					if(min > v8.getDistance())
					{
						min = v8.getDistance();
						color = target[i];
					}
					break;
				default:
					throw new InvalidInputException("Invalid vertex color: Vertex " + target + " does not exist.");
			}
		}
		dijkstraReset();
		return color;
	}
}

/**
 * This class represents the implementation of a vertex in a graph.
 * <p>
 * In this case a vertex is representative for one of the colored
 * points in the game world.
 * 
 * @author Nick Weber
 * @version 2.0
 */
class Vertex implements Comparable<Vertex> 
{
	/**
	 * The color of the vertex in the game world.
	 */
	private final String color;
	/**
	 * The adjacency list of a vertex contains all edges of the vertex
	 */
	private Edge[] adj;
	/**
	 * The predecessor of the vertex
	 */
	private Vertex pred;
	/**
	 * The distance from a specific vertex to another.<br>
	 */
	private double distance = Double.POSITIVE_INFINITY;

	Vertex(String vertexColor) 
	{
		this.color = vertexColor;
	}
	
	/**
	 * @return the color of the vertex
	 */
	String getColor()
	{
		return this.color;
	}
	
	/**
	 * @return the adjacency list of the vertex
	 * @see Edge
	 */
	Edge[] getAdjacencyList()
	{
		return this.adj;
	}
	
	/**
	 * @return the predecessor of the vertex
	 */
	Vertex getPredecessor()
	{
		return this.pred;
	}
	
	/**
	 * @return the distance to the vertex from another specific vertex
	 */
	double getDistance()
	{
		return this.distance;
	}
	
	/**
	 * @param a the adjacency list to set
	 * @see Edge
	 */
	void setAdjacencyList(Edge[] a)
	{
		this.adj = a;
	}
	
	/**
	 * @param v the predecessor to set
	 */
	void setPredecessor(Vertex v)
	{
		this.pred = v;
	}
	
	/**
	 * @param d the distance to set
	 */
	void setDistance(double d)
	{
		this.distance = d;
	}
	
	/**
	 * Compares this <Code>Vertex</Code> with the specified <Code>Vertex</Code> for distance.<br>
	 * Returns a negative integer, zero, or a positive integer as this <Code>Vertex</Code> distance is less than, 
	 * equal to, or greater than the specified <Code>Vertex</Code> distance.
	 * @param other the vertex to compare with
	 * @return the value 0 if the distance is numerically equal to other; 
	 * a value less than 0 if the distance is numerically less than other; 
	 * a value greater than 0 if the distance is numerically greater than other.
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Vertex other) 
	{
		return Double.compare(distance, other.distance);
	}
	
	/**
	 * @return a String representation of the vertex
	 */
	public String toString()
	{
		return "Vertex " + this.color;
	}
}

/**
 * This class represents the implementation of an edge between two vertices in a graph.
 * <p>
 * In this case an edge is representative for the route between two of the colored
 * points in the game world.<br>
 * NAO should walk among those routes in order to reach
 * specific points on the play board.
 * 
 * @author Nick Weber
 * @version 2.0
 * @see Vertex
 */
class Edge 
{
	/**
	 * The target <Code>Vertex</Code> of the <Code>Edge</Code>
	 */
	private final Vertex target;
	/**
	 * The weight of the <Code>Edge</Code>
	 */
	private final double weight;

	Edge(Vertex targetVertex, double edgeWeight) 
	{
		this.target = targetVertex;
		this.weight = edgeWeight;
	}
	
	/**
	 * @return the target of the <Code>Edge</Code>
	 */
	Vertex getTarget()
	{
		return this.target;
	}
	
	/**
	 * 
	 * @return the weight of the <Code>Edge</Code>
	 */
	double getWeight()
	{
		return this.weight;
	}
}