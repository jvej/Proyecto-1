package vista;

import controlador.LoginControlador;
import modelo.Usuario;
import util.Sesion;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtId;
    private JPasswordField txtClave;

    private final LoginControlador controlador = new LoginControlador();

    public LoginFrame() {

        setTitle("Sistema de Reservas - Iniciar Sesión");

        setSize(380, 300);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setResizable(false);

        JPanel panel = new JPanel();

        panel.setLayout(null);

        TemaOscuro.aplicarAPanel(panel);

        add(panel);

        // TÍTULO


        JLabel lblTitulo = new JLabel("SISTEMA DE RESERVAS - LOGIN");

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));

        TemaOscuro.aplicarAEtiqueta(lblTitulo);

        lblTitulo.setBounds(20, 15, 340, 30);

        panel.add(lblTitulo);

        // ID

        JLabel lblId = new JLabel("ID:");

        TemaOscuro.aplicarAEtiqueta(lblId);

        lblId.setBounds(30, 70, 100, 25);

        panel.add(lblId);

        txtId = new JTextField();

        txtId.setBounds(140, 70, 190, 25);
        TemaOscuro.aplicarACampoTexto(txtId);
        panel.add(txtId);

        // CLAVE

        JLabel lblClave = new JLabel("Clave:");

        TemaOscuro.aplicarAEtiqueta(lblClave);

        lblClave.setBounds(30, 110, 100, 25);
        panel.add(lblClave);

        txtClave = new JPasswordField();

        txtClave.setBounds(140, 110, 190, 25);
        TemaOscuro.aplicarACampoTexto(txtClave);
        panel.add(txtClave);

        JButton btnIngresar = new JButton(" ✅ ");

        btnIngresar.setBounds(50, 165, 110, 30);
        TemaOscuro.aplicarABoton(btnIngresar);
        panel.add(btnIngresar);

        JButton btnLimpiar = new JButton(" 🔄 ");

        btnLimpiar.setBounds(210, 165, 110, 30);
        TemaOscuro.aplicarABoton(btnLimpiar);
        panel.add(btnLimpiar);


        JButton btnCambiarClave = new JButton(" 🔑 ");

        btnCambiarClave.setBounds(80, 205, 210,30);
        TemaOscuro.aplicarABoton(btnCambiarClave);
        panel.add(btnCambiarClave);

        btnIngresar.addActionListener(e -> iniciarSesion());

        btnLimpiar.addActionListener(e -> limpiarCampos());

        btnCambiarClave.addActionListener(e -> new CambiarClavePanel(this).setVisible(true));
    }

    // INICIAR SESIÓN

    private void iniciarSesion() {

        try {

            String id = txtId.getText().trim();

            String clave = new String(txtClave.getPassword()).trim();

            // Validar campos
            if (id.isEmpty() || clave.isEmpty()) {

                throw new Exception("Debe completar el ID y la clave.");
            }

            // AUTENTICAR USUARIO

            Usuario u = controlador.login(id, clave);

            // VALIDAR RESULTADO

            if (u == null) {

                throw new Exception("ID o clave incorrectos.");
            }

            // GUARDAR USUARIO EN LA SESIÓN

            Sesion.getInstancia().setUsuarioActual(u);

            // ABRIR MENÚ PRINCIPAL

            MainFrame menu = new MainFrame();

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

    // LIMPIAR CAMPOS

    private void limpiarCampos() {

        txtId.setText("");

        txtClave.setText("");
    }
}