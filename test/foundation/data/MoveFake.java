package foundation.data;

import domain.represent.Field;

/**
 * <p>Fake class for the {@code Move} class. This class is needed for tests in combination with a {@code ComputationalUnit}.</p>
 * @author Julian Betz
 * @version 1.00
 */
public class MoveFake extends Move {
	private int fromState;
	private int toState;
	
	public MoveFake(int fromState, int toState) {
		super(false, false, null, true, false, null);
		this.fromState = fromState;
		this.toState = toState;
	}
	
	public int getFromState() {
		return fromState;
	}
	
	public int getToState() {
		return toState;
	}
	
	@Override
	public boolean getAction(int c) {
		throw new UnsupportedOperationException("Test failed due to illegal use of getAction(int)");
	}

	@Override
	public boolean getColor(int c) {
		throw new UnsupportedOperationException("Test failed due to illegal use of getColor(int)");
	}

	@Override
	public Field getField(int c) {
		throw new UnsupportedOperationException("Test failed due to illegal use of getField(int)");
	}

	@Override
	public int getEstimation() {
		return super.getEstimation();
	}

	@Override
	public void raiseEstimation(int cutoff) {
		super.raiseEstimation(cutoff);
	}

	@Override
	public int length() {
		throw new UnsupportedOperationException("Test failed due to illegal use of length()");
	}

	@Override
	public Move reverse() {
		return new MoveFake(toState, fromState);
	}

	@Override
	public int compareTo(Move move) {
		if (!(move instanceof MoveFake))
			throw new IllegalArgumentException("Test failed due to wrong use of compareTo(Move)");
		if (Integer.compare(fromState, ((MoveFake) move).fromState) == 0)
			return Integer.compare(toState, ((MoveFake) move).toState);
		return Integer.compare(fromState, ((MoveFake) move).fromState);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MoveFake))
			throw new IllegalArgumentException("Test failed due to wrong use of equals(Object)");
		return compareTo((MoveFake) o) == 0;
	}

	@Override
	public String toString() {
		return "<" + fromState + ", " + toState + ">";
	}
}
