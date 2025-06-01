package com.areandina.Interfaz_grafica;

import com.areandina.mysql.MySqlOperations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HistorialPedidos extends JFrame {

    private JTextField txtCedula;
    private JButton btnBuscar;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    // Conexión
    private static final String SERVER = "localhost";
    private static final String DATA_BASE_NAME = "tienda";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";
    private static final MySqlOperations mySqlOperation = new MySqlOperations();

    public HistorialPedidos() {
        setTitle("Historial de Pedidos");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior: búsqueda por cédula
        JPanel panelBusqueda = new JPanel(new FlowLayout());
        panelBusqueda.add(new JLabel("Cédula del cliente:"));
        txtCedula = new JTextField(15);
        panelBusqueda.add(txtCedula);
        btnBuscar = new JButton("Buscar");
        panelBusqueda.add(btnBuscar);

        // Tabla
        String[] columnas = {"ID Pedido", "Fecha", "Total", "Producto", "Cantidad", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaHistorial = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaHistorial);

        // Agregar al frame
        add(panelBusqueda, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Acción buscar
        btnBuscar.addActionListener(this::buscarPedidos);
    }

    private void buscarPedidos(ActionEvent e) {
        String cedulaStr = txtCedula.getText().trim();
        if (cedulaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula del cliente.");
            return;
        }

        int cedula;
        try {
            cedula = Integer.parseInt(cedulaStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cédula debe ser numérica.");
            return;
        }

        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Conectar y ejecutar
        try {
            openConnection();
            String consulta = String.format(
                    "SELECT p.IdPedido, p.FechaPedido, p.TotalPedido, d.NombreProducto, d.Cantidad, d.Subtotal " +
                            "FROM pedido p " +
                            "JOIN detalle_pedido d ON p.IdPedido = d.IdPedido " +
                            "WHERE p.CedulaCliente = %d", cedula
            );

            mySqlOperation.setSqlStatement(consulta);
            mySqlOperation.executeSqlStatement();
            ResultSet rs = mySqlOperation.getResulset();

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("IdPedido"),
                        rs.getString("FechaPedido"),
                        rs.getDouble("TotalPedido"),
                        rs.getString("NombreProducto"),
                        rs.getInt("Cantidad"),
                        rs.getDouble("Subtotal")
                });
            }

            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron pedidos para esa cédula.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar:\n" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    // Métodos de conexión
    public static void openConnection() {
        mySqlOperation.setServer(SERVER);
        mySqlOperation.setDataBaseName(DATA_BASE_NAME);
        mySqlOperation.setUser(USER);
        mySqlOperation.setPassword(PASSWORD);
    }

    public static void closeConnection() {
        mySqlOperation.close();
    }
}
