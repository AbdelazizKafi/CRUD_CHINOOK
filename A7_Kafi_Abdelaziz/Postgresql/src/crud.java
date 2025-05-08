import java.sql.*;
import java.util.Scanner;

/**
 * Classe que conté les operacions CRUD.
 * Requereix un {@link Scanner} per llegir quina opció ha triat l'usuari
 */
public class crud {
    private Scanner scanner;

    /**
     * Constructor que inicialitza la classe amb un objecte Scanner.
     *
     * @param scanner per llegir dades de l'usuari.
     */
    public crud(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Fa connexió amb getConnection del fitxer connexio.
     * Es crea un Statement per fer consulter sql.
     * Mostra tots els artistes ordenats per nom.
     * Agafa les dades de la base de dades seleccionant el ID i el nom de tots el artistes ordenant alfabeticament.
     * Si hi han resultats (artistes), s'imprimeixen. Sino dona error.
     */
    public void consultarTodosArtistas() {
        try (Connection connection = connexio.getConnection();
             Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT artist_id, name FROM artist ORDER BY name");
            boolean hayResultados = false;

            while (rs.next()) {
                hayResultados = true;
                System.out.println("ID: " + rs.getInt("artist_id") + ", NOM: " + rs.getString("name"));
            }

            if (!hayResultados) {
                System.out.println("No hi ha resultats per aquesta consulta.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar artistes: " + e.getMessage());
        }
    }

    /**
     * Si el nom es inferior a 2 caràcters dona error.
     * Un altre statement per la consulta. 
     * Cerca artistes pel seu nom utilitzant ILIKE que es insensible a majúscules i minúscules.
     * Demana a l'usuari una entrada i mostra coincidències per pantalla.
     */
    public void consultarArtistasPorNom() {
        System.out.print("Introdueix el nom de l'artista: ");
        String nom = scanner.nextLine();

        if (nom.length() < 2) {
            System.out.println("Has d'introduir almenys 2 caràcters");
            return;
        }

        try (Connection connection = connexio.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "SELECT artist_id, name FROM artist WHERE name ILIKE ? ORDER BY name")) {

            pstmt.setString(1, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();
            boolean hayResultados = false;

            while (rs.next()) {
                hayResultados = true;
                System.out.println("ID: " + rs.getInt("artist_id") + ", NOM: " + rs.getString("name"));
            }

            if (!hayResultados) {
                System.out.println("No hi ha resultats per aquesta consulta.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cercar artistes: " + e.getMessage());
        }
    }

    /**
     * Dona error si el nom és inferior a 2 caràcters.
     * Fa una busqueda de l'ID de l'usuari seleccionat a la taula d'albums
     * Mostra els 5 primers àlbums associats a artistes que comencen pel nom proporcionat.
     * Dona error si no es troba resultat.
     */
    public void consultarPrimerosAlbums() {
        System.out.print("Introdueix el nom de l'artista: ");
        String nom = scanner.nextLine();

        if (nom.length() < 2) {
            System.out.println("Has d'introduir almenys 2 caràcters");
            return;
        }

        try (Connection connection = connexio.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "SELECT a.album_id, a.title, ar.name " +
                     "FROM album a " +
                     "JOIN artist ar ON a.artist_id = ar.artist_id " +
                     "WHERE ar.name LIKE ? " +
                     "ORDER BY a.title " +
                     "LIMIT 5")) {

            pstmt.setString(1, nom + "%");
            ResultSet rs = pstmt.executeQuery();
            boolean hayResultados = false;

            while (rs.next()) {
                hayResultados = true;
                System.out.println("ID_ALBUM: " + rs.getInt("album_id") +
                        ", NOM_ALBUM: " + rs.getString("title") +
                        ", NOM_ARTISTA: " + rs.getString("name"));
            }

            if (!hayResultados) {
                System.out.println("No hi ha resultats per aquesta consulta.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar els àlbums: " + e.getMessage());
        }
    }

    /**
     * El nom ha de ser superior a 2 caràcters sino donara error.
     * Es fa connexió a la bbdd.
     * Es selecciona el numero d'ID més alt de la taula artista i s'assigna al nou usuari introduït per consola.
     * Afegeix un nou artista a la base de dades amb un ID automàtic generat.
     * Dona error sino es compleix alguna condició.
     */
    public void afegirArtista() {
        System.out.print("Nom del nou artista: ");
        String nom = scanner.nextLine();

        if (nom.length() < 2) {
            System.out.println("Nom massa curt.");
            return;
        }

        try (Connection connection = connexio.getConnection()) {
            int nouId = 1;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(artist_id) FROM artist")) {
                if (rs.next()) {
                    nouId = rs.getInt(1) + 1;
                }
            }

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO artist (artist_id, name) VALUES (?, ?)")) {
                pstmt.setInt(1, nouId);
                pstmt.setString(2, nom);
                pstmt.executeUpdate();
                System.out.println("Afegit amb ID: " + nouId);
            }
        } catch (SQLException e) {
            System.out.println("Error afegint artista: " + e.getMessage());
        }
    }

    /**
     * L'usuari introdueix l'ID de l'artista a modificar.
     * Es converteix a int i es fa connexió a la bbdd.
     * Demana a l'usuari que introdueixi el nou nom de l'artista i valida que tingui al menys 2 caracters.
     * Statement per fer una consulta tipus UPDATE a la base de dades.
     * Si hi ha hagut modificació, dona un missatge d'exit. En cas contrari dona error.
     * Modifica el nom d'un artista existent a partir de la seva ID.
     */
    public void modificarNomArtista() {
        System.out.print("Introdueix l'ID de l'artista a modificar: ");
        String input = scanner.nextLine();
        int id;

        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("ID no vàlid.");
            return;
        }

        System.out.print("Nou nom: ");
        String nouNom = scanner.nextLine();

        if (nouNom.length() < 2) {
            System.out.println("Nom massa curt.");
            return;
        }

        try (Connection connection = connexio.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "UPDATE artist SET name = ? WHERE artist_id = ?")) {

            pstmt.setString(1, nouNom);
            pstmt.setInt(2, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Artista modificat correctament.");
            } else {
                System.out.println("No s'ha trobat cap artista amb aquesta ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error modificant artista: " + e.getMessage());
        }
    }

    /**
     * Scanner per a que l'usuari introdueixi l'ID de l'artista que vol eliminar.
     * Converteix l'ID en un int, si no es un numero o es un ID inexistent dona error.
     * Connexio a la base de dades i statement per fer una consulta DELETE.
     * Es configura la consulta amb l'ID de l'artista a eliminar
     * Si s'ha eliminat alguna fila dona missatge d'exit, sino dona error.
     */
    public void borrarArtista() {
        System.out.print("ID de l'artista a eliminar: ");
        String input = scanner.nextLine();
        int id;

        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("ID no vàlid.");
            return;
        }

        try (Connection connection = connexio.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "DELETE FROM artist WHERE artist_id = ?")) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Artista eliminat.");
            } else {
                System.out.println("No s'ha trobat cap artista amb aquesta ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error eliminant artista: " + e.getMessage());
        }
    }
}
