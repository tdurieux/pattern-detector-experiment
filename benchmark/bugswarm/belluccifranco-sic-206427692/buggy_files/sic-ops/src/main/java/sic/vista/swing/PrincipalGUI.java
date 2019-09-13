package sic.vista.swing;

import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Caja;
import sic.modelo.Empresa;
import sic.modelo.EmpresaActiva;
import sic.modelo.UsuarioActivo;
import sic.modelo.EstadoCaja;
import sic.util.Utilidades;

public class PrincipalGUI extends JFrame {
    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public PrincipalGUI() {
        this.initComponents();
        ImageIcon iconoVentana = new ImageIcon(PrincipalGUI.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
        this.setTitle("S.I.C. Sistema de Información Comercial "
                + ResourceBundle.getBundle("Mensajes").getString("version"));
        this.setSize(new Dimension(1000, 720));
        this.setExtendedState(MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
    }

    public JDesktopPane getDesktopPane() {
        return dp_Escritorio;
    }

    private void llamarGUI_SeleccionEmpresa() {
        List<Empresa> empresas = Arrays.asList(RestClient.getRestTemplate().getForObject("/empresas", Empresa[].class));
        if (empresas.isEmpty() || empresas.size() > 1) {
            SeleccionEmpresaGUI gui_seleccionEmpresa = new SeleccionEmpresaGUI(this, empresas);
            gui_seleccionEmpresa.setLocationRelativeTo(this);
            gui_seleccionEmpresa.setVisible(true);
            gui_seleccionEmpresa.dispose();
        } else {
            EmpresaActiva.getInstance().setEmpresa(empresas.get(0));
        }
        Empresa empresa = EmpresaActiva.getInstance().getEmpresa();
        if (empresa == null) {
            lbl_EmpresaActiva.setText("Empresa: (sin empresa)");
        } else {
            lbl_EmpresaActiva.setText("Empresa: " + empresa.getNombre());
        }     
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_EmpresaActiva = new javax.swing.JLabel();
        lbl_UsuarioActivo = new javax.swing.JLabel();
        lbl_Separador = new javax.swing.JLabel();
        dp_Escritorio = new javax.swing.JDesktopPane();
        mb_BarraMenues = new javax.swing.JMenuBar();
        mnu_Sistema = new javax.swing.JMenu();
        mnuItm_IrTPV = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuItm_Empresas = new javax.swing.JMenuItem();
        mnuItm_CambiarEmpresa = new javax.swing.JMenuItem();
        Separador1 = new javax.swing.JPopupMenu.Separator();
        mnuItm_Usuarios = new javax.swing.JMenuItem();
        mnuItm_CambiarUser = new javax.swing.JMenuItem();
        Separador2 = new javax.swing.JPopupMenu.Separator();
        mnuItm_Configuracion = new javax.swing.JMenuItem();
        mnuItm_Salir = new javax.swing.JMenuItem();
        mnu_Compras = new javax.swing.JMenu();
        mnuItm_FacturasCompra = new javax.swing.JMenuItem();
        mnuItm_Proveedores = new javax.swing.JMenuItem();
        mnu_Ventas = new javax.swing.JMenu();
        mnuItm_FacturasVenta = new javax.swing.JMenuItem();
        mnuItm_Pedidos = new javax.swing.JMenuItem();
        mnuItm_Clientes = new javax.swing.JMenuItem();
        mnu_Administracion = new javax.swing.JMenu();
        mnuItm_Transportistas = new javax.swing.JMenuItem();
        mnuItm_FormasDePago = new javax.swing.JMenuItem();
        mnu_Cajas = new javax.swing.JMenuItem();
        mnu_Stock = new javax.swing.JMenu();
        mnuItm_Productos = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lbl_EmpresaActiva.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        lbl_EmpresaActiva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Empresa_16x16.png"))); // NOI18N
        lbl_EmpresaActiva.setText("Empresa: (sin empresa)");
        lbl_EmpresaActiva.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_UsuarioActivo.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        lbl_UsuarioActivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Group_16x16.png"))); // NOI18N
        lbl_UsuarioActivo.setText("Usuario: (sin usuario)");
        lbl_UsuarioActivo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_Separador.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        lbl_Separador.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        dp_Escritorio.setBackground(new java.awt.Color(204, 204, 204));
        dp_Escritorio.setToolTipText("");

        mnu_Sistema.setText("Sistema");

        mnuItm_IrTPV.setText("Ir a Punto de Venta");
        mnuItm_IrTPV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_IrTPVActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_IrTPV);
        mnu_Sistema.add(jSeparator1);

        mnuItm_Empresas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Empresa_16x16.png"))); // NOI18N
        mnuItm_Empresas.setText("Empresas");
        mnuItm_Empresas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_EmpresasActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_Empresas);

        mnuItm_CambiarEmpresa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EmpresaGo_16x16.png"))); // NOI18N
        mnuItm_CambiarEmpresa.setText("Cambiar Empresa");
        mnuItm_CambiarEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_CambiarEmpresaActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_CambiarEmpresa);
        mnu_Sistema.add(Separador1);

        mnuItm_Usuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Group_16x16.png"))); // NOI18N
        mnuItm_Usuarios.setText("Usuarios");
        mnuItm_Usuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_UsuariosActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_Usuarios);

        mnuItm_CambiarUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/GroupArrow_16x16.png"))); // NOI18N
        mnuItm_CambiarUser.setText("Cambiar Usuario");
        mnuItm_CambiarUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_CambiarUserActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_CambiarUser);
        mnu_Sistema.add(Separador2);

        mnuItm_Configuracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Gears_16x16.png"))); // NOI18N
        mnuItm_Configuracion.setText("Configuración");
        mnuItm_Configuracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_ConfiguracionActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_Configuracion);

        mnuItm_Salir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/LogOut_16x16.png"))); // NOI18N
        mnuItm_Salir.setText("Salir");
        mnuItm_Salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_SalirActionPerformed(evt);
            }
        });
        mnu_Sistema.add(mnuItm_Salir);

        mb_BarraMenues.add(mnu_Sistema);

        mnu_Compras.setText("Compras");

        mnuItm_FacturasCompra.setText("Facturas");
        mnuItm_FacturasCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_FacturasCompraActionPerformed(evt);
            }
        });
        mnu_Compras.add(mnuItm_FacturasCompra);

        mnuItm_Proveedores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/ProviderBag_16x16.png"))); // NOI18N
        mnuItm_Proveedores.setText("Proveedores");
        mnuItm_Proveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_ProveedoresActionPerformed(evt);
            }
        });
        mnu_Compras.add(mnuItm_Proveedores);

        mb_BarraMenues.add(mnu_Compras);

        mnu_Ventas.setText("Ventas");

        mnuItm_FacturasVenta.setText("Facturas");
        mnuItm_FacturasVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_FacturasVentaActionPerformed(evt);
            }
        });
        mnu_Ventas.add(mnuItm_FacturasVenta);

        mnuItm_Pedidos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/PedidoFacturar_16x16.png"))); // NOI18N
        mnuItm_Pedidos.setText("Pedidos");
        mnuItm_Pedidos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_PedidosActionPerformed(evt);
            }
        });
        mnu_Ventas.add(mnuItm_Pedidos);

        mnuItm_Clientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Client_16x16.png"))); // NOI18N
        mnuItm_Clientes.setText("Clientes");
        mnuItm_Clientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_ClientesActionPerformed(evt);
            }
        });
        mnu_Ventas.add(mnuItm_Clientes);

        mb_BarraMenues.add(mnu_Ventas);

        mnu_Administracion.setText("Administración");

        mnuItm_Transportistas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Truck_16x16.png"))); // NOI18N
        mnuItm_Transportistas.setText("Transportistas");
        mnuItm_Transportistas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_TransportistasActionPerformed(evt);
            }
        });
        mnu_Administracion.add(mnuItm_Transportistas);

        mnuItm_FormasDePago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Wallet_16x16.png"))); // NOI18N
        mnuItm_FormasDePago.setText("Formas de Pago");
        mnuItm_FormasDePago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_FormasDePagoActionPerformed(evt);
            }
        });
        mnu_Administracion.add(mnuItm_FormasDePago);

        mnu_Cajas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Caja_16x16.png"))); // NOI18N
        mnu_Cajas.setText("Cajas");
        mnu_Cajas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnu_CajasActionPerformed(evt);
            }
        });
        mnu_Administracion.add(mnu_Cajas);

        mb_BarraMenues.add(mnu_Administracion);

        mnu_Stock.setText("Stock");

        mnuItm_Productos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Product_16x16.png"))); // NOI18N
        mnuItm_Productos.setText("Productos");
        mnuItm_Productos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItm_ProductosActionPerformed(evt);
            }
        });
        mnu_Stock.add(mnuItm_Productos);

        mb_BarraMenues.add(mnu_Stock);

        setJMenuBar(mb_BarraMenues);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbl_UsuarioActivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_EmpresaActiva)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Separador, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE))
            .addComponent(dp_Escritorio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 851, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(dp_Escritorio, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_UsuarioActivo)
                    .addComponent(lbl_Separador, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_EmpresaActiva)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuItm_SalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_SalirActionPerformed
        this.formWindowClosing(null);
    }//GEN-LAST:event_mnuItm_SalirActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int respuesta = JOptionPane.showConfirmDialog(this,
                ResourceBundle.getBundle("Mensajes").getString("mensaje_confirmacion_salir_sistema"),
                ResourceBundle.getBundle("Mensajes").getString("mensaje_salir"), JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void mnuItm_EmpresasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_EmpresasActionPerformed
        EmpresasGUI gui_Empresas = new EmpresasGUI();
        gui_Empresas.setModal(true);
        gui_Empresas.setLocationRelativeTo(this);
        gui_Empresas.setVisible(true);
        Utilidades.cerrarTodasVentanas(dp_Escritorio);
        this.llamarGUI_SeleccionEmpresa();
    }//GEN-LAST:event_mnuItm_EmpresasActionPerformed

    private void mnuItm_CambiarUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_CambiarUserActionPerformed
        UsuarioActivo.getInstance().setToken("");
        UsuarioActivo.getInstance().setUsuario(null);
        this.dispose();
        new LoginGUI().setVisible(true);
    }//GEN-LAST:event_mnuItm_CambiarUserActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        lbl_UsuarioActivo.setText("Usuario: " + UsuarioActivo.getInstance().getUsuario().getNombre());       
        this.llamarGUI_SeleccionEmpresa();        
    }//GEN-LAST:event_formWindowOpened

    private void mnuItm_UsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_UsuariosActionPerformed
        UsuariosGUI gui_Usuarios = new UsuariosGUI();
        gui_Usuarios.setModal(true);
        gui_Usuarios.setLocationRelativeTo(this);
        gui_Usuarios.setVisible(true);
    }//GEN-LAST:event_mnuItm_UsuariosActionPerformed

    private void mnuItm_ProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_ProveedoresActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), ProveedoresGUI.class);
        if (gui == null) {
            gui = new ProveedoresGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            //selecciona y trae al frente el internalframe
            try {
                gui.setSelected(true);

            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_ProveedoresActionPerformed

    private void mnuItm_FacturasCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_FacturasCompraActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), FacturasCompraGUI.class);
        if (gui == null) {
            gui = new FacturasCompraGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            //selecciona y trae al frente el internalframe
            try {
                gui.setSelected(true);

            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_FacturasCompraActionPerformed

    private void mnuItm_TransportistasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_TransportistasActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), TransportistasGUI.class);
        if (gui == null) {
            gui = new TransportistasGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            //selecciona y trae al frente el internalframe
            try {
                gui.setSelected(true);

            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_TransportistasActionPerformed

    private void mnuItm_ProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_ProductosActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), ProductosGUI.class);
        if (gui == null) {
            ProductosGUI productos = new ProductosGUI();
            productos.setLocation(getDesktopPane().getWidth() / 2 - productos.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - productos.getHeight() / 2);
            getDesktopPane().add(productos);
            productos.setVisible(true);
        } else {
            //selecciona y trae al frente el internalframe
            try {
                gui.setSelected(true);

            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_ProductosActionPerformed

    private void mnuItm_ClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_ClientesActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), ClientesGUI.class);
        if (gui == null) {
            gui = new ClientesGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            //selecciona y trae al frente el internalframe
            try {
                gui.setSelected(true);

            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_ClientesActionPerformed

    private void mnuItm_CambiarEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_CambiarEmpresaActionPerformed
        Utilidades.cerrarTodasVentanas(dp_Escritorio);
        List<Empresa> empresas = Arrays.asList(RestClient.getRestTemplate().getForObject("/empresas", Empresa[].class));
        SeleccionEmpresaGUI gui_seleccionEmpresa = new SeleccionEmpresaGUI(this, empresas);
        gui_seleccionEmpresa.setLocationRelativeTo(this);
        gui_seleccionEmpresa.setVisible(true);
        gui_seleccionEmpresa.dispose();
    }//GEN-LAST:event_mnuItm_CambiarEmpresaActionPerformed

    private void mnuItm_FormasDePagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_FormasDePagoActionPerformed
        FormasDePagoGUI gui_FormasDePago = new FormasDePagoGUI();
        gui_FormasDePago.setModal(true);
        gui_FormasDePago.setLocationRelativeTo(this);
        gui_FormasDePago.setVisible(true);
    }//GEN-LAST:event_mnuItm_FormasDePagoActionPerformed

    private void mnuItm_IrTPVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_IrTPVActionPerformed
        PuntoDeVentaGUI gui_puntoDeVenta = new PuntoDeVentaGUI();
        gui_puntoDeVenta.setModal(true);
        gui_puntoDeVenta.setLocationRelativeTo(this);
        gui_puntoDeVenta.setVisible(true);
    }//GEN-LAST:event_mnuItm_IrTPVActionPerformed

    private void mnuItm_FacturasVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_FacturasVentaActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), FacturasVentaGUI.class);
        if (gui == null) {
            gui = new FacturasVentaGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            //selecciona y trae al frente el internalframe
            try {
                gui.setSelected(true);

            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_FacturasVentaActionPerformed

    private void mnuItm_ConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_ConfiguracionActionPerformed
        ConfiguracionDelSistemaGUI gui_ConfiguracionDelSistema = new ConfiguracionDelSistemaGUI();
        gui_ConfiguracionDelSistema.setModal(true);
        gui_ConfiguracionDelSistema.setLocationRelativeTo(this);
        gui_ConfiguracionDelSistema.setVisible(true);
        Utilidades.cerrarTodasVentanas(dp_Escritorio);
    }//GEN-LAST:event_mnuItm_ConfiguracionActionPerformed

    private void mnuItm_PedidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItm_PedidosActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), PedidosGUI.class);
        if (gui == null) {
            gui = new PedidosGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
        } else {
            //selecciona y trae al frente el internalframe
            try {
                gui.setSelected(true);

            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuItm_PedidosActionPerformed

    private void mnu_CajasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnu_CajasActionPerformed
        JInternalFrame gui = Utilidades.estaEnDesktop(getDesktopPane(), CajasGUI.class);
        if (gui == null) {
            gui = new CajasGUI();
            gui.setLocation(getDesktopPane().getWidth() / 2 - gui.getWidth() / 2,
                    getDesktopPane().getHeight() / 2 - gui.getHeight() / 2);
            getDesktopPane().add(gui);
            gui.setVisible(true);
            try {
                Caja caja = RestClient.getRestTemplate().getForObject("/cajas/empresas/"
                        + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                        + "/ultima", Caja.class);
                if (caja != null) {
                    if (caja.getEstado() == EstadoCaja.ABIERTA) {
                        CajaGUI cajaAbierta = new CajaGUI(caja);
                        cajaAbierta.setModal(true);
                        cajaAbierta.setLocationRelativeTo(this);
                        cajaAbierta.setVisible(true);
                    }
                }
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            //selecciona y trae al frente el internalframe
            try {
                gui.setSelected(true);

            } catch (PropertyVetoException ex) {
                String msjError = "No se pudo seleccionar la ventana requerida.";
                LOGGER.error(msjError + " - " + ex.getMessage());
                JOptionPane.showInternalMessageDialog(this.getDesktopPane(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnu_CajasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu.Separator Separador1;
    private javax.swing.JPopupMenu.Separator Separador2;
    private javax.swing.JDesktopPane dp_Escritorio;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel lbl_EmpresaActiva;
    private javax.swing.JLabel lbl_Separador;
    private javax.swing.JLabel lbl_UsuarioActivo;
    private javax.swing.JMenuBar mb_BarraMenues;
    private javax.swing.JMenuItem mnuItm_CambiarEmpresa;
    private javax.swing.JMenuItem mnuItm_CambiarUser;
    private javax.swing.JMenuItem mnuItm_Clientes;
    private javax.swing.JMenuItem mnuItm_Configuracion;
    private javax.swing.JMenuItem mnuItm_Empresas;
    private javax.swing.JMenuItem mnuItm_FacturasCompra;
    private javax.swing.JMenuItem mnuItm_FacturasVenta;
    private javax.swing.JMenuItem mnuItm_FormasDePago;
    private javax.swing.JMenuItem mnuItm_IrTPV;
    private javax.swing.JMenuItem mnuItm_Pedidos;
    private javax.swing.JMenuItem mnuItm_Productos;
    private javax.swing.JMenuItem mnuItm_Proveedores;
    private javax.swing.JMenuItem mnuItm_Salir;
    private javax.swing.JMenuItem mnuItm_Transportistas;
    private javax.swing.JMenuItem mnuItm_Usuarios;
    private javax.swing.JMenu mnu_Administracion;
    private javax.swing.JMenuItem mnu_Cajas;
    private javax.swing.JMenu mnu_Compras;
    private javax.swing.JMenu mnu_Sistema;
    private javax.swing.JMenu mnu_Stock;
    private javax.swing.JMenu mnu_Ventas;
    // End of variables declaration//GEN-END:variables
}
