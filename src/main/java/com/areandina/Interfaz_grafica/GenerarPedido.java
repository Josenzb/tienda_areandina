package com.areandina.Interfaz_grafica;

import com.areandina.mysql.MySqlOperations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public class GenerarPedido extends JFrame {

    private JTextField txtCedula, txtProducto, txtCantidad, txtPrecio, txtTotal;
    private JButton btnAgregar, btnGuardar;
    private JTable tablaDetalle;
    private DefaultTableModel modeloTabla;

    // Datos de conexión
    private static final String SERVER = "localhost";
    private static final String DATA_BASE_NAME = "tienda";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";
    private static final MySqlOperations mySqlOperation = new MySqlOperations();

    private double totalPedido = 0.0;

    public GenerarPedido() {
        setTitle("Generar Pedido");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de entrada de datos
        JPanel panelDatos = new JPanel(new GridLayout(5, 2, 10, 10));
        panelDatos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelDatos.add(new JLabel("Cédula Cliente:"));
        txtCedula = new JTextField();
        panelDatos.add(txtCedula);

        panelDatos.add(new JLabel("Nombre Producto:"));
        txtProducto = new JTextField();
        panelDatos.add(txtProducto);

        panelDatos.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField();
        panelDatos.add(txtCantidad);

        panelDatos.add(new JLabel("Precio Unitario:"));
        txtPrecio = new JTextField();
        panelDatos.add(txtPrecio);

        btnAgregar = new JButton("Agregar al pedido");
        panelDatos.add(btnAgregar);

        // Tabla para el detalle del pedido
        String[] columnas = {"Producto", "Cantidad", "Precio", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaDetalle = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaDetalle);

        // Panel inferior con total y guardar
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtTotal = new JTextField("0.00", 10);
        txtTotal.setEditable(false);
        btnGuardar = new JButton("Guardar Pedido");

        panelInferior.add(new JLabel("Total:"));
        panelInferior.add(txtTotal);
        panelInferior.add(btnGuardar);

        // Agregar componentes al frame
        add(panelDatos, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // Acción: Agregar producto a la tabla
        btnAgregar.addActionListener(e -> agregarProducto());

        // Acción: Guardar pedido en la BD (estructura preparada, sin conexión aún)
        btnGuardar.addActionListener(e -> guardarPedido());
    }

    private void agregarProducto() {
        String nombre = txtProducto.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();
        String precioStr = txtPrecio.getText().trim();

        if (nombre.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos del producto.");
            return;
        }

        int cantidad;
        double precio;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad y precio deben ser números válidos.");
            return;
        }

        double subtotal = cantidad * precio;
        totalPedido += subtotal;

        modeloTabla.addRow(new Object[]{nombre, cantidad, precio, subtotal});
        txtTotal.setText(String.format("%.2f", totalPedido));

        // Limpiar campos
        txtProducto.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
    }

    private void guardarPedido() {
        String cedulaStr = txtCedula.getText().trim();

        if (cedulaStr.isEmpty() || modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Ingrese cédula y al menos un producto.");
            return;
        }

        int cedula;
        try {
            cedula = Integer.parseInt(cedulaStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cédula debe ser un número.");
            return;
        }


        try {
            openConnection();
            int idPedido = obtenerSiguienteIdPedido();

            // 1. Insertar en pedido
            String insertPedido = String.format(Locale.US,
                    "INSERT INTO tienda.pedido (IdPedido, CedulaCliente, TotalPedido) VALUES (%d, %d, %.2f)",
                    idPedido, cedula, totalPedido);

            insertPedido(insertPedido);


            // 2. Insertar cada fila del detalle
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String producto = modeloTabla.getValueAt(i, 0).toString();
                int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 1).toString());
                double precio = Double.parseDouble(modeloTabla.getValueAt(i, 2).toString());
                double subtotal = Double.parseDouble(modeloTabla.getValueAt(i, 3).toString());

                String insertDetalle = String.format(Locale.US,
                        "INSERT INTO tienda.detalle_pedido (IdPedido, NombreProducto, Cantidad, PrecioUnitario, Subtotal) " +
                                "VALUES (%d, '%s', %d, %.2f, %.2f)",
                        idPedido, producto.replace("'", "''"), cantidad, precio, subtotal);

                insertPedido(insertDetalle); // Reutilizamos el método insertPedido
            }

            JOptionPane.showMessageDialog(this, "Pedido registrado correctamente.");
            modeloTabla.setRowCount(0);
            txtCedula.setText("");
            txtTotal.setText("0.00");
            totalPedido = 0.0;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar pedido:\n" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
        // Limpiar todo
        modeloTabla.setRowCount(0);
        txtCedula.setText("");
        txtTotal.setText("0.00");
        totalPedido = 0.0;
    }

    // Métodos de conexión
    public static void openConnection() {
        mySqlOperation.setServer(SERVER);
        mySqlOperation.setDataBaseName(DATA_BASE_NAME);
        mySqlOperation.setUser(USER);
        mySqlOperation.setPassword(PASSWORD);
    }

    public static void insertPedido(String sql) throws SQLException {
        mySqlOperation.setSqlStatement(sql);
        mySqlOperation.executeSqlStatementvoid();
    }

    public static int obtenerSiguienteIdPedido() throws SQLException {
        mySqlOperation.setSqlStatement("SELECT COUNT(*) FROM tienda.pedido");
        mySqlOperation.executeSqlStatement();
        return mySqlOperation.getColumnInt(1) + 1;
    }


    public static void closeConnection() {
        mySqlOperation.close();
    }
}

