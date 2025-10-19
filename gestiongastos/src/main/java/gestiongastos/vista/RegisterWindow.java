package gestiongastos.vista;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;

public class RegisterWindow extends JFrame {

    private static final String IMAGEN_POR_DEFECTO = "/Icons/ImageRegister.png";

    // Componentes de la vista
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
    private JDateChooser dateChooser;

    public RegisterWindow() {
        setTitle("Registro de Usuario");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Registro de Usuario", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Columna de etiquetas
        gbc.gridx = 0;
        gbc.gridy = 0; inputPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridy = 1; inputPanel.add(new JLabel("Apellidos:"), gbc);
        gbc.gridy = 2; inputPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridy = 3; inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridy = 4; inputPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridy = 5; inputPanel.add(new JLabel("Fecha (DD/MM/AAAA):"), gbc);
        gbc.gridy = 6; inputPanel.add(new JLabel("Saludo:"), gbc);

        // Columna de campos
        gbc.gridx = 1;
        gbc.gridy = 0; nameField = new JTextField(20); inputPanel.add(nameField, gbc);
        gbc.gridy = 1; lastNameField = new JTextField(20); inputPanel.add(lastNameField, gbc);
        gbc.gridy = 2; phoneField = new JTextField(15); inputPanel.add(phoneField, gbc);
        gbc.gridy = 3; emailField = new JTextField(20); inputPanel.add(emailField, gbc);
        gbc.gridy = 4; passwordField = new JPasswordField(10); inputPanel.add(passwordField, gbc);

        // Confirmación de contraseña
        gbc.gridx = 2; gbc.gridy = 4; inputPanel.add(new JLabel("Confirmar Contraseña:"), gbc);
        gbc.gridx = 3; gbc.gridy = 4; confirmPasswordField = new JPasswordField(10); inputPanel.add(confirmPasswordField, gbc);

        // Fecha
        gbc.gridx = 1; gbc.gridy = 5;
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(140, 25));
        inputPanel.add(dateChooser, gbc);

        // Saludo
        gbc.gridx = 1; gbc.gridy = 6;
        greetingField = new JTextField(15);
        inputPanel.add(greetingField, gbc);

        // Imagen (por defecto, sin interacción)
        gbc.gridx = 2; gbc.gridy = 6; inputPanel.add(new JLabel("Imagen:"), gbc);
        gbc.gridx = 3; gbc.gridy = 6;
        imageLabel = new JLabel(new ImageIcon(getClass().getResource(IMAGEN_POR_DEFECTO)));
        imageLabel.setPreferredSize(new Dimension(120, 120));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(imageLabel, gbc);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Botonera
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cancelarButton = new JButton("Cancelar");
        aceptarButton = new JButton("Aceptar");
        buttonPanel.add(cancelarButton);
        buttonPanel.add(aceptarButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Enter = Aceptar
        getRootPane().setDefaultButton(aceptarButton);

        // Listeners
        cancelarButton.addActionListener(e -> dispose());

        aceptarButton.addActionListener(e -> {
            // Validación mínima (sin registrar nada aún)
            if (nameField.getText().trim().isEmpty()
                    || lastNameField.getText().trim().isEmpty()
                    || phoneField.getText().trim().isEmpty()
                    || emailField.getText().trim().isEmpty()
                    || dateChooser.getDate() == null
                    || passwordField.getPassword().length == 0
                    || confirmPasswordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Por favor, completa todos los campos.",
                        "Campos incompletos",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String pass = new String(passwordField.getPassword());
            String pass2 = new String(confirmPasswordField.getPassword());
            if (!pass.equals(pass2)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Las contraseñas no coinciden.",
                        "Error de contraseña",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Simulación de registro correcto
            JOptionPane.showMessageDialog(
                    this,
                    "Registro (simulado) correcto. Aún no se guarda en base de datos.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
        });

        add(mainPanel);
    }
}
