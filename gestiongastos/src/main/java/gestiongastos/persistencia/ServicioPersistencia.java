package gestiongastos.persistencia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.Gasto;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServicioPersistencia {

    private final ObjectMapper mapper;
    private final File fileGastos;
    private final File fileCategorias;

    public ServicioPersistencia(final String baseFolder) {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());

        File base = new File(baseFolder);
        if (!base.exists()) {
            base.mkdirs();
        }

        this.fileGastos = new File(base, "gastos.json");
        this.fileCategorias = new File(base, "categorias.json");
    }

    public List<Gasto> cargarGastos() throws Exception {
        if (!fileGastos.exists() || Files.size(fileGastos.toPath()) == 0) {
            return new ArrayList<>();
        }
        Gasto[] arr = mapper.readValue(fileGastos, Gasto[].class);
        return new ArrayList<>(Arrays.asList(arr));
    }

    public void guardarGastos(final List<Gasto> gastos) throws Exception {
        mapper.writerWithDefaultPrettyPrinter().writeValue(fileGastos, gastos);
    }

    public List<Categoria> cargarCategorias() throws Exception {
        if (!fileCategorias.exists() || Files.size(fileCategorias.toPath()) == 0) {
            return new ArrayList<>();
        }
        Categoria[] arr = mapper.readValue(fileCategorias, Categoria[].class);
        return new ArrayList<>(Arrays.asList(arr));
    }

    public void guardarCategorias(final List<Categoria> categorias) throws Exception {
        mapper.writerWithDefaultPrettyPrinter().writeValue(fileCategorias, categorias);
    }
}
