package gestiongastos.importador;

import gestiongastos.dominio.Gasto;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ImportadorCSV implements ImportadorGastos {

    private static final DateTimeFormatter DF_DMY =
            DateTimeFormatter.ofPattern("d/M/uuuu");   

    @Override
    public List<Gasto> importar(final Path fichero) throws IOException {

        List<Gasto> lista = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(fichero, StandardCharsets.UTF_8)) {

            String header = br.readLine(); // la ignoramos

            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }

                String[] c = linea.split(",", -1);
                if (c.length < 7) {
                    throw new IllegalArgumentException("Línea CSV inválida: " + linea);
                }

                
                String rawFecha = c[0].trim();
                LocalDate fecha = parseFecha(rawFecha);

                String categoria = c[2].trim();
                String subcategoria = c[3].trim();

                String nota = c[4].trim();

                //(puede usar coma o punto)
                String rawImporte = c[6].trim().replace(",", ".");
                BigDecimal cantidad = new BigDecimal(rawImporte);

                if (!subcategoria.isBlank()) {
                    categoria = categoria + " / " + subcategoria;
                }

                Gasto g = new Gasto(cantidad, fecha, null, nota);
                g.setCategoriaNombreTemp(categoria);

                lista.add(g);
            }
        }

        return lista;
    }

    private LocalDate parseFecha(final String raw) {

        // Quitar la hora si existe
        String soloFecha = raw.split(" ")[0].trim();

        // FORMATOS POSIBLES
        DateTimeFormatter[] formatos = new DateTimeFormatter[]{
                DateTimeFormatter.ISO_LOCAL_DATE,           // yyyy-MM-dd
                DateTimeFormatter.ofPattern("d/M/uuuu"),    // 3/2/2022
                DateTimeFormatter.ofPattern("dd/MM/uuuu"),  // 03/02/2022
                DateTimeFormatter.ofPattern("M/d/uuuu"),    // 2/28/2022
                DateTimeFormatter.ofPattern("MM/dd/uuuu")   // 02/28/2022
        };

        for (DateTimeFormatter f : formatos) {
            try {
                return LocalDate.parse(soloFecha, f);
            } catch (Exception ignore) {}
        }

        throw new IllegalArgumentException("Fecha no reconocida: " + raw);
    }

}
