package sic.vista.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.Factura;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Pago;
import sic.modelo.Pedido;
import sic.modelo.RenglonFactura;
import sic.modelo.Transportista;
import sic.service.EstadoPedido;
import sic.service.IFacturaService;
import sic.service.IFormaDePagoService;
import sic.service.IPagoService;
import sic.service.IPedidoService;
import sic.service.ITransportistaService;
import sic.service.IUsuarioService;
import sic.service.BusinessServiceException;
import sic.service.TipoDeOperacion;

public class GUI_CerrarVenta extends JDialog {

    private boolean exito;
    private final GUI_PuntoDeVenta gui_puntoDeVenta;
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IFormaDePagoService formaDePagoService = appContext.getBean(IFormaDePagoService.class);
    private final ITransportistaService transportistaService = appContext.getBean(ITransportistaService.class);
    private final IFacturaService facturaService = appContext.getBean(IFacturaService.class);
    private final IUsuarioService usuarioService = appContext.getBean(IUsuarioService.class);
    private final IPedidoService pedidoService = appContext.getBean(IPedidoService.class);
    private final IPagoService pagoService = appContext.getBean(IPagoService.class);
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private static final Logger LOGGER = Logger.getLogger(GUI_CerrarVenta.class.getPackage().getName());

    public GUI_CerrarVenta(JDialog parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.setIcon();
        this.setLocationRelativeTo(null);
        this.gui_puntoDeVenta = (GUI_PuntoDeVenta) parent;
        lbl_Vendedor.setText("");
        lbl_TotalAPagar.setValue(gui_puntoDeVenta.getResultadosFactura().getTotal());
        lbl_Vuelto.setValue(0);
        txt_MontoPago1.setValue(gui_puntoDeVenta.getResultadosFactura().getTotal());

        //listeners
        cmb_FormaDePago3.addKeyListener(keyHandler);
        cmb_Transporte.addKeyListener(keyHandler);
        txt_MontoPago1.addKeyListener(keyHandler);
        btn_Finalizar.addKeyListener(keyHandler);
        if (gui_puntoDeVenta.getTipoDeComprobante().equals("Factura A") || gui_puntoDeVenta.getTipoDeComprobante().equals("Factura B") || gui_puntoDeVenta.getTipoDeComprobante().equals("Factura C")) {
            this.chk_condicionDividir.setEnabled(true);
        }
    }

    public boolean isExito() {
        return exito;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/SIC_24_square.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void lanzarReporteFactura(Factura factura) throws JRException {
//        JasperPrint report = facturaService.getReporteFacturaVenta(factura);
//        JDialog viewer = new JDialog(new JFrame(), "Vista Previa", true);
//        viewer.setSize(gui_puntoDeVenta.getWidth() - 25, gui_puntoDeVenta.getHeight() - 25);
//        ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/SIC_16_square.png"));
//        viewer.setIconImage(iconoVentana.getImage());
//        viewer.setLocationRelativeTo(null);
//        JRViewer jrv = new JRViewer(report);
//        viewer.getContentPane().add(jrv);
//        viewer.setVisible(true);
    }

    private void cargarFormasDePago() {
        cmb_FormaDePago1.removeAllItems();
        cmb_FormaDePago2.removeAllItems();
        cmb_FormaDePago3.removeAllItems();
        List<FormaDePago> formasDePago = formaDePagoService.getFormasDePago(gui_puntoDeVenta.getEmpresa());
        for (FormaDePago formaDePago : formasDePago) {
            cmb_FormaDePago1.addItem(formaDePago);
            cmb_FormaDePago2.addItem(formaDePago);
            cmb_FormaDePago3.addItem(formaDePago);
        }
    }

    private void cargarTransportistas() {
        cmb_Transporte.removeAllItems();
        List<Transportista> transportes = transportistaService.getTransportistas(gui_puntoDeVenta.getEmpresa());
        for (Transportista transporte : transportes) {
            cmb_Transporte.addItem(transporte);
        }
    }

    private Factura guardarFactura(Factura facturaVenta) throws BusinessServiceException {
        facturaService.guardar(facturaVenta);
        Factura facturaGuardada = facturaService.getFacturaVentaPorTipoSerieNum(facturaVenta.getTipoFactura(), facturaVenta.getNumSerie(), facturaVenta.getNumFactura());
        facturaGuardada.setPagos(pagoService.getPagosDeLaFactura(facturaGuardada));
        return facturaGuardada;
    }

    private void calcularVuelto() {
        try {
            txt_AbonaCon.commitEdit();
            double montoRecibido = Double.parseDouble((txt_AbonaCon.getText()));
            double vuelto = facturaService.calcularVuelto(gui_puntoDeVenta.getResultadosFactura().getTotal(), montoRecibido);
            lbl_Vuelto.setValue(vuelto);

        } catch (ParseException ex) {
            String msjError = "Se produjo un error analizando los campos.";
            LOGGER.error(msjError + " - " + ex.getMessage());
        }
    }

    private FacturaVenta construirFactura() {
        FacturaVenta facturaVenta = FacturaVenta.builder()
            .fecha(gui_puntoDeVenta.getFechaFactura())
            .tipoFactura(gui_puntoDeVenta.getTipoDeComprobante().charAt(gui_puntoDeVenta.getTipoDeComprobante().length() - 1))
            .numSerie(1)
            .fechaVencimiento(gui_puntoDeVenta.getFechaVencimiento())
            .transportista((Transportista) cmb_Transporte.getSelectedItem())
            .renglones(gui_puntoDeVenta.getRenglones())
            .subTotal(gui_puntoDeVenta.getResultadosFactura().getSubTotal())
            .recargo_porcentaje(gui_puntoDeVenta.getResultadosFactura().getRecargo_porcentaje())
            .recargo_neto(gui_puntoDeVenta.getResultadosFactura().getRecargo_neto())
            .descuento_porcentaje(0)
            .descuento_neto(0)
            .subTotal_neto(gui_puntoDeVenta.getResultadosFactura().getSubTotal_neto())
            .iva_105_neto(gui_puntoDeVenta.getResultadosFactura().getIva_105_neto())
            .iva_21_neto(gui_puntoDeVenta.getResultadosFactura().getIva_21_neto())
            .impuestoInterno_neto(gui_puntoDeVenta.getResultadosFactura().getImpuestoInterno_neto())
            .total(gui_puntoDeVenta.getResultadosFactura().getTotal())
            .observaciones(gui_puntoDeVenta.getTxta_Observaciones().getText().trim())
            .empresa(gui_puntoDeVenta.getEmpresa())
            .eliminada(false)
            .cliente(gui_puntoDeVenta.getCliente())
            .usuario(usuarioService.getUsuarioActivo().getUsuario())
            .build();
        double montoPagado = 0.0;
        List<Pago> pagos = this.construirListaPagos();
        for (Pago pago : pagos) {
            pago.setFactura(facturaVenta);
            montoPagado += pago.getMonto();
        }
        facturaVenta.setPagos(pagos);
        facturaVenta.setPagada((facturaVenta.getTotal() - montoPagado) <= 0);
         for (RenglonFactura renglon : gui_puntoDeVenta.getRenglones()) {
             renglon.setFactura(facturaVenta);
         }               
        return facturaVenta;
    }

    private void setEstadosCmbFormaDePago() {
        chk_FormaDePago1.setSelected(true);
        cmb_FormaDePago1.setSelectedItem(formaDePagoService.getFormaDePagoPredeterminada(gui_puntoDeVenta.getEmpresa()));
        cmb_FormaDePago2.setEnabled(false);
        txt_MontoPago2.setEnabled(false);
        cmb_FormaDePago2.setSelectedItem(formaDePagoService.getFormaDePagoPredeterminada(gui_puntoDeVenta.getEmpresa()));
        cmb_FormaDePago3.setSelectedItem(formaDePagoService.getFormaDePagoPredeterminada(gui_puntoDeVenta.getEmpresa()));
        cmb_FormaDePago3.setEnabled(false);
        txt_MontoPago3.setEnabled(false);
    }

    private List<Pago> construirListaPagos() {
        List<Pago> pagos = new ArrayList<>();
        if (chk_FormaDePago1.isSelected() && chk_FormaDePago1.isEnabled()) {
            Pago pago1 = new Pago();
            pago1.setEmpresa(gui_puntoDeVenta.getEmpresa());
            pago1.setFormaDePago((FormaDePago) cmb_FormaDePago1.getSelectedItem());
            pago1.setFecha(new Date());
            pago1.setMonto(Double.parseDouble(txt_MontoPago1.getValue().toString()));
            pago1.setNota("");
            pagos.add(pago1);
        }
        if (chk_FormaDePago2.isSelected() && chk_FormaDePago2.isEnabled()) {
            Pago pago2 = new Pago();
            pago2.setEmpresa(gui_puntoDeVenta.getEmpresa());
            pago2.setFormaDePago((FormaDePago) cmb_FormaDePago2.getSelectedItem());
            pago2.setFecha(new Date());
            pago2.setMonto(Double.parseDouble(txt_MontoPago2.getValue().toString()));
            pago2.setNota("");
            pagos.add(pago2);
        }
        if (chk_FormaDePago3.isSelected() && chk_FormaDePago3.isEnabled()) {
            Pago pago3 = new Pago();
            pago3.setEmpresa(gui_puntoDeVenta.getEmpresa());
            pago3.setFormaDePago((FormaDePago) cmb_FormaDePago3.getSelectedItem());
            pago3.setFecha(new Date());
            pago3.setMonto(Double.parseDouble(txt_MontoPago3.getValue().toString()));
            pago3.setNota("");
            pagos.add(pago3);
        }
        return pagos;
    }

    private void finalizarVenta() {
        try {
            boolean dividir = false;
            int[] indicesParaDividir = null;
            if (chk_condicionDividir.isSelected()
                    && (gui_puntoDeVenta.getTipoDeComprobante().equals("Factura A")
                    || gui_puntoDeVenta.getTipoDeComprobante().equals("Factura B")
                    || gui_puntoDeVenta.getTipoDeComprobante().equals("Factura C"))) {

                ModeloTabla modeloTablaPuntoDeVenta = gui_puntoDeVenta.getModeloTabla();
                indicesParaDividir = new int[modeloTablaPuntoDeVenta.getRowCount()];
                int j = 0;
                boolean tieneRenglonesMarcados = false;
                for (int i = 0; i < modeloTablaPuntoDeVenta.getRowCount(); i++) {
                    if ((boolean) modeloTablaPuntoDeVenta.getValueAt(i, 0)) {
                        indicesParaDividir[j] = i;
                        j++;
                        tieneRenglonesMarcados = true;
                    }
                }
                if (indicesParaDividir.length != 0 && tieneRenglonesMarcados) {
                    dividir = true;
                }
            }
            if (!dividir) {
                FacturaVenta factura = this.construirFactura();
                if (gui_puntoDeVenta.getPedido() != null) {
                    factura.setPedido(pedidoService.getPedidoPorNumero(gui_puntoDeVenta.getPedido().getNroPedido(), gui_puntoDeVenta.getEmpresa().getId_Empresa()));
                }
                this.lanzarReporteFactura(this.guardarFactura(factura));
                exito = true;
            } else {
                List<FacturaVenta> facturasDivididas = facturaService.dividirYGuardarFactura(this.construirFactura(), indicesParaDividir);
                for (Factura factura : facturasDivididas) {
                    if (facturasDivididas.size() == 2 && !factura.getRenglones().isEmpty()) {
                        if (gui_puntoDeVenta.getPedido() != null) {
                            factura.setPedido(pedidoService.getPedidoPorNumero(gui_puntoDeVenta.getPedido().getNroPedido(), gui_puntoDeVenta.getEmpresa().getId_Empresa()));
                        }
                        this.lanzarReporteFactura(this.guardarFactura(factura));
                        exito = true;
                    }
                }
            }
            if (gui_puntoDeVenta.getPedido() != null) {
                pedidoService.actualizarEstadoPedido(gui_puntoDeVenta.getPedido());
                gui_puntoDeVenta.dispose();
            }
            this.dispose();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        } catch (JRException ex) {
            String msjError = "Se produjo un error procesando el reporte.";
            LOGGER.error(msjError + " - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clase interna para manejar las hotkeys
     */
    class HotKeysHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }

            if (evt.getKeyCode() == KeyEvent.VK_ENTER && evt.getSource() == btn_Finalizar) {
                btn_FinalizarActionPerformed(null);
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Finalizar = new javax.swing.JButton();
        panel = new javax.swing.JPanel();
        lbl_Vendor = new javax.swing.JLabel();
        lbl_Vendedor = new javax.swing.JLabel();
        lbl_Transporte = new javax.swing.JLabel();
        cmb_Transporte = new javax.swing.JComboBox();
        separador1 = new javax.swing.JSeparator();
        chk_FormaDePago1 = new javax.swing.JCheckBox();
        cmb_FormaDePago1 = new javax.swing.JComboBox();
        txt_MontoPago1 = new javax.swing.JFormattedTextField();
        chk_FormaDePago2 = new javax.swing.JCheckBox();
        cmb_FormaDePago2 = new javax.swing.JComboBox();
        txt_MontoPago2 = new javax.swing.JFormattedTextField();
        chk_FormaDePago3 = new javax.swing.JCheckBox();
        cmb_FormaDePago3 = new javax.swing.JComboBox();
        txt_MontoPago3 = new javax.swing.JFormattedTextField();
        separador = new javax.swing.JSeparator();
        lbl_Cambio = new javax.swing.JLabel();
        lbl_Total = new javax.swing.JLabel();
        lbl_TotalAPagar = new javax.swing.JFormattedTextField();
        lbl_Total1 = new javax.swing.JLabel();
        txt_AbonaCon = new javax.swing.JFormattedTextField();
        lbl_Devolucion = new javax.swing.JLabel();
        lbl_Vuelto = new javax.swing.JFormattedTextField();
        separator2 = new javax.swing.JSeparator();
        chk_condicionDividir = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cerrar Venta");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        btn_Finalizar.setForeground(java.awt.Color.blue);
        btn_Finalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Finalizar.setText("Finalizar");
        btn_Finalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_FinalizarActionPerformed(evt);
            }
        });

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Vendor.setText("Vendedor:");

        lbl_Vendedor.setForeground(new java.awt.Color(29, 156, 37));
        lbl_Vendedor.setText("XXXXXXXXXXXXXXXXXXXXXX");

        lbl_Transporte.setText("Transporte:");

        chk_FormaDePago1.setText("Forma de Pago #1:");
        chk_FormaDePago1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FormaDePago1ItemStateChanged(evt);
            }
        });

        txt_MontoPago1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txt_MontoPago1.setText("0.00");
        txt_MontoPago1.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txt_MontoPago1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_MontoPago1FocusGained(evt);
            }
        });

        chk_FormaDePago2.setText("Forma de Pago #2:");
        chk_FormaDePago2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FormaDePago2ItemStateChanged(evt);
            }
        });

        txt_MontoPago2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txt_MontoPago2.setText("0.00");
        txt_MontoPago2.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txt_MontoPago2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_MontoPago2FocusGained(evt);
            }
        });

        chk_FormaDePago3.setText("Forma de Pago #3:");
        chk_FormaDePago3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FormaDePago3ItemStateChanged(evt);
            }
        });

        txt_MontoPago3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txt_MontoPago3.setText("0.00");
        txt_MontoPago3.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txt_MontoPago3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_MontoPago3FocusGained(evt);
            }
        });

        lbl_Cambio.setText("Cambio:");

        lbl_Total.setText("Total a pagar:");

        lbl_TotalAPagar.setEditable(false);
        lbl_TotalAPagar.setForeground(new java.awt.Color(29, 156, 37));
        lbl_TotalAPagar.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        lbl_TotalAPagar.setText("0.00");
        lbl_TotalAPagar.setFocusable(false);
        lbl_TotalAPagar.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N

        lbl_Total1.setText("Abona con:");

        txt_AbonaCon.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txt_AbonaCon.setText("0.00");
        txt_AbonaCon.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        txt_AbonaCon.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_AbonaConFocusGained(evt);
            }
        });
        txt_AbonaCon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_AbonaConKeyReleased(evt);
            }
        });

        lbl_Devolucion.setText("Vuelto:");

        lbl_Vuelto.setEditable(false);
        lbl_Vuelto.setForeground(new java.awt.Color(29, 156, 37));
        lbl_Vuelto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        lbl_Vuelto.setText("0.00");
        lbl_Vuelto.setFocusable(false);
        lbl_Vuelto.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N

        chk_condicionDividir.setText("Dividir Factura");
        chk_condicionDividir.setEnabled(false);
        chk_condicionDividir.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_condicionDividirItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(separator2, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(separador, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_Vendor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_Transporte, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_Vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_Transporte, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(separador1, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chk_FormaDePago3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_FormaDePago2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chk_FormaDePago1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_FormaDePago2, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_FormaDePago1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_FormaDePago3, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_MontoPago3)
                            .addComponent(txt_MontoPago1)
                            .addComponent(txt_MontoPago2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(lbl_Cambio, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_Devolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_Total1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_Total, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_Vuelto)
                            .addComponent(txt_AbonaCon)
                            .addComponent(lbl_TotalAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(chk_condicionDividir, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Vendor)
                    .addComponent(lbl_Vendedor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_Transporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Transporte))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separador1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_MontoPago1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_FormaDePago1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_FormaDePago1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_FormaDePago2)
                    .addComponent(cmb_FormaDePago2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_MontoPago2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_MontoPago3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_FormaDePago3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_FormaDePago3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separador, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Cambio)
                    .addComponent(lbl_Total)
                    .addComponent(lbl_TotalAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Total1)
                    .addComponent(txt_AbonaCon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Devolucion)
                    .addComponent(lbl_Vuelto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chk_condicionDividir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_MontoPago1, txt_MontoPago3});

        panelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_Cambio, lbl_Devolucion, lbl_Total, lbl_Total1, lbl_TotalAPagar, lbl_Vuelto, txt_AbonaCon});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Finalizar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Finalizar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            this.cargarFormasDePago();
            this.cargarTransportistas();
            this.setEstadosCmbFormaDePago();
            //set predeterminado
            cmb_Transporte.setSelectedIndex(0);
            lbl_Vendedor.setText(usuarioService.getUsuarioActivo().getUsuario().getNombre());
            txt_AbonaCon.requestFocus();

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formWindowOpened

    private void btn_FinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_FinalizarActionPerformed
        double totalPagos = 0.0;
        if (chk_FormaDePago1.isSelected() && chk_FormaDePago1.isEnabled()) {
            totalPagos += Double.parseDouble((txt_MontoPago1.getText()).replaceAll(",", ""));
        }
        if (chk_FormaDePago2.isSelected() && chk_FormaDePago2.isEnabled()) {
            totalPagos += Double.parseDouble((txt_MontoPago2.getText()).replaceAll(",", ""));
        }
        if (chk_FormaDePago3.isSelected() && chk_FormaDePago3.isEnabled()) {
            totalPagos += Double.parseDouble((txt_MontoPago3.getText()).replaceAll(",", ""));
        }
        double totalAPagar = Double.parseDouble((lbl_TotalAPagar.getText().substring(1)).replaceAll(",", ""));
        if (totalPagos < totalAPagar) {
            int reply = JOptionPane.showConfirmDialog(this, "Los montos ingresados no cubren el total a pagar.\n¿Desea continuar?", "Aviso", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                this.finalizarVenta();
            }
        } else {
            this.finalizarVenta();
        }
    }//GEN-LAST:event_btn_FinalizarActionPerformed

    private void txt_MontoPago1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_MontoPago1FocusGained
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt_MontoPago1.selectAll();
            }
        });
    }//GEN-LAST:event_txt_MontoPago1FocusGained

    private void txt_MontoPago3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_MontoPago3FocusGained
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt_MontoPago3.selectAll();
            }
        });
    }//GEN-LAST:event_txt_MontoPago3FocusGained

    private void chk_FormaDePago1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FormaDePago1ItemStateChanged
        cmb_FormaDePago1.setEnabled(chk_FormaDePago1.isSelected());
        txt_MontoPago1.setEnabled(chk_FormaDePago1.isSelected());
    }//GEN-LAST:event_chk_FormaDePago1ItemStateChanged

    private void chk_FormaDePago2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FormaDePago2ItemStateChanged
        cmb_FormaDePago2.setEnabled(chk_FormaDePago2.isSelected());
        txt_MontoPago2.setEnabled(chk_FormaDePago2.isSelected());
    }//GEN-LAST:event_chk_FormaDePago2ItemStateChanged

    private void chk_FormaDePago3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FormaDePago3ItemStateChanged
        cmb_FormaDePago3.setEnabled(chk_FormaDePago3.isSelected());
        txt_MontoPago3.setEnabled(chk_FormaDePago3.isSelected());
    }//GEN-LAST:event_chk_FormaDePago3ItemStateChanged

    private void txt_AbonaConFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_AbonaConFocusGained
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt_AbonaCon.selectAll();
            }
        });
    }//GEN-LAST:event_txt_AbonaConFocusGained

    private void txt_MontoPago2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_MontoPago2FocusGained
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt_MontoPago2.selectAll();
            }
        });
    }//GEN-LAST:event_txt_MontoPago2FocusGained

    private void txt_AbonaConKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_AbonaConKeyReleased
        this.calcularVuelto();
    }//GEN-LAST:event_txt_AbonaConKeyReleased

    private void chk_condicionDividirItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_condicionDividirItemStateChanged
        if (chk_FormaDePago1.isSelected()) {
            chk_FormaDePago1.setEnabled(!chk_condicionDividir.isSelected());
            cmb_FormaDePago1.setEnabled(!chk_condicionDividir.isSelected());
            txt_MontoPago1.setEnabled(!chk_condicionDividir.isSelected());
        } else {
            chk_FormaDePago1.setEnabled(!chk_condicionDividir.isSelected());
        }
        if (chk_FormaDePago2.isSelected()) {
            chk_FormaDePago2.setEnabled(!chk_condicionDividir.isSelected());
            cmb_FormaDePago2.setEnabled(!chk_condicionDividir.isSelected());
            txt_MontoPago2.setEnabled(!chk_condicionDividir.isSelected());
        } else {
            chk_FormaDePago2.setEnabled(!chk_condicionDividir.isSelected());
        }
        if (chk_FormaDePago3.isSelected()) {
            chk_FormaDePago3.setEnabled(!chk_condicionDividir.isSelected());
            cmb_FormaDePago3.setEnabled(!chk_condicionDividir.isSelected());
            txt_MontoPago3.setEnabled(!chk_condicionDividir.isSelected());
        } else {
            chk_FormaDePago3.setEnabled(!chk_condicionDividir.isSelected());
        }
    }//GEN-LAST:event_chk_condicionDividirItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Finalizar;
    private javax.swing.JCheckBox chk_FormaDePago1;
    private javax.swing.JCheckBox chk_FormaDePago2;
    private javax.swing.JCheckBox chk_FormaDePago3;
    private javax.swing.JCheckBox chk_condicionDividir;
    private javax.swing.JComboBox cmb_FormaDePago1;
    private javax.swing.JComboBox cmb_FormaDePago2;
    private javax.swing.JComboBox cmb_FormaDePago3;
    private javax.swing.JComboBox cmb_Transporte;
    private javax.swing.JLabel lbl_Cambio;
    private javax.swing.JLabel lbl_Devolucion;
    private javax.swing.JLabel lbl_Total;
    private javax.swing.JLabel lbl_Total1;
    private javax.swing.JFormattedTextField lbl_TotalAPagar;
    private javax.swing.JLabel lbl_Transporte;
    private javax.swing.JLabel lbl_Vendedor;
    private javax.swing.JLabel lbl_Vendor;
    private javax.swing.JFormattedTextField lbl_Vuelto;
    private javax.swing.JPanel panel;
    private javax.swing.JSeparator separador;
    private javax.swing.JSeparator separador1;
    private javax.swing.JSeparator separator2;
    private javax.swing.JFormattedTextField txt_AbonaCon;
    private javax.swing.JFormattedTextField txt_MontoPago1;
    private javax.swing.JFormattedTextField txt_MontoPago2;
    private javax.swing.JFormattedTextField txt_MontoPago3;
    // End of variables declaration//GEN-END:variables
}
