package gestiongastos.persistencia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PersistenciaJSON {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public <T> void save(String filePath, Collection<T> data) throws Exception {
        File f = new File(filePath);
        f.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(f, data);
    }

    public <T> List<T> load(String filePath, Class<T[]> arrayType) throws Exception {
        File f = new File(filePath);
        if (!f.exists() || Files.size(f.toPath()) == 0) return List.of();
        T[] arr = mapper.readValue(f, arrayType);
        return Arrays.asList(arr);
    }
}
