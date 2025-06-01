package com.areandina.Interfaz_grafica;

import javax.swing.*;
import java.awt.event.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Sistema de Tienda - Menú Principal");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JButton btnCliente = new JButton("Registrar Cliente");
        btnCliente.setBounds(100, 30, 200, 30);
        add(btnCliente);

        JButton btnPedido = new JButton("Generar Pedido");
        btnPedido.setBounds(100, 80, 200, 30);
        add(btnPedido);

        JButton btnPago = new JButton("Registrar Pago");
        btnPago.setBounds(100, 130, 200, 30);
        add(btnPago);

        JButton btnHistorial = new JButton("Historial de Pedidos");
        btnHistorial.setBounds(100, 180, 200, 30);
        add(btnHistorial);

        // Eventos de los botones
        btnCliente.addActionListener(e -> new RegistroCliente().setVisible(true));
        btnPedido.addActionListener(e -> new GenerarPedido().setVisible(true));
        btnPago.addActionListener(e -> new RegistrarPago().setVisible(true));
        btnHistorial.addActionListener(e -> new HistorialPedidos().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}
