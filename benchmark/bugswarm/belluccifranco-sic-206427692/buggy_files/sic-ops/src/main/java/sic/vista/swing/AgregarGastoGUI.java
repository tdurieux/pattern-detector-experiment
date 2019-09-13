package sic.vista.swing;

import java.util.Date;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Empresa;
import sic.modelo.EmpresaActiva;
import sic.modelo.FormaDePago;
import sic.modelo.Gasto;
import sic.modelo.UsuarioActivo;

public class AgregarGastoGUI extends JDialog {
    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public AgregarGastoGUI(JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public Gasto construirGasto(String concepto, double monto, FormaDePago formaDePago) {
        Empresa empresa = EmpresaActiva.getInstance().getEmpresa();        
        Gasto gasto = new Gasto();
        gasto.setConcepto(concepto);
        gasto.setEliminado(false);
        gasto.setEmpresa(empresa);
        gasto.setFecha(new Date());
        gasto.setFormaDePago(formaDePago);
        gasto.setMonto(monto);
        gasto.setUsuario(UsuarioActivo.getInstance().getUsuario());
        return gasto;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Parametros = new javax.swing.JPanel();
        lbl_FormaDePago = new javax.swing.JLabel();
        lbl_Concepto = new javax.swing.JLabel();
        lbl_Monto = new javax.swing.JLabel();
        cmb_FormaDePago = new javax.swing.JComboBox<>();
        ftxt_Concepto = new javax.swing.JTextField();
        ftxt_Monto = new javax.swing.JFormattedTextField();
        pnl_Botones = new javax.swing.JPanel();
        lbl_Aceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nuevo Gasto");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lbl_FormaDePago.setText("Forma de Pago:");

        lbl_Concepto.setText("Concepto:");

        lbl_Monto.setText("Monto:");

        ftxt_Monto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxt_Monto.setText("0");
        ftxt_Monto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxt_MontoFocusGained(evt);
            }
        });

        javax.swing.GroupLayout pnl_ParametrosLayout = new javax.swing.GroupLayout(pnl_Parametros);
        pnl_Parametros.setLayout(pnl_ParametrosLayout);
        pnl_ParametrosLayout.setHorizontalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addComponent(lbl_FormaDePago)
                        .addGap(18, 18, 18)
                        .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addComponent(lbl_Monto)
                        .addGap(18, 18, 18)
                        .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                        .addComponent(lbl_Concepto)
                        .addGap(18, 18, 18)
                        .addComponent(ftxt_Concepto, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbl_Concepto, lbl_FormaDePago, lbl_Monto});

        pnl_ParametrosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmb_FormaDePago, ftxt_Concepto, ftxt_Monto});

        pnl_ParametrosLayout.setVerticalGroup(
            pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_ParametrosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_FormaDePago)
                    .addComponent(cmb_FormaDePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Concepto)
                    .addComponent(ftxt_Concepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_ParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Monto)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        lbl_Aceptar.setForeground(java.awt.Color.blue);
        lbl_Aceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Accept_16x16.png"))); // NOI18N
        lbl_Aceptar.setText("Aceptar");
        lbl_Aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lbl_AceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_BotonesLayout = new javax.swing.GroupLayout(pnl_Botones);
        pnl_Botones.setLayout(pnl_BotonesLayout);
        pnl_BotonesLayout.setHorizontalGroup(
            pnl_BotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_BotonesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_Aceptar)
                .addContainerGap())
        );
        pnl_BotonesLayout.setVerticalGroup(
            pnl_BotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_Aceptar)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(pnl_Botones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_Parametros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Botones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            for (FormaDePago formaDePago : RestClient.getRestTemplate().getForObject("/formas-de-pago/empresas/"
                    + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(), FormaDePago[].class)) {
                cmb_FormaDePago.addItem(formaDePago);
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formWindowOpened

    private void lbl_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lbl_AceptarActionPerformed
        if (ftxt_Monto.getValue() == null) {
            ftxt_Monto.setValue(0.00);
        }
        if (ftxt_Concepto.getText() == null) {
            ftxt_Concepto.setText("");
        }
        try {
            RestClient.getRestTemplate().postForObject("/gastos", this.construirGasto(ftxt_Concepto.getText(),
                    Double.parseDouble(ftxt_Monto.getValue().toString()),
                    (FormaDePago) cmb_FormaDePago.getSelectedItem()), Gasto.class);
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.dispose();
    }//GEN-LAST:event_lbl_AceptarActionPerformed

    private void ftxt_MontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxt_MontoFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxt_Monto.selectAll();
        });
    }//GEN-LAST:event_ftxt_MontoFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<FormaDePago> cmb_FormaDePago;
    private javax.swing.JTextField ftxt_Concepto;
    private javax.swing.JFormattedTextField ftxt_Monto;
    private javax.swing.JButton lbl_Aceptar;
    private javax.swing.JLabel lbl_Concepto;
    private javax.swing.JLabel lbl_FormaDePago;
    private javax.swing.JLabel lbl_Monto;
    private javax.swing.JPanel pnl_Botones;
    private javax.swing.JPanel pnl_Parametros;
    // End of variables declaration//GEN-END:variables
}
