package com.areandina.Interfaz_grafica;

import com.areandina.mysql.MySqlOperations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Locale;

public class RegistrarPago extends JFrame {

    private JTextField txtIdPedido, txtMonto;
    private JComboBox<String> comboMetodo;
    private JButton btnRegistrar;

    // Datos de conexión
    private static final String SERVER = "localhost";
    private static final String DATA_BASE_NAME = "tienda";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";
    private static final MySqlOperations mySqlOperation = new MySqlOperations();

    public RegistrarPago() {
        setTitle("Registrar Pago");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        // Componentes
        add(new JLabel("ID del Pedido:"));
        txtIdPedido = new JTextField();
        add(txtIdPedido);

        add(new JLabel("Monto Pagado:"));
        txtMonto = new JTextField();
        add(txtMonto);

        add(new JLabel("Método de Pago:"));
        comboMetodo = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        add(comboMetodo);

        btnRegistrar = new JButton("Registrar Pago");
        add(btnRegistrar);

        add(new JLabel()); // espacio vacío

        // Acción del botón
        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarPago();
            }
        });
    }

    private void registrarPago() {
        String idPedidoStr = txtIdPedido.getText().trim();
        String montoStr = txtMonto.getText().trim();
        String metodo = comboMetodo.getSelectedItem().toString();

        if (idPedidoStr.isEmpty() || montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
            return;
        }

        int idPedido;
        double monto;

        try {
            idPedido = Integer.parseInt(idPedidoStr);
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID del pedido y monto deben ser numéricos.");
            return;
        }

        try {
            openConnection();

            String insertPago = String.format(Locale.US,
                    "INSERT INTO tienda.pago (IdPedido, Monto, MetodoPago) VALUES (%d, %.2f, '%s')",
                    idPedido, monto, metodo);

            insertPago(insertPago);

            JOptionPane.showMessageDialog(this, "Pago registrado correctamente.");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar pago:\n" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeConnection();
        }

        limpiarCampos();
    }

    private void limpiarCampos() {
        txtIdPedido.setText("");
        txtMonto.setText("");
        comboMetodo.setSelectedIndex(0);
    }

    public static void openConnection() {
        mySqlOperation.setServer(SERVER);
        mySqlOperation.setDataBaseName(DATA_BASE_NAME);
        mySqlOperation.setUser(USER);
        mySqlOperation.setPassword(PASSWORD);
    }

    public static void insertPago(String sql) throws SQLException {
        mySqlOperation.setSqlStatement(sql);
        mySqlOperation.executeSqlStatementvoid();
    }

    public static void closeConnection() {
        mySqlOperation.close();
    }
}

