package domain.represent;
/**
 * 
 * @author Jonas
 *this class is a little different: There are 4 pile in the game, per party an initial
 *and burnt. Therefore man is an array containing up to 9 men.
 *in fact, according to the changed requirements, these fields would not have been necessary,
 *but as they exist and are to hard to be excluded again, they remain within the software and cause
 *a lot of extra cases to handle.
 */
class Pile extends Field{
	/**
	 * the attribute of the field class does not exist. It is overwritten by an array of Booleans.
	 */
private Boolean[] man;
protected Pile(int i){
	super(i);
	man = new Boolean[9];
}
/**
 * the man has to be set into this pile
 * if man is null, the last one should be deleted
 * if man is true/false, the last free position shall be used to save the man.
 */
@Override
void setMan(Boolean bMan) throws ArrayIndexOutOfBoundsException{
	if (bMan==null){
		//pop()
		if(man[man.length-1]!=null){
			man[man.length-1]=null;
			return;
		}
		for (int i=0;i<man.length;i++){
			if(man[i]==null){
				try{
					man[i-1]=null;
					return;
				}
				catch(ArrayIndexOutOfBoundsException e){
					//pile was empty
				}
			}
		}
		System.err.println("You should never see this message sent by your facourite pile!");
	}
	else{
		//insert()
		for(int i=0;i<man.length;i++){
			if(man[i]==null){
				man[i]=bMan;
				return;
			}
		}
		System.out.println("Stack Overflow");
		throw new ArrayIndexOutOfBoundsException("This pile was full");
	}
}

protected void setMan(Boolean man, int position){
	this.man[position]=man;
}

protected Boolean getMan(int position){
	return new Boolean(man[position]);
}
/**
 * {@inheritDoc}
 */
protected void setNeighbours(Field f0, Field f1){
	System.out.println("I'm just a pile of stuff");
}
/**
 * {@inheritDoc}
 */
protected void setNeighbours(Field f0, Field f1, Field f2){
	System.out.println("I'm just a pile of stuff");
}
/**
 * {@inheritDoc}
 */
protected void setNeighbours(Field f0, Field f1, Field f2, Field f3){
	System.out.println("I'm just a pile of stuff");
}
/**
 * {@inheritDoc}
 */
protected boolean partOfMill(boolean colour, int ide){
	System.out.println("I'm just a pile of stuff");
	return false;
}
/**
 * {@inheritDoc}
 */
protected boolean isEmpty(){
	if (man[0]==null)
		return true;
	else
		return false;
}
/**
 * {@inheritDoc}
 */
protected int getManCount(){
	int rtn=0;
	for (int i=0;i<man.length;i++){
		if(man[i]!=null)
			rtn++;
	}
	return rtn;
}

/**
 * used by deepCopy() to clear a pile of all men.
 */
protected void clear(){
	for(int i=0;i<9;i++){
		this.man[i]=null;
	}
}

public String toString(){
	StringBuffer sb = new StringBuffer("Pile "+this.getIndex()+ " with ");
	int i = this.getManCount();
	if (i==0)
		sb.append("no man");
	else if (i==1){
		sb.append("1 "+man[0]+ " man");
	}
	else{
		sb.append(i +" "+man[0]+" men");
	}
	sb.append(" on it");
	return sb.toString();
}

}
