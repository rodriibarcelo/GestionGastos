package gestiongastos.importador;

import gestiongastos.dominio.Gasto;

import java.nio.file.Path;
import java.util.List;

public interface ImportadorGastos {

    List<Gasto> importar(Path fichero) throws Exception;

}
