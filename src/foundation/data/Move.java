package foundation.data;

import domain.represent.Field;
import foundation.exception.InvalidMoveException;

/**
 * <p>The class representing a single move of one party.</p>
 * <p>A move can consist of two or four actions which represent the robots' actions of picking up or placing down men.
 * It comprises exactly two actions if one relocation is supposed to take place during the move and exactly
 * four actions if two relocations are supposed to take place during the move (i.e. if a mill is to be constructed
 * in the course of the move).
 * In order to be processed correctly, the remove action has be executed first and a man has to be set afterwards. If the
 * object's definition does not stick to this order, the constructor will throw an Exception.</p> 
 * @author Julian Betz, Jonas Thietke
 * @version 1.02
 */
public class Move implements Comparable<Move> {
	private final boolean[] actions;
	private final boolean[] colors;
	private final Field[] fields;
	private int estimation;
	
	/**
	 * <p>Constructs a new {@code Move} object holding two actions (i.e. the relocation of one man).</p>
	 * @param act1 the first action to be executed, true to set down, false to take away
	 * @param col1 the color of the first man to be processed
	 * @param f1 the field on which the first action has to take place
	 * @param act2 the second action to be executed, true to set down, false to take away
	 * @param col2 the color of the second man to be processed
	 * @param f2 the field on which the second action has to take place
	 */
	public Move(boolean act1, boolean col1, Field f1, boolean act2, boolean col2, Field f2) throws InvalidMoveException {
		if (act1 || !act2)
			throw new InvalidMoveException("Wrong order of actions. A man has to be removed before it can be set.");
		actions = new boolean[2];
		actions[0] = act1;
		actions[1] = act2;
		colors = new boolean[2];
		colors[0] = col1;
		colors[1] = col2;
		fields = new Field[2];
		fields[0] = f1;
		fields[1] = f2;
		estimation = 0;
	}
	
	/**
	 * <p>Constructs a new {@code Move} object holding four actions (i.e. the relocation of two men).</p>
	 * @param act1 the first action to be executed, true to set down, false to take away
	 * @param col1 the color of the first man to be processed
	 * @param f1 the field on which the first action has to take place
	 * @param act2 the second action to be executed, true to set down, false to take away
	 * @param col2 the color of the second man to be processed
	 * @param f2 the field on which the second action has to take place
	 * @param act3 the third action to be executed, true to set down, false to take away
	 * @param col3 the color of the third man to be processed
	 * @param f3 the field on which the third action has to take place
	 * @param act4 the fourth action to be executed, true to set down, false to take away
	 * @param col4 the color of the fourth man to be processed
	 * @param f4 the field on which the fourth action has to take place
	 */
	public Move(boolean act1, boolean col1, Field f1, boolean act2, boolean col2, Field f2, boolean act3, boolean col3, Field f3, boolean act4, boolean col4, Field f4) throws InvalidMoveException {
		if (act1 || act3 || !act2 || !act4)
			throw new InvalidMoveException("Wrong order of actions. A man has to be removed before it can be set.");
		actions = new boolean[4];
		actions[0] = act1;
		actions[1] = act2;
		actions[2] = act3;
		actions[3] = act4;
		colors = new boolean[4];
		colors[0] = col1;
		colors[1] = col2;
		colors[2] = col3;
		colors[3] = col4;
		fields = new Field[4];
		fields[0] = f1;
		fields[1] = f2;
		fields[2] = f3;
		fields[3] = f4;
		estimation = 0;
	}
	
	/**
	 * <p>Returns a boolean indicating the kind of action to be performed.</p>
	 * @param c the action under discussion
	 * @return true if a man has to be placed down, false if it has to be picked up
	 */
	public boolean getAction(int c) {
		return actions[c];
	}
	
	/**
	 * <p>Returns a boolean indicating the color of the man to be processed.</p>
	 * @param c the action under discussion
	 * @return true if the man is white, false if it is black
	 */
	public boolean getColor(int c) {
		return colors[c];
	}
	
	/**
	 * <p>Returns a reference to the {@code Field} object from which a man has to be picked up
	 * or on which a man has to be placed.</p>
	 * @param c the action under discussion
	 * @return a reference to the proper {@code Field} object
	 */
	public Field getField(int c) {
		return fields[c];
	}
	
	/**
	 * <p>Returns the estimation value already computed before.</p>
	 * @return the estimation value
	 */
	public int getEstimation() {
		return estimation;
	}
	
	/**
	 * <p>Sets the estimation attribute to the specified parameter if its current value is lower.</p>
	 * @param estimation the estimation value to store
	 */
	public void raiseEstimation(int estimation) {
		this.estimation = Math.max(this.estimation, estimation);
	}
	
	/**
	 * This method is needed in order to know how many actions were done within this move
	 * @return the elements within the move (2 or 4)
	 */
	public int length(){
		return actions.length;
	}	
	
	/**
	 * returns the move in reverse in order to undo it
	 * @return the whole move in reverse
	 */
	public Move reverse(){
		switch (actions.length){
		case 2: return new Move(!actions[1], colors[1], fields[1], !actions[0], colors[0],fields[0]);
		case 4: return new Move(!actions[1], colors[1], fields[1], !actions[0], colors[0],fields[0], !actions[3], colors[3],fields[3], !actions[2], colors[2],fields[2]);
		default: System.out.println("Your programmer hasn't worked correctly, please contact Jonas"); //Cannot happen: actions is defined as final and its length is set to either 2 or 4 in the constructor
		return null;
		}
	}
	
	/**
	 * <p>Compares two moves in order to distinguish them by their content data.</p>
	 * <p><i>(This functionality is needed in combination with a {@code MultiPrioritySet}.)</i><p>
	 * @param move the move to compare this move to
	 * @return {@code 0} if both moves are equivalent, {@code -1} if this move is deemed to be less than the specified move and {@code 1} else
	 */
	@Override
	public int compareTo(Move move) {
		if (actions.length < move.actions.length)
			return -1;
		else if (actions.length > move.actions.length)
			return 1;
		//Both moves have the same length
		for (int i = 0; i < actions.length; i++) {
			if (actions[i] && !move.actions[i])
				return 1;
			else if (!actions[i] && move.actions[i])
				return -1;
		}
		//All actions are equal
		for (int i = 0; i < colors.length; i++) {
			if (colors[i] && !move.colors[i])
				return 1;
			else if (!colors[i] && move.colors[i])
				return -1;
		}
		//All colors are equal
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].compareTo(move.fields[i]) != 0)
				return fields[i].compareTo(move.fields[i]);
		}
		//Both moves are equal
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Move))
			return false;
		return compareTo((Move) o) == 0;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<this.length();i++){
			sb.append("Action: ");
			if (this.getAction(i))
				sb.append("Set;");
			else
				sb.append("Remove;");
			sb.append(" Colour: "+this.getColor(i)+";");
			sb.append(" Field: "+getField(i).getIndex());
			sb.append("\n");
		}
		return sb.toString();
	}
}
