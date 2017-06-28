package domain.represent;
/**
 * a field containing three neighbours. the tops are on the outer ring.
 * @author Jonas
 *
 */
class TJunctionBottom extends Field{
	protected TJunctionBottom(Field f0, Field f1, Field f2, int i){
		this(i);
		super.neighbours[0] = f0;
		super.neighbours[1] = f1;
		super.neighbours[2] = f2;
	}
	protected TJunctionBottom(int i){
		super(i);
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
			if (neighbours[2].getIndex()!=index && neighbours[2].manEqualsColour(colour) && neighbours[2].neighbours[2].manEqualsColour(colour)){
				return true;
			}
		
		return false;
	}
}
