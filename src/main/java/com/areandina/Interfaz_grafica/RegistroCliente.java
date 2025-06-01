package com.areandina.Interfaz_grafica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import com.areandina.mysql.MySqlOperations;

public class RegistroCliente extends JFrame {

    // Datos de conexión
    private static final String SERVER = "localhost";
    private static final String DATA_BASE_NAME = "tienda";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";
    private static final MySqlOperations mySqlOperation = new MySqlOperations();

    // Componentes de la interfaz
    private JTextField txtCedula, txtNombre, txtEmail, txtDireccion, txtTelefono, txtCiudad;
    private JButton btnGuardar;

    public RegistroCliente() {
        setTitle("Registro de Cliente");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(8, 2, 10, 10));

        // Crear y agregar componentes
        add(new JLabel("Cédula:"));
        txtCedula = new JTextField();
        add(txtCedula);

        add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        add(txtNombre);

        add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        add(txtDireccion);

        add(new JLabel("Email:"));
        txtEmail = new JTextField();
        add(txtEmail);

        add(new JLabel("Ciudad:"));
        txtCiudad = new JTextField();
        add(txtCiudad);

        add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        add(txtTelefono);

        btnGuardar = new JButton("Guardar");
        add(btnGuardar);

        add(new JLabel()); // Espacio vacío

        // Acción del botón
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarCliente();
            }
        });
    }

    private void guardarCliente() {
        // Obtener datos
        String cedulaStr = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String email = txtEmail.getText().trim();
        String ciudad = txtCiudad.getText().trim();
        String telefono = txtTelefono.getText().trim();

        // Validación básica
        if (cedulaStr.isEmpty() || nombre.isEmpty() || direccion.isEmpty() ||
                email.isEmpty() || ciudad.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
            return;
        }

        int cedula;
        try {
            cedula = Integer.parseInt(cedulaStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cédula debe ser un número.");
            return;
        }

        // Construir SQL (comillas simples para los datos de tipo texto)
        String insertCliente = String.format(
                "INSERT INTO tienda.clientes (CedulaCliente, NombreCliente, DireccionCliente, EmailCliente, CiudadCliente, TelefonoCliente) " +
                        "VALUES (%d, '%s', '%s', '%s', '%s', '%s')",
                cedula, nombre, direccion, email, ciudad, telefono
        );

        // Ejecutar inserción con control de errores
        try {
            openConnection();
            insertCliente(insertCliente);
            JOptionPane.showMessageDialog(this, "Cliente registrado correctamente.");
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar cliente:\n" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void limpiarCampos() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtEmail.setText("");
        txtCiudad.setText("");
        txtTelefono.setText("");
    }

    // Métodos de conexión
    public static void openConnection() {
        mySqlOperation.setServer(SERVER);
        mySqlOperation.setDataBaseName(DATA_BASE_NAME);
        mySqlOperation.setUser(USER);
        mySqlOperation.setPassword(PASSWORD);
    }

    public static void insertCliente(String sql) throws SQLException {
        mySqlOperation.setSqlStatement(sql);
        mySqlOperation.executeSqlStatementvoid();
    }

    public static void closeConnection() {
        mySqlOperation.close();
    }
}



