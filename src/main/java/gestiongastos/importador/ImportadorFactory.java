package gestiongastos.importador;

import java.util.Map;

public class ImportadorFactory {

    private static final Map<String, ImportadorGastos> IMPORTADORES = Map.of(
            "csv", new ImportadorCSV()
    );

    public static ImportadorGastos crear(String tipo) {
        if (tipo == null)
            throw new IllegalArgumentException("Tipo de importador no puede ser null");

        ImportadorGastos imp = IMPORTADORES.get(tipo.toLowerCase());

        if (imp == null)
            throw new IllegalArgumentException("Tipo de importador no soportado: " + tipo);

        return imp;
    }
}
