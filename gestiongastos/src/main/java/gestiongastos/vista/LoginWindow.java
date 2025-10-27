package gestiongastos.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import gestiongastos.controlador.Controlador; 

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

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(contentPane);

        // Título
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        contentPane.add(titleLabel, BorderLayout.NORTH);

        // Panel central
        JPanel center = new JPanel(new GridBagLayout());
        contentPane.add(center, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        textUsuario = new JTextField(20);
        JLabel userLabel = new JLabel("Usuario");
        textPassword = new JPasswordField(20);
        JLabel passwordLabel = new JLabel("Contraseña");

        gbc.gridx = 0; gbc.gridy = 0; center.add(userLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1; center.add(textUsuario, gbc);
        gbc.gridx = 0; gbc.gridy = 2; center.add(passwordLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 3; center.add(textPassword, gbc);

        // Panel inferior con botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS));
        contentPane.add(panelBotones, BorderLayout.SOUTH);

        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");
        btnAceptar = new JButton("Aceptar");

        panelBotones.add(btnRegistrar);
        panelBotones.add(Box.createHorizontalGlue());
        panelBotones.add(btnCancelar);
        panelBotones.add(Box.createHorizontalStrut(8));
        panelBotones.add(btnAceptar);

        getRootPane().setDefaultButton(btnAceptar);

        // Listeners
        btnRegistrar.addActionListener(e -> {
            RegisterWindow registro = new RegisterWindow();
            registro.setLocationRelativeTo(this);
            registro.setVisible(true);
        });

        btnCancelar.addActionListener(e -> dispose()); // o System.exit(0) si quieres cerrar app

        btnAceptar.addActionListener(e -> {
            String usuario = textUsuario.getText().trim();
            String password = new String(textPassword.getPassword());

            if (usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduce usuario y contraseña.",
                        "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                boolean ok = Controlador.getInstance().loginUsuario(usuario, password);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Login correcto.", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    // TODO: abre tu ventana principal:
                    // new MainWindow().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.",
                            "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al iniciar sesión: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
