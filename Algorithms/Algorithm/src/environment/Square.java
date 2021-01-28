package environment;

public class Square {
	
	public String symbol = " ";	
	public boolean acc = true;
	
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
