
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
/*
 * JDBC class include 
 * function jdbc() connect database and return a resultset object
 * function firstHead() 
 * function secondHead() 
 * function dataformat() Transfer data format to "MM/DD/YYYY" 
 * function productQuant() come true the first report
 * function addressQuant() come true the second report
 * */


public class JDBC {
	private static String usr;
	private static String pwd; 
	
	public static void main(String[] args) {
		JDBC jdbc = new JDBC();
		Scanner in = new Scanner(System.in);
		System.out.println("Please input the usename of the database");
		String input = in.nextLine();
		usr = input;
		
		System.out.println("Please input the password of the database");
		String inputpwd = in.nextLine();
		pwd = inputpwd;
		
		try {
			jdbc.productQuant();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println();
		try {
			jdbc.addressQuant();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/*Connect to postSQL database system*/
	public ResultSet jdbc()
	{
//		Scanner in = new Scanner(System.in);
//		System.out.println("Please input the usename of the database");
//		String input = in.nextLine();
//		String usr = input;
//		//String usr = "postgres";
//		System.out.println("Please input the password of the database");
//		String inputpwd = in.nextLine();
//		String pwd = inputpwd;
//		//String pwd = "oracle";
		String url = "jdbc:postgresql://localhost:5432/postgres";

		try
		{
			Class.forName("org.postgresql.Driver");
			//System.out.println("Success loading Driver!");
		}

		catch(Exception e)
		{
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}
		ResultSet rs = null;
		try
		{
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			//System.out.println("Success connecting server!");
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Sales");
			
		}

		catch(SQLException e)
		{
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
		return rs;
	}
	
	
	//Print first report head
	public void firstHead(){
		String prod = String.format("%-8s","PRODUCT");
		String date = String.format("%-10s","DATE");
		System.out.println(prod+" "+"MAX_Q"+" "+"CUSTOMER"+" "+date+" "+"ST"+" "+
									"MIN_Q"+" "+"CUSTOMER"+" "+date+" "+"ST"+" "+
									"AVG_Q");
		System.out.print("======== ===== ======== ========== == ");
		System.out.println("===== ======== ========== == =====");
	}
	//Print second report head
	public void secondHead(){
		String date = String.format("%-10s","DATE");
		System.out.println("CUSTOMER PRODUCT CT_MAX"+" "+date+" "+
												"NY_MIN"+" "+date+" "+
												"NJ_MIN"+" "+date);
		System.out.print("======== ======= ====== ========== ");
		System.out.println("====== ========== ====== ==========");
	}
	/*function dataformat:Transfer data format to "MM/DD/YYYY"
	 *parameter 
	 *m: month; d:day; y:year
	 */
	public String dataformat(String m , String d, String y)
	{
		Date date = null;
		String dataformat;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			date = sdf.parse(m+"/"+d+"/"+y);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataformat = sdf.format(date);
		return dataformat;
	}
	
	/*Print first report*/
	public void productQuant() throws SQLException
	{
		ResultSet rs = jdbc();
		firstHead();
		HashMap<String , Integer> maxmap = new HashMap<String , Integer>();
		HashMap<String , String> maxCustomermap = new HashMap<String , String>();
		HashMap<String , String> maxDatemap = new HashMap<String , String>();
		HashMap<String , String> maxStmap = new HashMap<String , String>();
		HashMap<String , Integer> minmap = new HashMap<String , Integer>();
		HashMap<String , String> minCustomermap = new HashMap<String , String>();
		HashMap<String , String> minDatemap = new HashMap<String , String>();
		HashMap<String , String> minStmap = new HashMap<String , String>();
		HashMap<String , Integer> summap = new HashMap<String , Integer>();
		HashMap<String , Integer> countermap = new HashMap<String , Integer>();
		
		int sum = 0;	
		int counter;
		int quant;
		String dateform;
		String product;
		//traverse the data of database
		while(rs.next()){
			dateform = this.dataformat(rs.getString("month"), rs.getString("day"), rs.getString("year"));
			product = rs.getString("prod");
			quant = rs.getInt("quant");
			/*
			 * */
			if(maxmap.containsKey(product))
			{
				sum = summap.get(product) + quant;
				counter = countermap.get(product)+1;
				summap.put(product, sum);
				countermap.put(product, counter);
			}
			/*if Quant is more than data in maxmap execute exchange operation*/
			if(maxmap.containsKey(product) && quant>maxmap.get(product) )
			{
				maxmap.put(product, quant);
				maxCustomermap.put(product, rs.getString("cust"));
				maxStmap.put(product, rs.getString("state"));
				maxDatemap.put(product, dateform);
			}
			/*if Quant is small than data in minmap execute exchange operation*/
			if(minmap.containsKey(product) && quant<minmap.get(product))
			{
				minmap.put(product, quant);
				minCustomermap.put(product, rs.getString("cust"));
				minStmap.put(product, rs.getString("state"));
				minDatemap.put(product, dateform);	
			}
			/*put the first value into hashmap*/
			if(!maxmap.containsKey(product)||!minmap.containsKey(product))
			{
				maxmap.put(product, quant);
				minmap.put(product, quant);
				maxCustomermap.put(product, rs.getString("cust"));
				minCustomermap.put(product, rs.getString("cust"));
				maxStmap.put(product, rs.getString("state"));
				minStmap.put(product, rs.getString("state"));
				maxDatemap.put(product, dateform);
				minDatemap.put(product, dateform);
				summap.put(product, quant);
				countermap.put(product, 1);
			}	
		};
		
		String prod;
		String max;
		String maxCustomer;
		String min;
		String minCustomer;
		String avgValue;
		Iterator<String> maxiter = maxmap.keySet().iterator();
		//iterator print the request information
		while (maxiter.hasNext()) {
		    // get key(product)
		    prod = (String)maxiter.next();
		    max = String.format("%5d", (Integer)maxmap.get(prod));
		    min = String.format("%5d", (Integer)minmap.get(prod));
		    maxCustomer = String.format("%-8s", (String)maxCustomermap.get(prod));
		    minCustomer = String.format("%-8s", (String)minCustomermap.get(prod));
		    avgValue = String.format("%5d", (Integer)summap.get(prod)/(Integer)countermap.get(prod));
		    
		    System.out.print(String.format("%-8s", prod)+" "
		    		+max+" "+maxCustomer+" "+(String)maxDatemap.get(prod)+" "+(String)maxStmap.get(prod)+" "
		    		+min+" "+minCustomer+" "+(String)minDatemap.get(prod)+" "+(String)minStmap.get(prod)+" ");
		    System.out.println(avgValue);   
		}
	}
	
	//Print second report
	public void addressQuant() throws SQLException
	{
		ResultSet rs = jdbc();
		secondHead();
		TreeMap<String , Integer> custP = new TreeMap<String , Integer>();
		HashMap<String , Integer> ctMax = new HashMap<String , Integer>();
		HashMap<String , Integer> nyMin = new HashMap<String , Integer>();
		HashMap<String , Integer> njMin = new HashMap<String , Integer>();
		HashMap<String , String> ctDate = new HashMap<String , String>();
		HashMap<String , String> nyDate = new HashMap<String , String>();
		HashMap<String , String> njDate = new HashMap<String , String>();
		
		String custProd;
		String state;
		String dateform;
		int quant;
		//traverse the data of database
		while(rs.next())
		{
			dateform = this.dataformat(rs.getString("month"), rs.getString("day"), rs.getString("year"));
			custProd = rs.getString("cust")+" "+rs.getString("prod");
			quant = rs.getInt("quant");
			state = rs.getString("state");
			custP.put(custProd , 1);
			if(!ctMax.containsKey(custProd) && state.equals("CT"))
			{
				if(rs.getInt("year")>=2000 && rs.getInt("year")<=2005)
				{
					ctMax.put(custProd , quant);
					ctDate.put(custProd, dateform);	
				}
			}
			if(!nyMin.containsKey(custProd) && state.equals("NY"))
			{
				nyMin.put(custProd , quant);
				nyDate.put(custProd, dateform);	
			}
			if(!njMin.containsKey(custProd) && state.equals("NJ"))
			{
				njMin.put(custProd , quant);
				njDate.put(custProd, dateform);	
			}
			if(ctMax.containsKey(custProd) && quant > ctMax.get(custProd) && state.equals("CT"))
			{	
				if(rs.getInt("year")>=2000 && rs.getInt("year")<=2005)
				{
					ctMax.put(custProd , quant);
					ctDate.put(custProd, dateform);	
				}
			}
			if(nyMin.containsKey(custProd) && quant < nyMin.get(custProd) && state.equals("NY"))
			{
				nyMin.put(custProd , quant);
				nyDate.put(custProd, dateform);
			}
			if(njMin.containsKey(custProd) && quant < njMin.get(custProd) && state.equals("NJ"))
			{
				njMin.put(custProd , quant);
				njDate.put(custProd, dateform);	
			}	
		}
		String[] strarray = new String[2];
		String str;
		String cust;
		String prod;
		String ctmax;
		String nymin;
		String njmin;
		String ctdate;
		String nydate;
		String njdate;
		Iterator<String> ctiter = custP.keySet().iterator();
		//System.out.println(custP.size());
		/*iterator print request information of second report */
		while (ctiter.hasNext()) {
			str = ctiter.next();
			//System.out.println(ctiter.next());
			strarray = str.split(" ");
			cust = String.format("%-8s", strarray[0]);
			prod = String.format("%-7s", strarray[1]);
			
			ctmax = String.format("%6s", (Integer)ctMax.get(str)==null?" NULL":(Integer)ctMax.get(str));
			nymin = String.format("%6s", (Integer)nyMin.get(str)==null?" NULL":(Integer)nyMin.get(str));
			njmin = String.format("%6s", (Integer)njMin.get(str)==null?" NULL":(Integer)njMin.get(str));
			
			ctdate = (String)ctDate.get(str)==null?"      NULL":(String)ctDate.get(str);
			nydate = (String)nyDate.get(str)==null?"      NULL":(String)nyDate.get(str);
			njdate = (String)njDate.get(str)==null?"      NULL":(String)njDate.get(str);
			
			System.out.println(cust+" "+prod+" "+ctmax+" "+ctdate+" "+nymin+" "+nydate+" "+njmin+" "+njdate);
		}
	}
}
