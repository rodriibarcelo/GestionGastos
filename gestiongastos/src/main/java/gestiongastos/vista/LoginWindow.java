package gestiongastos.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import tds.controlador.Controlador;

public class LoginWindow extends JFrame {

    private JPanel contentPane;
    private JTextField textUsuario;
    private JPasswordField textPassword;
    private JButton btnRegistrar;
    private JButton btnAceptar;
    private JButton btnCancelar;

    public LoginWindow() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(500, 200, 450, 300);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        // Título
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(titleLabel, BorderLayout.NORTH);

        // Panel central con campos
        JPanel telCon = new JPanel(new GridBagLayout());
        contentPane.add(telCon, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        textUsuario = new JTextField(20);
        JLabel userLabel = new JLabel("Usuario");
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);

        textPassword = new JPasswordField(20);
        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0; gbc.gridy = 0; telCon.add(userLabel, gbc);
        gbc.gridy = 1; telCon.add(textUsuario, gbc);
        gbc.gridy = 2; telCon.add(passwordLabel, gbc);
        gbc.gridy = 3; telCon.add(textPassword, gbc);

        // Panel inferior con botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS));
        contentPane.add(panelBotones, BorderLayout.SOUTH);

        btnRegistrar = new JButton("Registrar");
        panelBotones.add(btnRegistrar);

        panelBotones.add(Box.createHorizontalGlue());

        btnCancelar = new JButton("Cancelar");
        btnAceptar = new JButton("Aceptar");
        panelBotones.add(btnCancelar);
        panelBotones.add(btnAceptar);

        // Enter = Aceptar
        getRootPane().setDefaultButton(btnAceptar);

        // === Listeners ===

        // Registrar
        btnRegistrar.addActionListener(e -> {
            RegisterWindow registro = new RegisterWindow();
            registro.setLocationRelativeTo(this);
            registro.setVisible(true);
        });

        // Cancelar
        btnCancelar.addActionListener(e -> System.exit(0));

        // Aceptar (login)
        btnAceptar.addActionListener(e -> {
            String usuario = textUsuario.getText().trim();
            String password = new String(textPassword.getPassword());

            if (usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Introduce usuario y contraseña.",
                        "Campos vacíos",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            boolean ok = false;
            try {
                ok = Controlador.INSTANCE.loginUsuario(usuario, password);
            } catch (Exception ex) {
                // Por si tu Controlador lanza algo
                JOptionPane.showMessageDialog(
                        this,
                        "Error al iniciar sesión: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "Login correcto.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // TODO: abre tu ventana principal aquí si la tienes.
                // new MainWindow().setVisible(true);

                dispose(); // cierra el login
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Usuario o contraseña incorrectos.",
                        "Acceso denegado",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
