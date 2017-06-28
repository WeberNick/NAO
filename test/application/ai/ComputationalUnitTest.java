package application.ai;

import java.util.ArrayList;
import org.junit.Test;

import domain.represent.Playboard;
import domain.represent.PlayboardFake;
import foundation.data.Move;
import foundation.exception.*;

/**
 * <p>Tests the {@link ComputationalUnit ComputationalUnit}.</p>
 * @author Julian Betz
 * @version 1.00
 */
public class ComputationalUnitTest {
	@Test
	public void testNextMoveFake() {
		ComputationalUnit ai = new ComputationalUnit();
		try {
			ai.nextMove(new PlayboardFake(), true);
		}
		catch (Exception exc) {
			throw new AssertionError(exc.getMessage());
		}
	}
	
	@Test
	public void testNextMoveReal() {
		ComputationalUnit ai = new ComputationalUnit();
		Playboard board = new Playboard();
		board.testSetter("empty");
		for (int i = 0; i < 50; i++) {
			System.out.println(2 * i + ": Beginning of a white move in ComputationalUnitTest" + System.lineSeparator() + board + "The tracking arrays look like");
			board.fieldPrinter(true);
			board.fieldPrinter(false);
			try {
				board.updateByAI(ai.nextMove(board, true), false);
			}
			catch (GameWonException exc) {
				board.updateByAI(exc.getMove(), false);
				System.out.println("White wins the game");
				return;
			}
			catch (GameDrawException exc) {
				System.out.println("The game ended in a draw");
				return;
			}
			catch (GameLostException exc) {
				System.out.println("White loses");
				return;
			}
			System.out.println(2 * i + 1 + ": Beginning of a black move in ComputationalUnitTest" + System.lineSeparator() + board + "The tracking arrays look like");
			board.fieldPrinter(true);
			board.fieldPrinter(false);
			try {
				board.updateByAI(ai.nextMove(board, false), false);
			}
			catch (GameWonException exc) {
				board.updateByAI(exc.getMove(), false);
				System.out.println("Black wins the game");
				return;
			}
			catch (GameDrawException exc) {
				System.out.println("The game ended in a draw");
				return;
			}
			catch (GameLostException exc) {
				System.out.println("Black loses");
				return;
			}
		}
		System.out.println("End of ComputationalUnitTest" + System.lineSeparator() + board + "The tracking arrays look like");
		board.fieldPrinter(true);
		board.fieldPrinter(false);
	}
	
	@Test
	public void testNextMoveRandomized() {
		ComputationalUnit ai = new ComputationalUnit();
		Playboard board = new Playboard();
		board.testSetter("empty");
		for (int i = 0; i < 50; i++) {
			System.out.println(2 * i + ": Beginning of a white move in ComputationalUnitTest" + System.lineSeparator() + board + "The tracking arrays look like");
			board.fieldPrinter(true);
			board.fieldPrinter(false);
			try {
				board.updateByAI(ai.nextMove(board, true), false);
			}
			catch (GameWonException exc) {
				board.updateByAI(exc.getMove(), false);
				System.out.println("White wins the game");
				return;
			}
			catch (GameDrawException exc) {
				System.out.println("The game ended in a draw");
				return;
			}
			catch (GameLostException exc) {
				System.out.println("White loses");
				return;
			}
			System.out.println(2 * i + 1 + ": Beginning of a black move in ComputationalUnitTest" + System.lineSeparator() + board + "The tracking arrays look like");
			board.fieldPrinter(true);
			board.fieldPrinter(false);
			try {
				ArrayList<Move> moves = board.possibleMoves(false);
				if (moves.size() == 0)
					throw new GameDrawException("The game ended in a draw");
				Move move = moves.get((int) (moves.size() * Math.random()));
				board.updateByAI(move, false);
			}
			catch (GameDrawException exc) {
				System.out.println("The game ended in a draw");
				return;
			}
			catch (GameLostException exc) {
				System.out.println("Black loses");
				return;
			}
		}
		System.out.println("End of ComputationalUnitTest" + System.lineSeparator() + board + "The tracking arrays look like");
		board.fieldPrinter(true);
		board.fieldPrinter(false);
	}
}
