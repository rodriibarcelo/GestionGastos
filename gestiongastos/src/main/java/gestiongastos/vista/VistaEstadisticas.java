package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.Gasto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class VistaEstadisticas {

    private final BorderPane root;
    private final Controlador ctrl;

    private final TableView<RowResumen> tvResumen = new TableView<>();

    public VistaEstadisticas() {
        this.ctrl = Controlador.getInstance();
        this.root = new BorderPane();
        this.root.setPadding(new Insets(12));

        Label title = new Label("Estadísticas de gastos");
        BorderPane.setAlignment(title, Pos.CENTER_LEFT);
        BorderPane.setMargin(title, new Insets(0, 0, 8, 0));
        root.setTop(title);

        TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("Resumen", crearResumen()));
        tabs.getTabs().add(new Tab("Tarta por categoría", crearTartaPorCategoria()));
        tabs.getTabs().add(new Tab("Barras por categoría", crearBarrasPorCategoria()));
        tabs.getTabs().add(new Tab("Evolución mensual", crearLineaMensual()));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        root.setCenter(tabs);
    }

    public Parent getRoot() { return root; }

    // ======= Resumen (tabla) =======
    private Parent crearResumen() {
        tvResumen.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<RowResumen, String> cCat = new TableColumn<>("Categoría");
        cCat.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        TableColumn<RowResumen, BigDecimal> cTotal = new TableColumn<>("Total (€)");
        cTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        TableColumn<RowResumen, String> cPct = new TableColumn<>("% sobre total");
        cPct.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));

        tvResumen.getColumns().setAll(cCat, cTotal, cPct);
        tvResumen.setItems(datosResumen());

        BorderPane pane = new BorderPane();
        pane.setCenter(tvResumen);
        return pane;
    }

    private ObservableList<RowResumen> datosResumen() {
        List<Gasto> gastos = ctrl.listarGastos();
        Map<UUID, String> catNames = ctrl.listarCategorias().stream()
                .collect(Collectors.toMap(Categoria::getId, Categoria::getNombre));
        BigDecimal total = gastos.stream().map(Gasto::getCantidad).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> porCat = gastos.stream()
                .collect(Collectors.groupingBy(
                        g -> catNames.getOrDefault(g.getCategoriaId(), "(sin categoría)"),
                        Collectors.mapping(Gasto::getCantidad, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        List<RowResumen> rows = porCat.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(e -> new RowResumen(
                        e.getKey(),
                        e.getValue().setScale(2, RoundingMode.HALF_UP),
                        total.signum() == 0 ? "0,00 %" :
                                e.getValue().multiply(BigDecimal.valueOf(100))
                                        .divide(total, 2, RoundingMode.HALF_UP) + " %"
                ))
                .toList();
        return FXCollections.observableArrayList(rows);
    }

    // ======= Tarta por categoría =======
    private Parent crearTartaPorCategoria() {
        List<Gasto> gastos = ctrl.listarGastos();
        Map<UUID, String> catNames = ctrl.listarCategorias().stream()
                .collect(Collectors.toMap(Categoria::getId, Categoria::getNombre));

        Map<String, BigDecimal> porCat = gastos.stream()
                .collect(Collectors.groupingBy(
                        g -> catNames.getOrDefault(g.getCategoriaId(), "(sin categoría)"),
                        Collectors.mapping(Gasto::getCantidad, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        porCat.forEach((cat, sum) -> data.add(new PieChart.Data(cat, sum.doubleValue())));

        PieChart chart = new PieChart(data);
        chart.setTitle("Distribución por categoría");

        // -> aplicar color por categoría (según Categoria.getColorHex)
        Map<String, String> colorByName = coloresPorNombre();
        for (PieChart.Data d : chart.getData()) {
            String hex = colorByName.getOrDefault(d.getName(), "#808080");
            String css = "-fx-pie-color: " + hex + ";";
            d.nodeProperty().addListener((obs, oldN, newN) -> { if (newN != null) newN.setStyle(css); });
            if (d.getNode() != null) d.getNode().setStyle(css);
        }

        return wrap(chart);
    }

    // ======= Barras por categoría =======
    private Parent crearBarrasPorCategoria() {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle("Total por categoría");

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Gastos");

        List<Gasto> gastos = ctrl.listarGastos();
        Map<UUID, String> catNames = ctrl.listarCategorias().stream()
                .collect(Collectors.toMap(Categoria::getId, Categoria::getNombre));

        Map<String, BigDecimal> porCat = gastos.stream()
                .collect(Collectors.groupingBy(
                        g -> catNames.getOrDefault(g.getCategoriaId(), "(sin categoría)"),
                        Collectors.mapping(Gasto::getCantidad, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        porCat.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .forEach(e -> serie.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())));

        chart.getData().add(serie);

        // -> colorear cada barra según la categoría
        Map<String, String> colorByName = coloresPorNombre();
        for (XYChart.Data<String, Number> d : serie.getData()) {
            String hex = colorByName.getOrDefault(d.getXValue(), "#808080");
            String css = "-fx-bar-fill: " + hex + ";";
            d.nodeProperty().addListener((obs, o, n) -> { if (n != null) n.setStyle(css); });
            if (d.getNode() != null) d.getNode().setStyle(css);
        }

        return wrap(chart);
    }

    // ======= Línea: evolución mensual =======
    private Parent crearLineaMensual() {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(x, y);
        chart.setTitle("Evolución mensual (total €)");

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Mensual");

        List<Gasto> gastos = ctrl.listarGastos();
        Map<YearMonth, BigDecimal> porMes = gastos.stream()
                .collect(Collectors.groupingBy(
                        g -> YearMonth.from(g.getFecha()),
                        TreeMap::new,
                        Collectors.mapping(Gasto::getCantidad, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        porMes.forEach((ym, sum) -> serie.getData().add(new XYChart.Data<>(ym.toString(), sum)));

        chart.getData().add(serie);
        return wrap(chart);
    }

    // ======= helpers de color =======
    private Map<String, String> coloresPorNombre() {
        // nombreCategoria -> hex normalizado
        return ctrl.listarCategorias().stream()
                .collect(Collectors.toMap(
                        Categoria::getNombre,
                        c -> normalizaHex(c.getColorHex()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private static String normalizaHex(String hex) {
        if (hex == null) return "#808080";
        String h = hex.trim();
        if (h.isEmpty()) return "#808080";
        if (!h.startsWith("#")) h = "#" + h;
        if (h.matches("#[0-9a-fA-F]{6}")) return h;
        if (h.matches("#[0-9a-fA-F]{3}")) {
            char r = h.charAt(1), g = h.charAt(2), b = h.charAt(3);
            return ("#" + r + r + g + g + b + b);
        }
        return "#808080";
    }

    private static BorderPane wrap(Parent chart) {
        BorderPane bp = new BorderPane(chart);
        bp.setPadding(new Insets(6));
        return bp;
    }

    // ======= DTO tabla resumen =======
    public static class RowResumen {
        private final String categoria;
        private final BigDecimal total;
        private final String porcentaje;

        public RowResumen(String categoria, BigDecimal total, String porcentaje) {
            this.categoria = categoria;
            this.total = total;
            this.porcentaje = porcentaje;
        }
        public String getCategoria() { return categoria; }
        public BigDecimal getTotal() { return total; }
        public String getPorcentaje() { return porcentaje; }
    }
}
