package gestiongastos.dominio;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notificacion {

    private UUID id;
    private UUID usuarioId;
    private String mensaje;
    private LocalDateTime fecha;

    public Notificacion() {
    }

    public Notificacion(UUID usuarioId, String mensaje) {
        this.id = UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.mensaje = mensaje;
        this.fecha = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
