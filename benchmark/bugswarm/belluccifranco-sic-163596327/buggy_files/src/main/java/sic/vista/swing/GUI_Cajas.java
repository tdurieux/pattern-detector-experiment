package sic.vista.swing;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.BusquedaCajaCriteria;
import sic.modelo.Caja;
import sic.modelo.EmpresaActiva;
import sic.modelo.Usuario;
import sic.service.EstadoCaja;
import sic.service.ICajaService;
import sic.service.IUsuarioService;
import sic.service.BusinessServiceException;
import sic.util.ColoresEstadosRenderer;
import sic.util.FormatoFechasEnTablasRenderer;
import sic.util.FormatterFechaHora;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class GUI_Cajas extends javax.swing.JInternalFrame {

    private ModeloTabla modeloTablaCajas = new ModeloTabla();
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final ICajaService cajaService = appContext.getBean(ICajaService.class);    
    private final IUsuarioService usuarioService = appContext.getBean(IUsuarioService.class);
    private static final Logger log = Logger.getLogger(GUI_Cajas.class.getPackage().getName());
    private List<Caja> cajas;

    public GUI_Cajas() {
        initComponents();
        this.setSize(850, 600);
        this.setColumnasCaja();
        Usuario paraMostrar = new Usuario();
        paraMostrar.setNombre("Seleccione un Usuario....");
        cmb_Usuarios.addItem(paraMostrar);
        cmb_Usuarios.setEnabled(false);
    }

    private void setColumnasCaja() {
        //sorting
        tbl_Cajas.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[8];
        encabezados[0] = "Estado";
        encabezados[1] = "Fecha Apertura";
        encabezados[2] = "Hora Control";
        encabezados[3] = "Fecha Cierre";
        encabezados[4] = "Usuario de Cierre";
        encabezados[5] = "Saldo Apertura";
        encabezados[6] = "Saldo Sistema";
        encabezados[7] = "Saldo Real";
        modeloTablaCajas.setColumnIdentifiers(encabezados);
        tbl_Cajas.setModel(modeloTablaCajas);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaCajas.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = Date.class;
        tipos[2] = String.class;
        tipos[3] = Date.class;
        tipos[4] = String.class;
        tipos[5] = Double.class;
        tipos[6] = Double.class;
        tipos[7] = Double.class;
        modeloTablaCajas.setClaseColumnas(tipos);
        tbl_Cajas.getTableHeader().setReorderingAllowed(false);
        tbl_Cajas.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Cajas.setDefaultRenderer(Double.class, new RenderTabla());

        //Tamanios de columnas
        tbl_Cajas.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbl_Cajas.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbl_Cajas.getColumnModel().getColumn(2).setPreferredWidth(30);
        tbl_Cajas.getColumnModel().getColumn(3).setPreferredWidth(80);
        tbl_Cajas.getColumnModel().getColumn(4).setPreferredWidth(40);
        tbl_Cajas.getColumnModel().getColumn(5).setPreferredWidth(20);
        tbl_Cajas.getColumnModel().getColumn(6).setPreferredWidth(20);
        tbl_Cajas.getColumnModel().getColumn(7).setPreferredWidth(20);
        //renderer fechas
        tbl_Cajas.getColumnModel().getColumn(1).setCellRenderer(new FormatoFechasEnTablasRenderer());
        tbl_Cajas.getColumnModel().getColumn(2).setCellRenderer(new FormatoFechasEnTablasRenderer());
        tbl_Cajas.getColumnModel().getColumn(3).setCellRenderer(new FormatoFechasEnTablasRenderer());
    }

    private void buscar() {
        pb_barra.setIndeterminate(true);
        SwingWorker<List<Caja>, Void> worker = new SwingWorker<List<Caja>, Void>() {
            @Override
            protected List<Caja> doInBackground() throws Exception {
                try {
                    BusquedaCajaCriteria criteria = new BusquedaCajaCriteria();
                    criteria.setBuscaPorFecha(chk_Fecha.isSelected());
                    criteria.setFechaDesde(dc_FechaDesde.getDate());
                    criteria.setFechaHasta(dc_FechaHasta.getDate());
                    criteria.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
                    //criteria.setCantidadDeRegistros(Integer.parseInt(cmb_paginado.getSelectedItem().toString()));
                    criteria.setCantidadDeRegistros(0);
                    criteria.setBuscaPorUsuario(chk_Usuario.isSelected());
                    criteria.setUsuario((Usuario) cmb_Usuarios.getSelectedItem());
                    List<Caja> cajasResultantes = cajaService.getCajasCriteria(criteria);
                    cajas = cajasResultantes;
                    return cajas;

                } catch (BusinessServiceException ex) {
                    JOptionPane.showInternalMessageDialog(getParent(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                } 
                cajas = new ArrayList<>();
                return cajas;
            }

            @Override
            protected void done() {
                pb_barra.setIndeterminate(false);
                try {
                    lbl_cantidadMostrar.setText(cajas.size() + " Cajas encontradas");
                    if (get().isEmpty()) {
                        JOptionPane.showInternalMessageDialog(getParent(),
                                ResourceBundle.getBundle("Mensajes")
                                .getString("mensaje_busqueda_sin_resultados"),
                                "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        cargarResultadosAlTable(cajas);
                    }

                } catch (InterruptedException ex) {
                    String msjError = "La tarea que se estaba realizando fue interrumpida. Intente nuevamente.";
                    log.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(getParent(), msjError, "Error", JOptionPane.ERROR_MESSAGE);

                } catch (ExecutionException ex) {
                    String msjError = "Se produjo un error en la ejecución de la tarea solicitada. Intente nuevamente.";
                    log.error(msjError + " - " + ex.getMessage());
                    JOptionPane.showInternalMessageDialog(getParent(), msjError, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void cargarResultadosAlTable(List<Caja> cajas) {
        double totalFinal = 0.0;
        double totalCierre = 0.0;
        for (Caja caja : cajas) {
            Object[] fila = new Object[8];
            fila[0] = caja.getEstado();
            fila[1] = caja.getFechaApertura();
            fila[2] = (new FormatterFechaHora(FormatterFechaHora.FORMATO_HORA_INTERNACIONAL)).format(caja.getFechaCorteInforme());
            if (caja.getFechaCierre() != null) {
                fila[3] = caja.getFechaCierre();
            }
            fila[4] = (caja.getUsuarioCierraCaja() != null ? caja.getUsuarioCierraCaja() : "");
            fila[5] = caja.getSaldoInicial();
            fila[6] = (caja.getEstado().equals(EstadoCaja.CERRADA) ? caja.getSaldoFinal() : 0.0);
            fila[7] = (caja.getEstado().equals(EstadoCaja.CERRADA) ? caja.getSaldoReal() : 0.0);
            totalFinal += caja.getSaldoFinal();
            totalCierre += caja.getSaldoReal();
            modeloTablaCajas.addRow(fila);
        }
        tbl_Cajas.setModel(modeloTablaCajas);
        tbl_Cajas.getColumnModel().getColumn(0).setCellRenderer(new ColoresEstadosRenderer());
        ftxt_TotalFinal.setValue(totalFinal);
        ftxt_TotalCierre.setValue(totalCierre);
    }

    private void limpiarResultados() {
        modeloTablaCajas = new ModeloTabla();
        tbl_Cajas.setModel(modeloTablaCajas);
        this.setColumnasCaja();
    }

    private void habilitarAperturaDeCaja() {
        boolean cajaAbierta = true;
        Caja caja = cajaService.getUltimaCaja(EmpresaActiva.getInstance().getEmpresa().getId_Empresa());               
        if (caja != null) {
            if (caja.getEstado() == EstadoCaja.CERRADA) {
                String fechaCaja = (new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHA_HISPANO)).format(caja.getFechaApertura());
                if (fechaCaja.equals((new FormatterFechaHora(FormatterFechaHora.FORMATO_FECHA_HISPANO)).format(new Date()))) {
                    cajaAbierta = false;                    
                }
            } else {
                cajaAbierta = false;                
            }
        }        
        btn_AbrirCaja.setEnabled(cajaAbierta);
    }

    private void abrirCaja() {
        GUI_AbrirCaja abrirCaja = new GUI_AbrirCaja(true);
        abrirCaja.setLocationRelativeTo(this);
        abrirCaja.setVisible(true);
        this.limpiarResultados();
        this.buscar();
        this.habilitarAperturaDeCaja();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Filtros = new javax.swing.JPanel();
        chk_Fecha = new javax.swing.JCheckBox();
        dc_FechaDesde = new com.toedter.calendar.JDateChooser();
        dc_FechaHasta = new com.toedter.calendar.JDateChooser();
        lbl_Hasta = new javax.swing.JLabel();
        lbl_Desde = new javax.swing.JLabel();
        btn_buscar = new javax.swing.JButton();
        chk_Usuario = new javax.swing.JCheckBox();
        cmb_Usuarios = new javax.swing.JComboBox<>();
        pb_barra = new javax.swing.JProgressBar();
        lbl_cantidadMostrar = new javax.swing.JLabel();
        pnl_Cajas = new javax.swing.JPanel();
        sp_TablaCajas = new javax.swing.JScrollPane();
        tbl_Cajas = new javax.swing.JTable();
        btn_AbrirCaja = new javax.swing.JButton();
        btn_verDetalle = new javax.swing.JButton();
        btn_eliminarCaja = new javax.swing.JButton();
        lbl_TotalFinal = new javax.swing.JLabel();
        ftxt_TotalFinal = new javax.swing.JFormattedTextField();
        lbl_TotalCierre = new javax.swing.JLabel();
        ftxt_TotalCierre = new javax.swing.JFormattedTextField();

        setClosable(true);
        setMaximizable(true);
        setTitle("Administrar Cajas");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Caja_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                GUI_Cajas.this.internalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        pnl_Filtros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Fecha.setText("Fecha Caja:");
        chk_Fecha.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_FechaItemStateChanged(evt);
            }
        });

        dc_FechaDesde.setDateFormatString("dd/MM/yyyy");
        dc_FechaDesde.setEnabled(false);

        dc_FechaHasta.setDateFormatString("dd/MM/yyyy");
        dc_FechaHasta.setEnabled(false);

        lbl_Hasta.setText("Hasta:");
        lbl_Hasta.setEnabled(false);

        lbl_Desde.setText("Desde:");
        lbl_Desde.setEnabled(false);

        btn_buscar.setForeground(java.awt.Color.blue);
        btn_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_buscar.setText("Buscar");
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });

        chk_Usuario.setText("Usuario:");
        chk_Usuario.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_UsuarioItemStateChanged(evt);
            }
        });

        lbl_cantidadMostrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout pnl_FiltrosLayout = new javax.swing.GroupLayout(pnl_Filtros);
        pnl_Filtros.setLayout(pnl_FiltrosLayout);
        pnl_FiltrosLayout.setHorizontalGroup(
            pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                        .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chk_Usuario)
                            .addComponent(chk_Fecha))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_Usuarios, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                                .addComponent(lbl_Desde)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                                .addComponent(lbl_Hasta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                        .addComponent(btn_buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantidadMostrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pb_barra, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pnl_FiltrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_buscar, chk_Fecha, chk_Usuario});

        pnl_FiltrosLayout.setVerticalGroup(
            pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_FiltrosLayout.createSequentialGroup()
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Fecha)
                    .addComponent(lbl_Desde)
                    .addComponent(dc_FechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Hasta)
                    .addComponent(dc_FechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chk_Usuario)
                    .addComponent(cmb_Usuarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_FiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_buscar)
                    .addComponent(lbl_cantidadMostrar)
                    .addComponent(pb_barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnl_FiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmb_Usuarios, dc_FechaDesde, dc_FechaHasta});

        pnl_FiltrosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_buscar, lbl_cantidadMostrar, pb_barra});

        pnl_Cajas.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Cajas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        sp_TablaCajas.setViewportView(tbl_Cajas);

        btn_AbrirCaja.setForeground(java.awt.Color.blue);
        btn_AbrirCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AbrirCaja_16x16.png"))); // NOI18N
        btn_AbrirCaja.setText("Abrir Nueva");
        btn_AbrirCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AbrirCajaActionPerformed(evt);
            }
        });

        btn_verDetalle.setForeground(java.awt.Color.blue);
        btn_verDetalle.setText("Ver Detalle");
        btn_verDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verDetalleActionPerformed(evt);
            }
        });

        btn_eliminarCaja.setForeground(java.awt.Color.blue);
        btn_eliminarCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Cancel_16x16.png"))); // NOI18N
        btn_eliminarCaja.setText("Eliminar");
        btn_eliminarCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_eliminarCajaActionPerformed(evt);
            }
        });

        lbl_TotalFinal.setText("Total Sistema:");

        ftxt_TotalFinal.setEditable(false);
        ftxt_TotalFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        ftxt_TotalFinal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_TotalFinal.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        lbl_TotalCierre.setText("Total Real:");

        ftxt_TotalCierre.setEditable(false);
        ftxt_TotalCierre.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("$##,###,##0.00"))));
        ftxt_TotalCierre.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ftxt_TotalCierre.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        javax.swing.GroupLayout pnl_CajasLayout = new javax.swing.GroupLayout(pnl_Cajas);
        pnl_Cajas.setLayout(pnl_CajasLayout);
        pnl_CajasLayout.setHorizontalGroup(
            pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_CajasLayout.createSequentialGroup()
                .addComponent(btn_AbrirCaja)
                .addGap(0, 0, 0)
                .addComponent(btn_verDetalle)
                .addGap(0, 0, 0)
                .addComponent(btn_eliminarCaja)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_CajasLayout.createSequentialGroup()
                        .addComponent(lbl_TotalFinal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxt_TotalFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_CajasLayout.createSequentialGroup()
                        .addComponent(lbl_TotalCierre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftxt_TotalCierre, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addComponent(sp_TablaCajas, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
        );

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_AbrirCaja, btn_eliminarCaja, btn_verDetalle});

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_TotalCierre, lbl_TotalFinal});

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ftxt_TotalCierre, ftxt_TotalFinal});

        pnl_CajasLayout.setVerticalGroup(
            pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_CajasLayout.createSequentialGroup()
                .addComponent(sp_TablaCajas, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_verDetalle)
                        .addComponent(btn_eliminarCaja)
                        .addComponent(btn_AbrirCaja))
                    .addGroup(pnl_CajasLayout.createSequentialGroup()
                        .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_TotalFinal)
                            .addComponent(ftxt_TotalFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(pnl_CajasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_TotalCierre)
                            .addComponent(ftxt_TotalCierre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        pnl_CajasLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_eliminarCaja, btn_verDetalle});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_Cajas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Filtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Filtros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Cajas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chk_FechaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_FechaItemStateChanged
        //Pregunta el estado actual del checkBox
        if (chk_Fecha.isSelected() == true) {
            dc_FechaDesde.setEnabled(true);
            dc_FechaHasta.setEnabled(true);
            lbl_Desde.setEnabled(true);
            lbl_Hasta.setEnabled(true);
            dc_FechaDesde.requestFocus();
        } else {
            dc_FechaDesde.setEnabled(false);
            dc_FechaHasta.setEnabled(false);
            lbl_Desde.setEnabled(false);
            lbl_Hasta.setEnabled(false);
        }
    }//GEN-LAST:event_chk_FechaItemStateChanged

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        this.limpiarResultados();
        this.buscar();
        this.habilitarAperturaDeCaja();
    }//GEN-LAST:event_btn_buscarActionPerformed

    private void btn_verDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verDetalleActionPerformed
        if (tbl_Cajas.getSelectedRow() != -1) {
            int indiceDelModel = Utilidades.getSelectedRowModelIndice(tbl_Cajas);
            GUI_Caja caja = new GUI_Caja(this.cajas.get(indiceDelModel));
            caja.setLocationRelativeTo(this);
            caja.setModal(true);
            caja.setVisible(true);
            this.limpiarResultados();
            this.buscar();
        }
    }//GEN-LAST:event_btn_verDetalleActionPerformed

    private void btn_eliminarCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_eliminarCajaActionPerformed
        if (tbl_Cajas.getSelectedRow() != -1) {
            int confirmacionEliminacion = JOptionPane.showConfirmDialog(this,
                    "¿Esta seguro que desea eliminar la caja seleccionada?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (confirmacionEliminacion == JOptionPane.YES_OPTION) {
                int indiceDelModel = Utilidades.getSelectedRowModelIndice(tbl_Cajas);
                this.cajas.get(indiceDelModel).setEliminada(true);
                cajaService.actualizar(this.cajas.get(indiceDelModel));

            }
            this.limpiarResultados();
            this.buscar();
            this.habilitarAperturaDeCaja();
        }
    }//GEN-LAST:event_btn_eliminarCajaActionPerformed

    private void chk_UsuarioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_UsuarioItemStateChanged
        cmb_Usuarios.setEnabled(false);
        cmb_Usuarios.removeAllItems();
        if (chk_Usuario.isSelected() == true) {
            cmb_Usuarios.setEnabled(true);
            for (Usuario usuario : usuarioService.getUsuarios()) {
                cmb_Usuarios.addItem(usuario);
            }
        } else {
            Usuario paraMostrar = new Usuario();
            paraMostrar.setNombre("Seleccione un Usuario....");
            cmb_Usuarios.addItem(paraMostrar);
        }
    }//GEN-LAST:event_chk_UsuarioItemStateChanged

    private void btn_AbrirCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AbrirCajaActionPerformed
        Caja abierta = cajaService.getUltimaCaja(EmpresaActiva.getInstance().getEmpresa().getId_Empresa());
        if (abierta == null) {
            this.abrirCaja();
        } else if (abierta.getEstado() == EstadoCaja.CERRADA) {
            this.abrirCaja();
        }
    }//GEN-LAST:event_btn_AbrirCajaActionPerformed

    private void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_internalFrameOpened
        try {
            this.setMaximum(true);
            this.habilitarAperturaDeCaja();
        } catch (PropertyVetoException ex) {
            String msjError = "Se produjo un error al intentar maximizar la ventana.";
            log.error(msjError + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, msjError, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_internalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AbrirCaja;
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_eliminarCaja;
    private javax.swing.JButton btn_verDetalle;
    private javax.swing.JCheckBox chk_Fecha;
    private javax.swing.JCheckBox chk_Usuario;
    private javax.swing.JComboBox<Usuario> cmb_Usuarios;
    private com.toedter.calendar.JDateChooser dc_FechaDesde;
    private com.toedter.calendar.JDateChooser dc_FechaHasta;
    private javax.swing.JFormattedTextField ftxt_TotalCierre;
    private javax.swing.JFormattedTextField ftxt_TotalFinal;
    private javax.swing.JLabel lbl_Desde;
    private javax.swing.JLabel lbl_Hasta;
    private javax.swing.JLabel lbl_TotalCierre;
    private javax.swing.JLabel lbl_TotalFinal;
    private javax.swing.JLabel lbl_cantidadMostrar;
    private javax.swing.JProgressBar pb_barra;
    private javax.swing.JPanel pnl_Cajas;
    private javax.swing.JPanel pnl_Filtros;
    private javax.swing.JScrollPane sp_TablaCajas;
    private javax.swing.JTable tbl_Cajas;
    // End of variables declaration//GEN-END:variables
}
