package gestiongastos.persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import gestiongastos.dominio.Notificacion;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotificacionRepositoryJson implements NotificacionRepository {

    private static final NotificacionRepositoryJson INSTANCE =
            new NotificacionRepositoryJson();

    private final Path filePath;
    private final List<Notificacion> datos;

    private NotificacionRepositoryJson() {
        this.filePath = Path.of(
                System.getProperty("user.dir"),
                "data",
                "notificaciones.json"
        );

        this.datos = new ArrayList<>(
                JsonStorage.loadList(
                        filePath,
                        new TypeReference<List<Notificacion>>() {}
                )
        );
    }

    public static NotificacionRepositoryJson getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized void save(Notificacion n) {
        datos.add(n);
        JsonStorage.saveList(filePath, datos);
    }

    @Override
    public synchronized List<Notificacion> findByUsuario(UUID usuarioId) {
        return datos.stream()
                .filter(n -> n.getUsuarioId() != null)
                .filter(n -> n.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }
}
