package application.ai;

import domain.represent.Playboard;
import foundation.data.Move;
import foundation.data.MultiPrioritySet;
import foundation.exception.*;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * <p>The class charged with the choice of the next move.</p>
 * <p>Tries to find an optimal action in the given situation of the game up to a certain limit of accuracy (i.e. a number of
 * consecutive moves of both players). In order to reduce computation time heuristics are used when this limit is reached.</p>
 * @author Julian Betz
 * @version 1.03
 */
public class ComputationalUnit {
	private int maxDepth;
	private final ArrayList<Comparator<Move>> comparators;
	
	/**
	 * <p>Creates a new {@code ComputationalUnit} ready to compute future moves.</p>
	 * <p>Sets the depth of the search to {@code 4}.</p>
	 */
	public ComputationalUnit() {
		maxDepth = 4;
		comparators = new ArrayList<Comparator<Move>>(2);
		comparators.add((Move a, Move b) -> a.compareTo(b));
		comparators.add((Move a, Move b) -> b.getEstimation() - a.getEstimation());
	}
	
	public synchronized void setMaxDepth(int depth) {
		maxDepth = depth;
	}
	
	public synchronized int getMaxDepth() {
		return maxDepth;
	}
	
	/**
	 * <p>Computes an optimal move (up to the limit of accuracy defined by {@code maxDepth}) for the current game situation
	 * presented by the specified playboard. After reaching the limit, evaluation heuristics are used.</p>
	 * <p>The maximal search depth is set to {@code max(2, depth)}.</p>
	 * <p>Time complexity is highly reduced due to the method's implementation as a NegaScout algorithm.
	 * At the same time, space complexity is minimized by removing references to irrelevant nodes.</p>
	 * @param board the playboard presenting the current game situation
	 * @param color the color of the men owned
	 * @return an optimal move for the current game situation
	 * @throws GameWonException if no moves can be carried out after the next move and the game will not end in a draw 
	 * @throws GameDrawException if the game ends in a draw
	 * @throws GameLostException if no moves can be carried out and the game does not end in a draw
	 */
	public synchronized Move nextMove(Playboard board, boolean color) throws GameWonException, GameDrawException, GameLostException {
		maxDepth = maxDepth < 2 ? 2 : maxDepth;
		board = board.deepCopy();
		SearchTree tree = new SearchTree();
		SearchTree.Node node = tree.getRoot();
		MultiPrioritySet<Move> priority = new MultiPrioritySet<Move>(comparators);
		//Initialize the search by identifying all moves directly executable in the current situation, throw GameLostException if the game was lost
		node.addAll(board.possibleMoves(new MultiPrioritySet<Move>(comparators), new MultiPrioritySet<Move>(comparators), color), 1);
		if (!node.hasChild())
			throw new GameDrawException("No move can be carried out");
		//Launch the search
		SearchTree.Node child = node.pop();
		board.updateByAI(child.getMove(), false);
		try {
			child.addAll(board.possibleMoves(priority, new MultiPrioritySet<Move>(comparators), !color), 1);
		}
		catch (GameLostException exc) {
			throw new GameWonException("The game will be won after the next move", child.getMove());
		}
		Move nextMove = child.getMove();
		int beta = Integer.MAX_VALUE;
		int alpha = -negaScout(board, child, -beta, Integer.MAX_VALUE, color, !color, priority);
		board.updateByAI(child.getMove(), true);
		while (node.hasChild()) {
			child = node.pop();
			board.updateByAI(child.getMove(), false);
			try {
				child.addAll(board.possibleMoves(priority, new MultiPrioritySet<Move>(comparators), !color), 1);
			}
			catch (GameLostException exc) {
				throw new GameWonException("The game will be won after the next move", child.getMove());
			}
			int estimation = -negaScout(board, child, -alpha - 1, -alpha, color, !color, priority);
			if (alpha < estimation && estimation < beta)
				estimation = -negaScout(board, child, -beta, -estimation, color, !color, priority);
			if (alpha < estimation) {
				alpha = estimation;
				nextMove = child.getMove();
			}
			board.updateByAI(child.getMove(), true);
		}
		return nextMove;
	}
	
	/**
	 * <p>A NegaScout algorithm based on the one proposed by Alexander Reinefeld (1989). It identifies the most advantageous path in the search tree by
	 * recursively computing the estimation of the specified node's subtree's value.</p>
	 * @param board the playboard presenting the simulated game situation
	 * @param node the node in the search tree corresponding the simulated game situation
	 * @param alpha the lower search bound
	 * @param beta the upper search bound
	 * @param color the color of the initial player's men
	 * @param currentPlayer the color of the current player's men
	 * @param comparables the set of moves already tested while computing the estimation for sibling nodes (used for implementation of the killer
	 * heuristic)
	 * @return the estimation of the node's subtree's advantageousness
	 */
	private int negaScout(Playboard board, SearchTree.Node node, int alpha, int beta, boolean color, boolean currentPlayer, MultiPrioritySet<Move> comparables) {
		if (!node.hasChild()) {
			if (board.allMenOnBoard(true, false) == 3 && board.allMenOnBoard(false, false) == 3)
				return 0; //Handle a draw situation
			return currentPlayer ^ color ? -evaluate(board, color) : evaluate(board, color);
		}
		MultiPrioritySet<Move> priority = new MultiPrioritySet<Move>(comparators);
		SearchTree.Node child = node.pop();
		board.updateByAI(child.getMove(), false);
		boolean branch = child.getDepth() < maxDepth;
		if (branch) {
			try {
				child.addAll(board.possibleMoves(priority, new MultiPrioritySet<Move>(comparators), !currentPlayer), 1);
				int estimation = -negaScout(board, child, -beta, -alpha, color, !currentPlayer, priority);
				Move move = comparables.integrate(child.getMove());
				comparables.remove(move);
				move.raiseEstimation(estimation); //Killer-heuristic update
				comparables.add(move);
				alpha = alpha > estimation ? alpha : estimation;
			}
			catch (GameLostException exc) {
				alpha = Integer.MAX_VALUE;
			}
		}
		if (alpha >= beta)
			node.clear(); //Pruning
		board.updateByAI(child.getMove(), true);
		while (node.hasChild()) {
			child = node.pop();
			board.updateByAI(child.getMove(), false);
			if (branch) {
				try {
					child.addAll(board.possibleMoves(priority, new MultiPrioritySet<Move>(comparators), !currentPlayer), 1);
				}
				catch (GameLostException exc) {
					alpha = Integer.MAX_VALUE;
				}
			}
			int estimation = -negaScout(board, child, -alpha - 1, -alpha, color, !currentPlayer, priority);
			if (alpha < estimation && estimation < beta)
				estimation = -negaScout(board, child, -beta, -estimation, color, !currentPlayer, priority);
			Move move = comparables.integrate(child.getMove());
			comparables.remove(move);
			move.raiseEstimation(estimation); //Killer-heuristic update
			comparables.add(move);
			alpha = alpha > estimation ? alpha : estimation;
			if (alpha >= beta)
				node.clear(); //Pruning
			board.updateByAI(child.getMove(), true);
		}
		return alpha;
	}
	
	/**
	 * <p>Evaluates the game situation presented by the playboard using heuristics.</p>
	 * @param board the playboard the information about the game situation is stored in
	 * @param color the color of the men owned
	 * @return an estimation {@code e} of the advantageousness of the game situation so that {@code Integer.MIN_VALUE < e <
	 * Integer.MAX_VALUE}
	 */
	private int evaluate(Playboard board, boolean color) {
		return board.allMenOnBoard(color, false) - board.allMenOnBoard(!color, false) + 3 * (board.numberOfMills(color)
				- board.numberOfMills(!color));
	}
}
