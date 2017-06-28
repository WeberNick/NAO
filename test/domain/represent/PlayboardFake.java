package domain.represent;

import java.util.ArrayList;

import foundation.data.*;
import foundation.exception.GameLostException;

/**
 * <p>Fake class for the {@code Playboard} class. This class is needed for tests in combination with a {@code ComputationalUnit}.</p>
 * @author Julian Betz
 * @version 1.00
 */
public class PlayboardFake extends Playboard {
	private int state;
	private int[] men = {-1, -1, -1, -1, -1, -1, -1, 2, 5, 9, 3, 1, 4, 7, 8};
	
	public PlayboardFake() {
		state = 0;
	}
	
	@Override
	public void updateByAI(Move move, boolean reverse) {
		if (!(move instanceof MoveFake) || state != (reverse ? ((MoveFake) move).getToState() : ((MoveFake) move).getFromState()))
			throw new IllegalArgumentException("Test failed due to wrong use of update_AI(Move, boolean)");
		state = reverse ? ((MoveFake) move).getFromState() : ((MoveFake) move).getToState();
	}
	
	@Override
	public MultiPrioritySet<Move> possibleMoves(MultiPrioritySet<Move> input, MultiPrioritySet<Move> output, boolean color) {
		MoveFake left = new MoveFake(state, 2 * state + 1);
		MoveFake rght = new MoveFake(state, 2 * state + 2);
		left.raiseEstimation(1);
		if (left.getToState() < men.length) {
			output.add(left);
			if (rght.getToState() < men.length)
				output.add(rght);
		}
		return output;
	}

	@Override
	public Field getField(int index) {
		throw new UnsupportedOperationException("Test failed due to illegal use of getField(int)");
	}

	@Override
	public Boolean[] updateGUI() {
		throw new UnsupportedOperationException("Test failed due to illegal use of update_GUI()");
	}

	@Override
	public ArrayList<Move> possibleMoves(boolean colour) throws GameLostException {
		throw new UnsupportedOperationException("Test failed due to illegal use of possibleMoves(boolean)");
	}

	@Override
	public int numberOfMills(boolean colour) {
		return 0;
	}

	@Override
	public int allMenOnBoard(boolean colour, boolean count) {
		if (men[state] < 0)
			throw new IllegalStateException("Test failed due to illegal state of the playboard");
		if (colour)
			return men[state];
		return 0;
	}

	@Override
	public Playboard deepCopy() {
		return new PlayboardFake();
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException("Test failed due to illegal use of toString()");
	}

	@Override
	public void testSetter(String kase) {
		throw new UnsupportedOperationException("Test failed due to illegal use of testSetter(String)");
	}
}
