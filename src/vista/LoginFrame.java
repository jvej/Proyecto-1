package vista;

import controlador.LoginControlador;
import modelo.Usuario;
import util.Sesion;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtId;
    private JPasswordField txtClave;

    private final LoginControlador controlador =
            new LoginControlador();

    public LoginFrame() {

        setTitle(
                "Sistema de Reservas - Iniciar Sesión"
        );

        setSize(380, 300);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setResizable(false);

        JPanel panel =
                new JPanel();

        panel.setLayout(null);

        panel.setBackground(Color.black);

        add(panel);

        // ==========================================
        // TÍTULO
        // ==========================================

        JLabel lblTitulo =
                new JLabel(
                        "SISTEMA DE RESERVAS - LOGIN"
                );

        lblTitulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        lblTitulo.setForeground(
                Color.white
        );

        lblTitulo.setBounds(
                20,
                15,
                340,
                30
        );

        panel.add(lblTitulo);

        // ==========================================
        // ID
        // ==========================================

        JLabel lblId =
                new JLabel("ID:");

        lblId.setForeground(
                Color.white
        );

        lblId.setBounds(
                30,
                70,
                100,
                25
        );

        panel.add(lblId);

        txtId =
                new JTextField();

        txtId.setBounds(
                140,
                70,
                190,
                25
        );

        panel.add(txtId);

        // ==========================================
        // CLAVE
        // ==========================================

        JLabel lblClave =
                new JLabel("Clave:");

        lblClave.setForeground(
                Color.white
        );

        lblClave.setBounds(
                30,
                110,
                100,
                25
        );

        panel.add(lblClave);

        txtClave =
                new JPasswordField();

        txtClave.setBounds(
                140,
                110,
                190,
                25
        );

        panel.add(txtClave);

        // ==========================================
        // BOTÓN INGRESAR
        // ==========================================

        JButton btnIngresar =
                new JButton("✅ Ingresar");

        btnIngresar.setBounds(
                30,
                165,
                150,
                30
        );

        panel.add(btnIngresar);

        // ==========================================
        // BOTÓN LIMPIAR
        // ==========================================

        JButton btnLimpiar =
                new JButton("🔄 Limpiar");

        btnLimpiar.setBounds(
                200,
                165,
                140,
                30
        );

        panel.add(btnLimpiar);

        // ==========================================
        // BOTÓN CAMBIAR CLAVE
        // ==========================================

        JButton btnCambiarClave =
                new JButton("🔑 Cambiar clave");

        btnCambiarClave.setBounds(
                30,
                205,
                310,
                30
        );

        panel.add(btnCambiarClave);

        // ==========================================
        // EVENTOS
        // ==========================================

        btnIngresar.addActionListener(
                e -> iniciarSesion()
        );

        btnLimpiar.addActionListener(
                e -> limpiarCampos()
        );

        btnCambiarClave.addActionListener(
                e ->
                        new CambiarClavePanel(this)
                                .setVisible(true)
        );
    }

    // ==============================================
    // INICIAR SESIÓN
    // ==============================================

    private void iniciarSesion() {

        try {

            String id = txtId.getText().trim();

            String clave = new String(txtClave.getPassword()).trim();

            // Validar campos
            if (id.isEmpty() || clave.isEmpty()) {

                throw new Exception("Debe completar el ID y la clave.");
            }

            // ======================================
            // AUTENTICAR USUARIO
            // ======================================

            Usuario u = controlador.login(id, clave);

            // ======================================
            // VALIDAR RESULTADO
            // ======================================

            if (u == null) {

                throw new Exception(
                        "ID o clave incorrectos."
                );
            }

            // ======================================
            // GUARDAR USUARIO EN LA SESIÓN
            // ======================================

            Sesion.getInstancia().setUsuarioActual(u);

            // ======================================
            // ABRIR MENÚ PRINCIPAL
            // ======================================

            MainFrame menu =
                    new MainFrame();

            menu.setVisible(true);

            // Cerramos la ventana de login
            this.dispose();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error de autenticación",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==============================================
    // LIMPIAR CAMPOS
    // ==============================================

    private void limpiarCampos() {

        txtId.setText("");

        txtClave.setText("");
    }
}