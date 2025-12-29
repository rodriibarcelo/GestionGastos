package gestiongastos.persistencia;

import java.util.List;
import java.util.UUID;

import gestiongastos.dominio.Notificacion;

public interface NotificacionRepository {

    void save(Notificacion n);

    List<Notificacion> findByUsuario(UUID usuarioId);
}
