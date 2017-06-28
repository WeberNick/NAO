package domain.represent;
/**
 * this field has three neighbours. it is located in the inner ring.
 * @author Jonas
 *
 */
class TJunctionTop extends Field{
/*	protected TJunctionTop(Field f0, Field f1, Field f2){
		super.neighbours = new Field[3];
		super.neighbours[0] = f0;
		super.neighbours[1] = f1;
		super.neighbours[2] = f2;
	}*/
	protected TJunctionTop(int i){
		super (i);
		neighbours = new Field[3];
	}
	/**
	 * {@inheritDoc}
	 */
	protected void setNeighbours(Field f0, Field f1){
		System.out.println("Wrong method invoked");
	}
	/**
	 * {@inheritDoc}
	 */
	protected void setNeighbours(Field f0, Field f1, Field f2, Field f3){
		System.out.println("Wrong method invoked");
	}
	/**
	 * {@inheritDoc}
	 */
	protected void setNeighbours(Field f0, Field f1, Field f2){
		neighbours[0] = f0;
		neighbours[1] = f1;
		neighbours[2] = f2;
	}
	/**
	 * {@inheritDoc}
	 */
	protected boolean partOfMill(boolean colour, int index){

	
			if (neighbours[0].getIndex()!=index && neighbours[0].manEqualsColour(colour) && neighbours[1].getIndex()!=index && neighbours[1].manEqualsColour(colour)){
				return true;
			}
			if (neighbours[2].getIndex()!=index && neighbours[2].manEqualsColour(colour) && neighbours[2].neighbours[3].manEqualsColour(colour)){
				return true;
			}
		
		return false;
	}
}
