/*********************************************************************************
** HARSHIT 
** 
** 
** Task - To Implement Queen Mckluskey method to find least no. of prime implicants
**	  for any Boolean function( $$$$ UPTO 7 Variables $$$$ ) 
*********************************************************************************/
import java.util.*;

class Minterms {	//class to store minterms as per the input by user
	Integer term;
	Minterms(int term){
		this.term=term;
	}
}

class ColumnTerm {	//class to store the prime Implicant
	
	Integer value,sum=0;	//for 11_0 value = 1100 sum = 10
	ArrayList<Integer> mterms = new ArrayList<Integer>();	//integer array to store the minterms associated with the prime Implicant
	ArrayList<Integer> dntCareBits =  new ArrayList<Integer>();	//to store Dont Care terms
	
	void sum(){
		for(int i=0;i<dntCareBits.size();i++)
			sum=sum+dntCareBits.get(i);
	}
	
	ColumnTerm(int value){
		this.value=value;
	}
	
	ColumnTerm(ColumnTerm p, ColumnTerm q){	//constructor to merge two prime Implicant
		for(int i=0; i<p.mterms.size(); i++)
			mterms.add(p.mterms.get(i));
		for(int i=0; i<q.mterms.size(); i++)
			mterms.add(q.mterms.get(i));
		for(int i=0; i<p.dntCareBits.size(); i++)
			dntCareBits.add(p.dntCareBits.get(i));
	}
	
	boolean equals(ColumnTerm p){	//method to check equality of two min terms(overrides equals method of the Object class)
		if(sum==p.sum && value==p.value)
			return true;
		else	return false;
	}
}

public class qm {
	
	public static ArrayList<Minterms> tms = new ArrayList<Minterms>();	//array of minterms
	public static ArrayList<Minterms> dntCare =new ArrayList<Minterms>();	//array of Dont care terms
	public static ArrayList<Minterms> tm = new ArrayList<Minterms>();	//array of minterms + dont care terms
	public static ArrayList<ColumnTerm> colterm = new ArrayList<ColumnTerm>();	//array of prime Implicants
	public static ArrayList<ColumnTerm> essentials = new ArrayList<ColumnTerm>();	//array of essential Implicants
	public static int max=0;	//max of the minterms
	public static int var=0;	//no. of variable of the boolean function maximum 7
	public static boolean table[][] = new boolean[128][128];	//final table to get Essential prime Implicants
	
	public static void maximum(){	//computes maximum
		max=0;
		for(int i=0;i<tm.size();i++)
			if(max<tm.get(i).term)
				max=tm.get(i).term;
		if(max>=0 && max<4)	var=2;
		else if(max>=4 && max<8)	var=3;
		else if(max>=8 && max<16)	var=4;
		else if(max>=16 && max<32)	var=5;
		else if(max>=32 && max<64)	var=6;
		else if(max>=64 && max<128)	var=7;
		else if(max>=128){
			System.out.println("\nProgram is valid for minterms upto 7 only\n");
			System.exit(0);
		}	
	}
	
	public static void executeColumn1(){	//Column 1 execution
		int[] count = new int[tm.size()];
		for(int i =0;i<tm.size();i++)
				count[i]=0;
		for(int i=0;i<tm.size()-1;i++)
			for(int j=i+1;j<tm.size();j++){
				int and = tm.get(i).term ^ tm.get(j).term;
				int value = tm.get(i).term & tm.get(j).term;
				for(int k=0;k<8;k++){
					if(and == Math.pow(2, k)){
						ColumnTerm p = new ColumnTerm(value);
						p.mterms.add(tm.get(i).term);
						p.mterms.add(tm.get(j).term);
						p.dntCareBits.add(and);
						p.sum();
						colterm.add(p);
						count[i]=1;
						count[j]=1;
					}
				}
			}
		for(int i=0;i<tm.size();i++)
			if(count[i]==0){
				ColumnTerm p = new ColumnTerm(tm.get(i).term);
				p.mterms.add(tm.get(i).term);
				p.dntCareBits.add(0);
				p.sum();
				if(colterm.size()>0)	colterm.add(colterm.size()-1, p);
				else	colterm.add(p);
			}
	}
	
	public static void executeColumns(){	//executing other columns after column 1 execution
		for(int k=2;k<var;k++){
			ArrayList<ColumnTerm> b = new ArrayList<ColumnTerm>();
			if(colterm.size()>1){
			for(int i=0; i<colterm.size()-1; i++){
				for(int j=i+1; j<colterm.size(); j++)
					if(colterm.get(i).mterms.size() == colterm.get(j).mterms.size())
						if(colterm.get(i).sum == colterm.get(j).sum){
							int m = colterm.get(i).value ^ colterm.get(j).value;
							int value = colterm.get(i).value & colterm.get(j).value;
							for(int l=0; l<8; l++)
								if(m == Math.pow(2, l)){
									ColumnTerm temp = new ColumnTerm(colterm.get(i), colterm.get(j));
									temp.value=value;
									temp.dntCareBits.add(m);
									temp.sum();
									b.add(temp);
									/*colterm.remove(j);
									colterm.remove(i);
									j=i;*/
								}
						}
			}
			for(int i=0; i<b.size(); i++)
					colterm.add(0,b.get(i));
			for(int i=0; i<colterm.size()-1; i++)
				for(int j=i+1; j<colterm.size(); j++)
					if(colterm.get(i).equals(colterm.get(j))){
						colterm.remove(j);
						j--;
					}
			}
		}
	}
	
	public static void initiateTable(){	//Making table to obtain essential prime Implicants
		for(int i=0; i<128; i++)
			for(int j=0; j<128; j++)
				table[i][j]=false;
		for(int i=0; i<colterm.size(); i++)
			for(int i1=0; i1<colterm.get(i).mterms.size(); i1++)
				for(int j=0; j<tms.size(); j++)
					if(colterm.get(i).mterms.get(i1) == tms.get(j).term)
						table[i][j]=true;
	}
		
	public static void executeTable1(){	//this method figures out the essential prime implicant which has a minterm contained in it and nowhere else
		int k=0;
		for(int i=0; i<128; i++)
			for(int j=0; j<128; j++)
				if(table[i][j]!=false)
					k++;
		if(k==0)	return;
		else{
			int sumR[] = new int[128];
			for(int i=0; i<128; i++)
				sumR[i]=0;
			for(int i=0; i<colterm.size(); i++)
				for(int j=0; j<tms.size(); j++)
					if(table[i][j])
						sumR[j]++;
			for(int j=0; j<tms.size(); j++)
				if(sumR[j]==1)
					for(int i=0; i<colterm.size(); i++)
						if(table[i][j]){
							table[i][j]=false;
							sumR[j]=0;
							essentials.add(colterm.get(i));
							for(int x=0; x<tms.size(); x++)
								if(table[i][x]){
									for(int y=0; y<colterm.size(); y++)
										table[y][x]=false;
									table[i][x]=false;
								}
							break;
						}
			for(int i=0; i<128; i++)
				sumR[i]=0;
			for(int i=0; i<colterm.size(); i++)
				for(int j=0; j<tms.size(); j++)
					if(table[i][j])
						sumR[j]++;
			int y=0;
			for(int j=0; j<tms.size(); j++)
				if(sumR[j]==1)
					y++;
			if(y>0)
				executeTable1();
			else
				executeTable2();
		}
	}
	
	public static void executeTable2(){	//this method figures out the essential Implicants by checking the size(no.  of minterms contained) of prime Implicants
		int k=0;
		for(int i=0; i<colterm.size(); i++)
			for(int j=0; j<tms.size(); j++)
				if(table[i][j]!=false)
					k++;
		if(k==0)	return;
		else{
			int maxIndex=0, max=0;
			int sumR[] = new int[128];
			for(int i=0; i<128; i++)
				sumR[i]=0;
			for(int i=0; i<colterm.size(); i++)
				for(int j=0; j<tms.size(); j++)
					if(table[i][j])
						sumR[i]++;
			for(int i=0; i<colterm.size(); i++)
				if(max<sumR[i]){
					max=sumR[i];
					maxIndex = i;
				}
			essentials.add(colterm.get(maxIndex));
			for(int x=0; x<tms.size(); x++){
				if(table[maxIndex][x])
					for(int i=0; i<colterm.size(); i++)
						table[i][x]=false;
				table[maxIndex][x]=false;
			}	
			executeTable1();
			executeTable2();
		}	
	}
	
	public static String finalOutput(int value, int sum, int var){	//produces final output of the Task
		int n=Integer.parseInt(Integer.toBinaryString(sum))*2;
		Integer m = n + Integer.parseInt(Integer.toBinaryString(value));
		String s = "";
		for(int i = 0;i<var-m.toString().length(); i++)
			s=s+"0";
		s=s+m;
		String out = "";
		for(int i=0; i<var; i++){
			if(s.charAt(i) == '0')
				out = out + (char)(i+91-var) + "\'";
			else if(s.charAt(i) == '1')
				out = out + (char)(i+91-var);
		}
		return out;
	}
	
	public static void input1(String l){	//takes input of the minterms
		if(l.length()>0){
		for( int i=0; i<l.length()-1; i++)
            for( int j=i+1; j<l.length()-1; j++)
                    if(l.charAt(i)!=' ' && l.charAt(j)==' '){
                    	Minterms temp = new Minterms(Integer.parseInt(l.substring(i, j)));
                            tm.add(temp);
                            tms.add(temp);
                            i=j;
                            break;
                    }
		for(int i=l.length()-1; i>-1; i--)
            	if(l.charAt(i)==' '){
            		Minterms temp = new Minterms(Integer.parseInt(l.substring(i+1,l.length())));
                    		tm.add(temp);
                    		tms.add(temp);
                    		break;
            		}
			if(tm.size()==0){
					Minterms temp = new Minterms(Integer.parseInt(l));
					tm.add(temp);
					tms.add(temp);
			}
		}
	}
	public static void input2(String d){	//takes input of the Don't Care terms
		if(d.length()>0){
		for( int i=0; i<d.length()-1; i++)
            for( int j=i+1; j<d.length()-1; j++)
                    if(d.charAt(i)!=' ' && d.charAt(j)==' '){
                    	Minterms temp = new Minterms(Integer.parseInt(d.substring(i, j)));
                            tm.add(temp);
                            i=j;
                            break;
                    }
		for(int i=d.length()-1; i>-1; i--)
            	if(d.charAt(i)==' '){
            		Minterms temp = new Minterms(Integer.parseInt(d.substring(i+1,d.length())));
                    	tm.add(temp);
                    	break;
            	}
		if(tm.size()==0){
			Minterms temp = new Minterms(Integer.parseInt(d));
            	tm.add(temp);
		}
		}
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {	//the main function
		Scanner a = new Scanner(System.in);
		System.out.print("\nEnter the Minterms : ");
		String b, d;
		b = a.nextLine();
		input1(b);
		System.out.print("Any Dont Care Terms : ");
		String c = a.nextLine();
		if(c.equals("Y") || c.equals("y")){
			System.out.print("Enter the Don't Care Terms : ");
			d = a.nextLine();
			input2(d);
		}
		maximum();
		executeColumn1();
		executeColumns();
		initiateTable();
		executeTable1();
		executeTable2();
		System.out.println("\n\nThe Minimized Expression Using queen Mckluskey Method is :\nExpression1 :");
		for(int i=0; i<essentials.size(); i++){
			System.out.print(finalOutput(essentials.get(i).value, essentials.get(i).sum, var));
			if(i<essentials.size()-1)
				System.out.print(" + ");
		}
		System.out.println("\n");
	}
}

