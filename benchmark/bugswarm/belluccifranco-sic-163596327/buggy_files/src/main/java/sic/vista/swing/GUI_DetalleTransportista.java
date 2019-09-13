package sic.vista.swing;

import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.springframework.context.ApplicationContext;
import sic.AppContextProvider;
import sic.modelo.EmpresaActiva;
import sic.modelo.Localidad;
import sic.modelo.Pais;
import sic.modelo.Provincia;
import sic.modelo.Transportista;
import sic.service.ILocalidadService;
import sic.service.IPaisService;
import sic.service.IProvinciaService;
import sic.service.ITransportistaService;
import sic.service.BusinessServiceException;
import sic.service.TipoDeOperacion;

public class GUI_DetalleTransportista extends JDialog {

    private Transportista transportistaModificar;
    private final TipoDeOperacion operacion;
    private final ApplicationContext appContext = AppContextProvider.getApplicationContext();
    private final IPaisService paisService = appContext.getBean(IPaisService.class);
    private final IProvinciaService provinciaService = appContext.getBean(IProvinciaService.class);
    private final ILocalidadService localidadService = appContext.getBean(ILocalidadService.class);
    private final ITransportistaService transportistaService = appContext.getBean(ITransportistaService.class);    

    public GUI_DetalleTransportista() {
        this.initComponents();
        this.setIcon();
        this.setTitle("Nuevo Transportista");
        operacion = TipoDeOperacion.ALTA;
    }

    public GUI_DetalleTransportista(Transportista transportista) {
        this.initComponents();
        this.setIcon();
        this.setTitle("Modificar Transportista");
        operacion = TipoDeOperacion.ACTUALIZACION;
        transportistaModificar = transportista;
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(GUI_DetalleCliente.class.getResource("/sic/icons/Truck_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    private void cargarTransportistaParaModificar() {
        txt_Nombre.setText(transportistaModificar.getNombre());
        txt_Direccion.setText(transportistaModificar.getDireccion());
        cmb_Pais.setSelectedItem(transportistaModificar.getLocalidad().getProvincia().getPais());
        cmb_Provincia.setSelectedItem(transportistaModificar.getLocalidad().getProvincia());
        cmb_Localidad.setSelectedItem(transportistaModificar.getLocalidad());
        txt_Telefono.setText(transportistaModificar.getTelefono());
        txt_Web.setText(transportistaModificar.getWeb());
    }

    private void limpiarYRecargarComponentes() {
        txt_Nombre.setText("");
        txt_Direccion.setText("");
        txt_Telefono.setText("");
        txt_Web.setText("");
        cargarComboBoxPaises();
    }

    private void cargarComboBoxPaises() {
        List<Pais> paises;
        cmb_Pais.removeAllItems();
        paises = paisService.getPaises();
        for (Pais pais : paises) {
            cmb_Pais.addItem(pais);
        }
    }

    private void cargarComboBoxProvinciasDelPais(Pais paisSeleccionado) {
        List<Provincia> provincias;
        cmb_Provincia.removeAllItems();
        provincias = provinciaService.getProvinciasDelPais(paisSeleccionado);
        for (Provincia prov : provincias) {
            cmb_Provincia.addItem(prov);
        }
    }

    private void cargarComboBoxLocalidadesDeLaProvincia(Provincia provSeleccionada) {
        List<Localidad> Localidades;
        cmb_Localidad.removeAllItems();
        Localidades = localidadService.getLocalidadesDeLaProvincia(provSeleccionada);
        for (Localidad loc : Localidades) {
            cmb_Localidad.addItem(loc);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new javax.swing.JPanel();
        lbl_Direccion = new javax.swing.JLabel();
        txt_Direccion = new javax.swing.JTextField();
        lbl_Provincia = new javax.swing.JLabel();
        cmb_Provincia = new javax.swing.JComboBox();
        lbl_Localidad = new javax.swing.JLabel();
        cmb_Pais = new javax.swing.JComboBox();
        lbl_Pais = new javax.swing.JLabel();
        btn_NuevoPais = new javax.swing.JButton();
        btn_NuevaProvincia = new javax.swing.JButton();
        btn_NuevaLocalidad = new javax.swing.JButton();
        cmb_Localidad = new javax.swing.JComboBox();
        lbl_Nombre = new javax.swing.JLabel();
        txt_Nombre = new javax.swing.JTextField();
        lbl_Web = new javax.swing.JLabel();
        txt_Web = new javax.swing.JTextField();
        lbl_Telefono = new javax.swing.JLabel();
        txt_Telefono = new javax.swing.JTextField();
        btn_Guardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lbl_Direccion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Direccion.setText("Dirección:");

        lbl_Provincia.setForeground(java.awt.Color.red);
        lbl_Provincia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Provincia.setText("* Provincia:");

        cmb_Provincia.setMaximumRowCount(5);
        cmb_Provincia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_ProvinciaItemStateChanged(evt);
            }
        });

        lbl_Localidad.setForeground(java.awt.Color.red);
        lbl_Localidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Localidad.setText("* Localidad:");

        cmb_Pais.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmb_PaisItemStateChanged(evt);
            }
        });

        lbl_Pais.setForeground(java.awt.Color.red);
        lbl_Pais.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Pais.setText("* Pais:");

        btn_NuevoPais.setForeground(java.awt.Color.blue);
        btn_NuevoPais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevoPais.setText("Nuevo");
        btn_NuevoPais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoPaisActionPerformed(evt);
            }
        });

        btn_NuevaProvincia.setForeground(java.awt.Color.blue);
        btn_NuevaProvincia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevaProvincia.setText("Nueva");
        btn_NuevaProvincia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaProvinciaActionPerformed(evt);
            }
        });

        btn_NuevaLocalidad.setForeground(java.awt.Color.blue);
        btn_NuevaLocalidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddMap_16x16.png"))); // NOI18N
        btn_NuevaLocalidad.setText("Nueva");
        btn_NuevaLocalidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevaLocalidadActionPerformed(evt);
            }
        });

        lbl_Nombre.setForeground(java.awt.Color.red);
        lbl_Nombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Nombre.setText("* Nombre:");

        lbl_Web.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Web.setText("Página Web:");

        lbl_Telefono.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_Telefono.setText("Teléfono:");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_Localidad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(lbl_Provincia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Pais, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Direccion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Nombre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Web, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_Telefono, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_Web)
                    .addComponent(txt_Telefono)
                    .addComponent(txt_Direccion, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_Localidad, 0, 304, Short.MAX_VALUE)
                            .addComponent(cmb_Provincia, 0, 304, Short.MAX_VALUE)
                            .addComponent(cmb_Pais, 0, 304, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_NuevaProvincia)
                            .addComponent(btn_NuevoPais)
                            .addComponent(btn_NuevaLocalidad)))
                    .addComponent(txt_Nombre))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_Direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Direccion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Pais)
                    .addComponent(cmb_Pais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevoPais))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Provincia)
                    .addComponent(cmb_Provincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaProvincia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Localidad)
                    .addComponent(cmb_Localidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_NuevaLocalidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_Telefono)
                    .addComponent(txt_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Web)
                    .addComponent(txt_Web, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevoPais, cmb_Pais});

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaProvincia, cmb_Provincia});

        panel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_NuevaLocalidad, cmb_Localidad});

        btn_Guardar.setForeground(java.awt.Color.blue);
        btn_Guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        btn_Guardar.setText("Guardar");
        btn_Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_Guardar)
                    .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Guardar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void btn_NuevoPaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoPaisActionPerformed
            GUI_DetallePais gui_DetallePais = new GUI_DetallePais();
            gui_DetallePais.setModal(true);
            gui_DetallePais.setLocationRelativeTo(this);
            gui_DetallePais.setVisible(true);

            try {
                cargarComboBoxPaises();
                cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());

            } catch (BusinessServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
	}//GEN-LAST:event_btn_NuevoPaisActionPerformed

	private void btn_NuevaProvinciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaProvinciaActionPerformed
            GUI_DetalleProvincia gui_DetalleProvincia = new GUI_DetalleProvincia();
            gui_DetalleProvincia.setModal(true);
            gui_DetalleProvincia.setLocationRelativeTo(this);
            gui_DetalleProvincia.setVisible(true);

            try {
                cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());

            } catch (BusinessServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
	}//GEN-LAST:event_btn_NuevaProvinciaActionPerformed

	private void cmb_PaisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_PaisItemStateChanged
            if (cmb_Pais.getItemCount() > 0) {
                try {
                    cargarComboBoxProvinciasDelPais((Pais) cmb_Pais.getSelectedItem());

                } catch (BusinessServiceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
	}//GEN-LAST:event_cmb_PaisItemStateChanged

	private void btn_NuevaLocalidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevaLocalidadActionPerformed
            GUI_DetalleLocalidad gui_DetalleLocalidad = new GUI_DetalleLocalidad();
            gui_DetalleLocalidad.setModal(true);
            gui_DetalleLocalidad.setLocationRelativeTo(this);
            gui_DetalleLocalidad.setVisible(true);

            try {
                cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmb_Provincia.getSelectedItem());

            } catch (BusinessServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
	}//GEN-LAST:event_btn_NuevaLocalidadActionPerformed

	private void cmb_ProvinciaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmb_ProvinciaItemStateChanged
            if (cmb_Provincia.getItemCount() > 0) {
                try {
                    cargarComboBoxLocalidadesDeLaProvincia((Provincia) cmb_Provincia.getSelectedItem());

                } catch (BusinessServiceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                cmb_Localidad.removeAllItems();
            }
	}//GEN-LAST:event_cmb_ProvinciaItemStateChanged

	private void btn_GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GuardarActionPerformed
            try {
                if (operacion == TipoDeOperacion.ALTA) {
                    Transportista transportista = new Transportista();
                    transportista.setNombre(txt_Nombre.getText().trim());
                    transportista.setDireccion(txt_Direccion.getText().trim());
                    transportista.setLocalidad((Localidad) cmb_Localidad.getSelectedItem());                    
                    transportista.setTelefono(txt_Telefono.getText().trim());
                    transportista.setWeb(txt_Web.getText().trim());
                    transportista.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
                    transportistaService.guardar(transportista);
                    int respuesta = JOptionPane.showConfirmDialog(this,
                            "El transportista se guardó correctamente.\n¿Desea dar de alta otro transportista?",
                            "Aviso", JOptionPane.YES_NO_OPTION);
                    this.limpiarYRecargarComponentes();
                    if (respuesta == JOptionPane.NO_OPTION) {
                        this.dispose();
                    }
                }

                if (operacion == TipoDeOperacion.ACTUALIZACION) {
                    transportistaModificar.setNombre(txt_Nombre.getText().trim());
                    transportistaModificar.setDireccion(txt_Direccion.getText().trim());
                    transportistaModificar.setLocalidad((Localidad) cmb_Localidad.getSelectedItem());
                    transportistaModificar.setTelefono(txt_Telefono.getText().trim());
                    transportistaModificar.setWeb(txt_Web.getText().trim());
                    transportistaModificar.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
                    transportistaService.actualizar(transportistaModificar);
                    JOptionPane.showMessageDialog(this, "El transportista se modificó correctamente!",
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                }

            } catch (BusinessServiceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            }
	}//GEN-LAST:event_btn_GuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            this.cargarComboBoxPaises();
            if (operacion == TipoDeOperacion.ACTUALIZACION) {
                this.cargarTransportistaParaModificar();
            }

        } catch (BusinessServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Guardar;
    private javax.swing.JButton btn_NuevaLocalidad;
    private javax.swing.JButton btn_NuevaProvincia;
    private javax.swing.JButton btn_NuevoPais;
    private javax.swing.JComboBox cmb_Localidad;
    private javax.swing.JComboBox cmb_Pais;
    private javax.swing.JComboBox cmb_Provincia;
    private javax.swing.JLabel lbl_Direccion;
    private javax.swing.JLabel lbl_Localidad;
    private javax.swing.JLabel lbl_Nombre;
    private javax.swing.JLabel lbl_Pais;
    private javax.swing.JLabel lbl_Provincia;
    private javax.swing.JLabel lbl_Telefono;
    private javax.swing.JLabel lbl_Web;
    private javax.swing.JPanel panel1;
    private javax.swing.JTextField txt_Direccion;
    private javax.swing.JTextField txt_Nombre;
    private javax.swing.JTextField txt_Telefono;
    private javax.swing.JTextField txt_Web;
    // End of variables declaration//GEN-END:variables
}
