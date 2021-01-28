package environment;

public class Square {
	
	String symbol = " ";	
	boolean acc = true;
	
	public Square(String symbol){			
		this.symbol = symbol;					
	}
	
	public String get(){		
		return this.symbol;	
	}
	
	public void set(String symbol){		
		this.symbol = symbol;		
		return;
	}

}
