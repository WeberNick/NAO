package domain.represent;
/**
 * This class represents a Field with 4 neighbours
 * @author Jonas
 *
 */
class Cross extends Field {
	protected Cross(int i){
		super(i);
		super.neighbours = new Field[4];
	}
	/*protected Cross(Field f0, Field f1, Field f2, Field f3){
		super.neighbours = new Field[4];
		super.neighbours[0] = f0;
		super.neighbours[1] = f1;
		super.neighbours[2] = f2;
		super.neighbours[3] = f3;
		
	}*/
	/**
	 * {@inheritDoc}
	 */
	protected void setNeighbours(Field f0, Field f1){
		System.err.println("Wrong method invoked");
	}
	/**
	 * {@inheritDoc}
	 */
	protected void setNeighbours(Field f0, Field f1, Field f2){
		System.err.println("Wrong method invoked");
	}
	/**
	 * {@inheritDoc}
	 */
	protected void setNeighbours(Field f0, Field f1, Field f2, Field f3){
		neighbours[0] = f0;
		neighbours[1] = f1;
		neighbours[2] = f2;
		neighbours[3] = f3;
	}
	/**
	 * {@inheritDoc}
	 */
	protected boolean partOfMill(boolean colour, int index){

	
			if (neighbours[0].getIndex()!=index && neighbours[0].manEqualsColour(colour) && neighbours[1].getIndex()!=index && neighbours[1].manEqualsColour(colour)){
				return true;
			}
			if (neighbours[2].getIndex()!=index && neighbours[2].manEqualsColour(colour) && neighbours[3].getIndex()!=index && neighbours[3].manEqualsColour(colour)){
				return true;
			}
		return false;
	}
}
