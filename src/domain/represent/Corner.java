package domain.represent;
/**
 * This class represents an corner an a board. It therefore has two neighbours.
 * @author Jonas
 *
 */
class Corner extends Field{
protected Corner(Field f0, Field f1, int i){
	this(i);
	super.neighbours[0] = f0;
	super.neighbours[1] = f1;
	
}
protected Corner (int i){
	super(i);
	neighbours = new Field[2];
}
/**
*{@inheritDoc}
*/
protected void setNeighbours(Field f0, Field f1, Field f2){
	System.err.println("Wrong method invoked");
}
/**
*{@inheritDoc}
*/
protected void setNeighbours(Field f0, Field f1, Field f2, Field f3){
	System.err.println("Wrong method invoked");
}
/**
*{@inheritDoc}
*/	
protected void setNeighbours(Field f0, Field f1){
	neighbours[0] = f0;
	neighbours[1] = f1;
}
/**
*{@inheritDoc}
*/
protected boolean partOfMill(boolean colour, int index){


		for (int i=0;i<neighbours.length;i++){
			if (neighbours[i].getIndex()==index){
				continue;
			}
			if (neighbours[i].manEqualsColour(colour) && neighbours[i].neighbours[i].manEqualsColour(colour)){
				return true;
			}
		}

	return false;
}
}
