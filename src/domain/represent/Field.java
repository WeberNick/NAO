package domain.represent;
/**
 * this class shall is a superclass for all kinds of fields. It implements method to be
 * overwritten only by certain fields.
 * @author Jonas
 *
 */
abstract public class Field implements Comparable<Field> {
	protected Field[] neighbours;
	/**
	 * One a normal field, there can be no man (null), a black man (false) or a white man (true)
	 */
	private Boolean man; 
	/**
	 * every field has a index corresponding to its position in the array 'fields' in Playboard
	 */
	private final int index;

	public Field(int i){
		this.index=i;
	}
	
	public int getIndex(){
		return index;
	}
	
	Boolean getMan(){
		return man;
	}
	void setMan(Boolean man){
		this.man=man;
	}
/**
 * sets the neighbours if there are 4 of them 
 * @param f0 a neighbour
 * @param f1 a neighbour
 * @param f3 a neighbour
 * @param f4 a neighbour
 */
	abstract void setNeighbours(Field f0, Field f1, Field f3, Field f4);
	/**
	 * sets the nighbours if there are 3 of them 
	 * @param f0 a neighbour
	 * @param f1 a neighbour
	 * @param f3 a neighbour
	 */
	abstract void setNeighbours(Field f0, Field f1, Field f3);
	/**
	 * sets the neighbous, if there are 2 of them
	 * @param f0 a neighbour
	 * @param f1 a neighbour
	 */
	abstract void setNeighbours(Field f0, Field f1);
	
	/**
	 *the fields are compared via index
	 * @param f another field
	 * @return the difference in the index
	 */
	public int compareTo(Field f){
		return index-f.getIndex();
	}

	/**
	 * if field is occupied, return false, else true.
	 * @return boolean true if there is no man, false if there is one
	 */
	protected boolean isEmpty(){
		if (man==null)
			return true;
		else
			return false;
	}
	/**
	 * if the field would be occupied by a man of the given colour, would it be part of a Mill?
	 * @param colour the colour a man has, which is assumend on the field
	 * @return false of not, true if it is the case
	 */
	protected boolean partOfMill(boolean colour){
		if (man==null)
			return false;
		return partOfMill(colour,-1);
	}
	
	//abstract int[] returnMill(boolean colour, int index);
	/**
	 * if the field would be occupied by a man of the given colour, and the field of the given 
	 * index would be empty, would it be part of a Mill?
	 * @param colour the colour a man has, which is assumed on the field
	 * @param index index where no man is assumed
	 * @return false if not, true if it is the case
	 */
	abstract boolean partOfMill(boolean colour, int index);
	
	
	/**
	 * checks if the current field is part of a mill
	 * @return true or false
	 */
	protected boolean partOfMill(){
		if (man==null)
			return false;
		return partOfMill(man.booleanValue());
	}
	
	/**
	 * returns if the man on this field has the given colour.
	 * this method is necessary to avoid NullPointerExceptions
	 * @param colour the colour a man shall have
	 * @return a boolean as answer
	 */
	protected boolean manEqualsColour(boolean colour){
		if (this.getMan()==null)
			return false;
		else{
			if(man.booleanValue()==colour)
				return true;
			else
				return false;
		}
	}
	
	/**
	 * this method returns how many man are stored the field
	 * it only has a use, if the field is pile.
	 * @return how many men are stored in this field
	 */
	protected int getManCount(){
		if (man==null)
			return 0;
		else
			return 1;
	}
	
	public String toString(){
		return ("Field "+index+" , man: "+man);
	}
	
}
