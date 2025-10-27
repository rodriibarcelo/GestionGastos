package gestiongastos.vista;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;
import java.net.URL;

public class RegisterWindow extends JFrame {

    private static final String IMAGEN_POR_DEFECTO = "/Icons/ImageRegister.png";

    private JTextField nameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField greetingField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel imageLabel;
    private JButton cancelarButton;
    private JButton aceptarButton;
    private JButton cambiarImagenButton;
    private JDateChooser dateChooser;

    public RegisterWindow() {
        setTitle("Registro de Usuario");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Registro de Usuario", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0; inputPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridy = 1; inputPanel.add(new JLabel("Apellidos:"), gbc);
        gbc.gridy = 2; inputPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridy = 3; inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridy = 4; inputPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridy = 5; inputPanel.add(new JLabel("Fecha (DD/MM/AAAA):"), gbc);
        gbc.gridy = 6; inputPanel.add(new JLabel("Saludo:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0; nameField = new JTextField(20); inputPanel.add(nameField, gbc);
        gbc.gridy = 1; lastNameField = new JTextField(20); inputPanel.add(lastNameField, gbc);
        gbc.gridy = 2; phoneField = new JTextField(15); inputPanel.add(phoneField, gbc);
        gbc.gridy = 3; emailField = new JTextField(20); inputPanel.add(emailField, gbc);
        gbc.gridy = 4; passwordField = new JPasswordField(10); inputPanel.add(passwordField, gbc);

        gbc.gridx = 2; gbc.gridy = 4; inputPanel.add(new JLabel("Confirmar Contraseña:"), gbc);
        gbc.gridx = 3; gbc.gridy = 4; confirmPasswordField = new JPasswordField(10); inputPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(140, 25));
        inputPanel.add(dateChooser, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        greetingField = new JTextField(15);
        inputPanel.add(greetingField, gbc);

        // Imagen
        gbc.gridx = 2; gbc.gridy = 6; inputPanel.add(new JLabel("Imagen:"), gbc);
        gbc.gridx = 3; gbc.gridy = 6;
        imageLabel = new JLabel(loadIconOrPlaceholder(IMAGEN_POR_DEFECTO));
        imageLabel.setPreferredSize(new Dimension(120, 120));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(imageLabel, gbc);

        // Botonera
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cancelarButton = new JButton("Cancelar");
        aceptarButton = new JButton("Aceptar");
        cambiarImagenButton = new JButton("Cambiar imagen…");
        buttonPanel.add(cambiarImagenButton);
        buttonPanel.add(cancelarButton);
        buttonPanel.add(aceptarButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(aceptarButton);

        cancelarButton.addActionListener(e -> dispose());

        cambiarImagenButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                ImageIcon icon = new ImageIcon(fc.getSelectedFile().getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            }
        });

        aceptarButton.addActionListener(e -> {
            if (!validarCampos()) return;
            JOptionPane.showMessageDialog(this,
                    "Registro (simulado) correcto. Aún no se guarda en base de datos.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
    }

    private boolean validarCampos() {
        if (nameField.getText().trim().isEmpty()
                || lastNameField.getText().trim().isEmpty()
                || phoneField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty()
                || dateChooser.getDate() == null
                || passwordField.getPassword().length == 0
                || confirmPasswordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$").matcher(emailField.getText().trim()).matches()) {
            JOptionPane.showMessageDialog(this, "Email no válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!phoneField.getText().trim().matches("\\d{7,15}")) {
            JOptionPane.showMessageDialog(this, "Teléfono no válido (solo dígitos).",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String pass = new String(passwordField.getPassword());
        String pass2 = new String(confirmPasswordField.getPassword());
        if (!pass.equals(pass2)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.",
                    "Error de contraseña", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private Icon loadIconOrPlaceholder(String path) {
        URL res = getClass().getResource(path);
        if (res == null) {
            // placeholder simple
            BufferedImage img = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.LIGHT_GRAY); g.fillRect(0,0,120,120);
            g.setColor(Color.DARK_GRAY); g.drawString("Sin imagen", 20, 60);
            g.dispose();
            return new ImageIcon(img);
        }
        ImageIcon icon = new ImageIcon(res);
        Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
