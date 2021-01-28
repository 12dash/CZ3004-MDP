package environment;

import environment.Square;

public class Arena {	

	public Square arr[][];
	
	public int n = 0;
	public int m = 0;

	public Arena(int n, int m){		
		this.n = n;
		this.m = m;

		return;				
	}

	public void initialize_maze(String layout[][]) {	

		this.arr = new Square[this.n][this.m];

		for (int i = 0;i<this.n;i++) {
			for (int j = 0;j<this.m;j++) {				
				arr[i][j] = new Square(layout[i][j]);				
			}
		}		
	}

	public boolean check_padding_validity(int i, int j) {
		if ((i >= this.n) || (i<0)){
			return false;
		}
		else if ((j>=this.m)||(j<0)) {
			return false;
		}
		else if (this.arr[i][j].symbol.equals("X")) {
			return false;
		}
		return true;

	}

	public void add_padding(){		
		for(int i=0;i<this.n;i++) {
			if (i == 0) {
				for (int j = 0;j<this.m;j++)
				{
					this.arr[i][j].acc = false;
				}
			}
			else if(i == this.n - 1) {
				for (int j = 0;j<this.m;j++)
				{
					this.arr[i][j].acc = false;
				}
			}
			else {
				this.arr[i][0].acc = false;
				this.arr[i][this.m-1].acc = false;
			}
		}

		for(int i = 0;i<this.n;i++) {
			for(int j = 0;j<this.m;j++)
			{
				if(this.arr[i][j].symbol.equals("X")) {
					this.arr[i][j].acc = false;
					pad_around_obj(i,j);				
				}
			}
		}		
	}

	private void mark_p(int i, int j) {
		boolean valid = this.check_padding_validity(i,j);
		if (valid){
			this.arr[i][j].acc = false;
		}
	}

	private void pad_around_obj(int i, int j) {
		int a1=i-1; 
		int a2=i+1;
		int b1=j-1;
		int b2=j+1;
		mark_p(a1,b1);
		mark_p(a1,b2);
		mark_p(a1,j);
		mark_p(a2,b1);
		mark_p(a2,b2);
		mark_p(a2,j);
		mark_p(i,b1);
		mark_p(i,b2);
	}

	public void get_reperesentation() {
		for (int i =0;i<this.n;i++) {
			for(int j = 0;j<this.m;j++) {
				System.out.print(this.arr[i][j].symbol + "|");
			}
			System.out.println("\t\t"+i);
		}
	}
	
	public void get_acc_reperesentation() {
		for (int i =0;i<this.n;i++) {
			for(int j = 0;j<this.m;j++) {
				if (this.arr[i][j].acc) {
					System.out.print(" "+"|");
					continue;
				}
				System.out.print("X"+ "|");
			}
			System.out.println("\t\t"+i);
		}
	}

}


