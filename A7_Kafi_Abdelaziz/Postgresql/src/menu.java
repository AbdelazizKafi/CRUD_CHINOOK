import java.util.Scanner;

/**
 * Classe principal que mostra un menú a la consola per gestionar els artistes.
 * Utilitza la classe {@link crud} per realitzar operacions CRUD sobre la base de dades.
 */
public class menu {

    /**
     * Mètode principal que inicia l'aplicació i mostra el menú. 
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        /*Instància de la classe que porta les operacions de la bbdd */
        crud crud = new crud(scanner);
        int opcion;

        do {
            mostrarMenu();
            opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1 -> crud.consultarTodosArtistas();
                case 2 -> crud.consultarArtistasPorNom();
                case 3 -> crud.consultarPrimerosAlbums();
                case 4 -> crud.afegirArtista();
                case 5 -> crud.modificarNomArtista();
                case 6 -> crud.borrarArtista();
                case 7 -> System.out.println("Sortint del programa...");
                default -> System.out.println("Opció no vàlida.");
            }

            if (opcion != 7) {
                System.out.println("\nPrem ENTER per continuar...");
                scanner.nextLine();
            }
        } while (opcion != 7);

        scanner.close();
    }

    /**
     * Print per mostrar el menú principal amb les opcions disponibles per a l'usuari.
     */
    private static void mostrarMenu() {
        System.out.println("\nMenú Principal");
        System.out.println("1 - Consultar tots els artistes");
        System.out.println("2 - Consultar artistes pel seu nom");
        System.out.println("3 - Consultar els 5 primers àlbums pel nom de l'artista");
        System.out.println("4 - Afegir un artista");
        System.out.println("5 - Modificar el nom d'un artista");
        System.out.println("6 - Borrar un artista");
        System.out.println("7 - Sortir");
    }

    /**
     * Lee la opción seleccionada por el usuario desde la entrada estándar.
     *
     * @param scanner Objecte scanner per llegir l'entrada de l'usuari
     * @return Número enter corresponent a l'opció seleccionada.
     *         Retorna 0 si l'entrada no es un número vàlid.
     */
    private static int leerOpcion(Scanner scanner) {
        System.out.print("Selecciona una opció: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
