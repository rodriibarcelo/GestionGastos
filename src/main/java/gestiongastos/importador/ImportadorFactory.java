package gestiongastos.importador;

public class ImportadorFactory {

    public static ImportadorGastos crear(String extension) {

        extension = extension.toLowerCase();

        return switch (extension) {
            case "csv" -> new ImportadorCSV();
            default -> throw new IllegalArgumentException("Formato no soportado: " + extension);
        };
    }
}
