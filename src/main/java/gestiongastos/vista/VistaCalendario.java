package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Gasto;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DetailedDayView;
import com.calendarfx.view.MonthView;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class VistaCalendario {

    private final BorderPane root;

    public VistaCalendario() {

        root = new BorderPane();

        /* -----------------------------
           VISTAS DEL CALENDARIO
        ----------------------------- */

        DetailedDayView dayView = new DetailedDayView();
        dayView.setShowAllDayView(true);
        dayView.setDate(LocalDate.now());
        dayView.setPrefWidth(900); 
        dayView.setPrefHeight(650);

        MonthView monthView = new MonthView();
        monthView.setDate(LocalDate.now());
        monthView.setPrefWidth(900);
        monthView.setPrefHeight(650);

        // Contenedor para alternar vistas
        StackPane contenedorVistas = new StackPane(dayView, monthView);
        monthView.setVisible(false);  // Por defecto mostramos el día

        // --- Envolver en un Pane que bloquea TODA interacción ---
        StackPane bloqueador = new StackPane(contenedorVistas);

        // Este filtro absorbe absolutamente todos los eventos del ratón
        bloqueador.addEventFilter(javafx.scene.input.MouseEvent.ANY, e -> e.consume());
        bloqueador.addEventFilter(javafx.scene.input.DragEvent.ANY, e -> e.consume());

        bloqueador.setFocusTraversable(false);

        root.setCenter(bloqueador);


        /* -----------------------------
           TOP: Selector de fecha y vista
        ----------------------------- */

        DatePicker selectorFecha = new DatePicker(LocalDate.now());

        ComboBox<String> selectorVista = new ComboBox<>();
        selectorVista.getItems().addAll("Día", "Mes");
        selectorVista.setValue("Día");

        selectorVista.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals("Día")) {
                dayView.setVisible(true);
                monthView.setVisible(false);
            } else {
                dayView.setVisible(false);
                monthView.setVisible(true);
            }
        });

        selectorFecha.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dayView.setDate(newDate);
                monthView.setDate(newDate);
            }
        });

        VBox top = new VBox(10, selectorFecha, selectorVista);
        top.setPadding(new Insets(10));
        root.setTop(top);

        /* -----------------------------
           CALENDARIO DE GASTOS
        ----------------------------- */

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

        CalendarSource source = new CalendarSource("Mis Gastos");
        source.getCalendars().add(calGastos);

        dayView.getCalendarSources().add(source);
        monthView.getCalendarSources().add(source);
    }

    public Parent getRoot() {
        return root;
    }
}
