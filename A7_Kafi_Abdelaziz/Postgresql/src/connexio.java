import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/*He modificat el fitxer de connexió ja que per fer-ho de manera modular en varis fitxers no podia
 * amb el codi copiat a classe. Amb aquest fitxer només cal fer una trucada a connexio.getConnection().
 */
public class connexio {
    private static final String URL = "jdbc:postgresql://localhost:5432/Chinook";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("No es pot carregar el driver de PostgreSQL.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
