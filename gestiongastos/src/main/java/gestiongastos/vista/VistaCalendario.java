package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Gasto;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DetailedDayView;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class VistaCalendario {

    private final BorderPane root;

    public VistaCalendario() {

        root = new BorderPane();

        // Vista diaria de CalendarFX (Full Day)
        DetailedDayView dayView = new DetailedDayView();
        dayView.setShowAllDayView(true);
        dayView.setDate(LocalDate.now());   // empezamos en hoy

        // ---- Selector de fecha (para cambiar el día mostrado) ----
        DatePicker selectorFecha = new DatePicker(LocalDate.now());
        selectorFecha.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dayView.setDate(newDate);  // cambiamos el día mostrado
            }
        });

        VBox top = new VBox(10, selectorFecha);
        top.setPadding(new Insets(10));
        root.setTop(top);

        // ---- Cargar entradas de gastos ----
        Calendar calGastos = new Calendar("Gastos");
        calGastos.setStyle(Calendar.Style.STYLE2);

        List<Gasto> gastos = Controlador.getInstance().listarGastos();

        for (Gasto g : gastos) {
            String titulo = g.getNota().isBlank()
                    ? g.getCantidad() + " €"
                    : g.getNota() + " (" + g.getCantidad() + " €)";

            Entry<String> entry = new Entry<>(titulo);

            entry.changeStartDate(g.getFecha());
            entry.changeEndDate(g.getFecha());

            entry.changeStartTime(LocalTime.of(9, 0));
            entry.changeEndTime(LocalTime.of(10, 0));

            calGastos.addEntry(entry);
        }

        // ---- Añadir calendario a la vista ----
        CalendarSource source = new CalendarSource("Mis Gastos");
        source.getCalendars().add(calGastos);

        dayView.getCalendarSources().add(source);

        root.setCenter(dayView);
    }

    public Parent getRoot() {
        return root;
    }
}
