package vista;
import controlador.LoginControlador;
import modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtId;
    private JPasswordField txtClave;

    private final LoginControlador controlador = new LoginControlador();

    public LoginFrame() {
        setTitle("Sistema de Reservas - Iniciar Sesión");
        setSize(380, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.black);
        add(panel);

        // TÍTULO
        JLabel lblTitulo = new JLabel("SISTEMA DE RESERVAS - LOGIN");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitulo.setForeground(Color.white);
        lblTitulo.setBounds(20, 15, 340, 30);
        panel.add(lblTitulo);

        // TEXTO "ID"
        JLabel lblId = new JLabel("ID:");
        lblId.setForeground(Color.white);
        lblId.setBounds(30, 70, 100, 25);
        panel.add(lblId);

        // CAJA PARA EL ID
        txtId = new JTextField();
        txtId.setBounds(140, 70, 190, 25);
        panel.add(txtId);

        // TEXTO "CLAVE"
        JLabel lblClave = new JLabel("Clave:");
        lblClave.setForeground(Color.white);
        lblClave.setBounds(30, 110, 100, 25);
        panel.add(lblClave);

        // CAJA PARA LA CLAVE
        txtClave = new JPasswordField();
        txtClave.setBounds(140, 110, 190, 25);
        panel.add(txtClave);

        // BOTÓN INGRESAR
        JButton btnIngresar = new JButton("✅ Ingresar");
        btnIngresar.setBounds(30, 165, 150, 30);
        panel.add(btnIngresar);

        // BOTÓN LIMPIAR
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        btnLimpiar.setBounds(200, 165, 140, 30);
        panel.add(btnLimpiar);

        // BOTÓN CAMBIAR CLAVE
        JButton btnCambiarClave = new JButton("🔑 Cambiar clave");
        btnCambiarClave.setBounds(30, 205, 310, 30);
        panel.add(btnCambiarClave);

        // EVENTOS
        btnIngresar.addActionListener(e -> iniciarSesion());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnCambiarClave.addActionListener(e -> new CambiarClavePanel(this).setVisible(true));
    }

    private void iniciarSesion() {
        try {
            String id = txtId.getText().trim();
            String clave = new String(txtClave.getPassword()).trim();

            if (id.isEmpty() || clave.isEmpty()) {
                throw new Exception("Debe completar el ID y la clave.");
            }

            // El controlador busca al usuario en el XML a través del DAO.
            Usuario u = controlador.login(id, clave);

            if (u == null) {
                throw new Exception("ID o clave incorrectos.");
            }

            MainFrame menu = new MainFrame();
            menu.setVisible(true);
            this.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtClave.setText("");
    }
}