
import java.sql.SQLException;

public class index {

	public static void main(String[] args) {
		JDBC jdbc = new JDBC();
		jdbc.firstHead();
		try {
			jdbc.productQuant();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println();
		jdbc.secondHead();
		try {
			jdbc.addressQuant();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
