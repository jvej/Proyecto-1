package vista;
import controlador.LoginControlador;
import javax.swing.*;
import java.awt.*;

public class CambiarClavePanel extends JDialog {

    private JTextField txtId;
    private JPasswordField txtActual;
    private JPasswordField txtNueva1;
    private JPasswordField txtNueva2;

    public CambiarClavePanel(JFrame padre) {
        super(padre, "Cambiar Clave", true);
        setSize(340, 280);
        setLocationRelativeTo(padre);
        setResizable(false);

        LoginControlador controlador = new LoginControlador();

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.black);
        add(panel);

        JLabel lblId = new JLabel("ID:");
        lblId.setForeground(Color.white);
        lblId.setBounds(25, 20, 100, 25);
        panel.add(lblId);

        txtId = new JTextField();
        txtId.setBounds(140, 20, 160, 25);
        panel.add(txtId);

        JLabel lblActual = new JLabel("Clave actual:");
        lblActual.setForeground(Color.white);
        lblActual.setBounds(25, 60, 110, 25);
        panel.add(lblActual);

        txtActual = new JPasswordField();
        txtActual.setBounds(140, 60, 160, 25);
        panel.add(txtActual);

        JLabel lblNueva1 = new JLabel("Clave nueva:");
        lblNueva1.setForeground(Color.white);
        lblNueva1.setBounds(25, 100, 110, 25);
        panel.add(lblNueva1);

        txtNueva1 = new JPasswordField();
        txtNueva1.setBounds(140, 100, 160, 25);
        panel.add(txtNueva1);

        JLabel lblNueva2 = new JLabel("Repetir clave:");
        lblNueva2.setForeground(Color.white);
        lblNueva2.setBounds(25, 140, 110, 25);
        panel.add(lblNueva2);

        txtNueva2 = new JPasswordField();
        txtNueva2.setBounds(140, 140, 160, 25);
        panel.add(txtNueva2);

        JButton btnAceptar = new JButton("✅ Aceptar");
        btnAceptar.setBounds(25, 190, 130, 30);
        panel.add(btnAceptar);

        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setBounds(170, 190, 130, 30);
        panel.add(btnCancelar);

        btnAceptar.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String actual = new String(txtActual.getPassword()).trim();
                String nueva1 = new String(txtNueva1.getPassword()).trim();
                String nueva2 = new String(txtNueva2.getPassword()).trim();

                if (id.isEmpty() || actual.isEmpty() || nueva1.isEmpty()) {
                    throw new Exception("Complete todos los campos.");
                }
                if (!nueva1.equals(nueva2)) {
                    throw new Exception("Las claves nuevas no coinciden.");
                }

                boolean ok = controlador.cambiarClave(id, actual, nueva1);
                if (!ok) {
                    throw new Exception("ID o clave actual incorrectos.");
                }

                JOptionPane.showMessageDialog(this, "Clave actualizada correctamente.");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose());
    }
}