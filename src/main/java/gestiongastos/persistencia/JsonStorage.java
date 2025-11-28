package gestiongastos.persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonStorage {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private JsonStorage() {
    }

    public static <T> List<T> loadList(final Path path,
                                       final TypeReference<List<T>> typeRef) {

        try {
            if (!Files.exists(path)) {
                return List.of();
            }
            byte[] json = Files.readAllBytes(path);
            if (json.length == 0) {
                return List.of();
            }
            return MAPPER.readValue(json, typeRef);
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo JSON de " + path, e);
        }
    }

    public static <T> void saveList(final Path path,
                                    final List<T> data) {

        try {
            Files.createDirectories(path.getParent());
            byte[] json = MAPPER.writerWithDefaultPrettyPrinter()
                                .writeValueAsBytes(data);
            Files.write(path, json);
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo JSON en " + path, e);
        }
    }
}
