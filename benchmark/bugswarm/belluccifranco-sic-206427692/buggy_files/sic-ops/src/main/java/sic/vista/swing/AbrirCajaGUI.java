package sic.vista.swing;

import java.util.Calendar;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.Caja;
import sic.modelo.EmpresaActiva;
import sic.modelo.UsuarioActivo;
import sic.modelo.EstadoCaja;

public class AbrirCajaGUI extends JDialog {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public AbrirCajaGUI(boolean modal) {
        this.setModal(true);
        initComponents();
        this.setModelSpinner();
        ImageIcon iconoVentana = new ImageIcon(AbrirCajaGUI.class.getResource("/sic/icons/Caja_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }
    
    private void setModelSpinner() {
        SpinnerModel spinnerModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), 00, 23, 1);
        this.spinner_Hora.setModel(spinnerModel);
        spinnerModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.MINUTE), 00, 59, 1);
        this.spinner_Minutos.setModel(spinnerModel);
    }

    private Caja construirCaja(double monto) {
        Caja caja = new Caja();
        caja.setEstado(EstadoCaja.ABIERTA);
        caja.setObservacion("Apertura De Caja");
        caja.setEmpresa(EmpresaActiva.getInstance().getEmpresa());
        Calendar corte = Calendar.getInstance();
        corte.set(Calendar.HOUR_OF_DAY, (int) spinner_Hora.getValue());
        corte.set(Calendar.MINUTE, (int) spinner_Minutos.getValue());
        caja.setFechaCorteInforme(corte.getTime());
        caja.setSaldoInicial(monto);
        caja.setSaldoFinal(0);
        caja.setSaldoReal(0);
        caja.setUsuarioAbreCaja(UsuarioActivo.getInstance().getUsuario());
        return caja;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_separador = new javax.swing.JPanel();
        spinner_Hora = new javax.swing.JSpinner();
        spinner_Minutos = new javax.swing.JSpinner();
        lbl_CorteControl = new javax.swing.JLabel();
        ftxt_Monto = new javax.swing.JFormattedTextField();
        lbl_monto = new javax.swing.JLabel();
        btn_AbrirCaja = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Abrir Caja");
        setResizable(false);

        lbl_CorteControl.setText("Hora de Control:");

        ftxt_Monto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        ftxt_Monto.setText("0");
        ftxt_Monto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ftxt_MontoFocusGained(evt);
            }
        });
        ftxt_Monto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ftxt_MontoKeyTyped(evt);
            }
        });

        lbl_monto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_monto.setText("Monto:");

        btn_AbrirCaja.setForeground(java.awt.Color.blue);
        btn_AbrirCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AbrirCaja_16x16.png"))); // NOI18N
        btn_AbrirCaja.setText("Abrir Caja");
        btn_AbrirCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AbrirCajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_separadorLayout = new javax.swing.GroupLayout(pnl_separador);
        pnl_separador.setLayout(pnl_separadorLayout);
        pnl_separadorLayout.setHorizontalGroup(
            pnl_separadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_separadorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_separadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_monto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_CorteControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_separadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_AbrirCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnl_separadorLayout.createSequentialGroup()
                        .addComponent(spinner_Hora, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinner_Minutos))
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_separadorLayout.setVerticalGroup(
            pnl_separadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_separadorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_separadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(spinner_Hora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinner_Minutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_CorteControl, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_separadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ftxt_Monto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_monto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_AbrirCaja)
                .addContainerGap())
        );

        pnl_separadorLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ftxt_Monto, lbl_CorteControl, lbl_monto, spinner_Hora, spinner_Minutos});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_separador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_separador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ftxt_MontoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ftxt_MontoKeyTyped
        char tecla = evt.getKeyChar();
        if (Character.isLetter(tecla)) {
            evt.consume();
        }
    }//GEN-LAST:event_ftxt_MontoKeyTyped

    private void btn_AbrirCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AbrirCajaActionPerformed
        if (ftxt_Monto.getValue() == null) {
            ftxt_Monto.setValue(0);
        }
        try {
            CajaGUI abrirCaja = new CajaGUI(RestClient.getRestTemplate()
                    .postForObject("/cajas",
                    this.construirCaja(((Number) ftxt_Monto.getValue()).doubleValue()),
                    Caja.class));
            abrirCaja.setModal(true);
            abrirCaja.setLocationRelativeTo(this);
            abrirCaja.setVisible(true);
            this.dispose();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_AbrirCajaActionPerformed

    private void ftxt_MontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ftxt_MontoFocusGained
        SwingUtilities.invokeLater(() -> {
            ftxt_Monto.selectAll();
        });
    }//GEN-LAST:event_ftxt_MontoFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AbrirCaja;
    private javax.swing.JFormattedTextField ftxt_Monto;
    private javax.swing.JLabel lbl_CorteControl;
    private javax.swing.JLabel lbl_monto;
    private javax.swing.JPanel pnl_separador;
    private javax.swing.JSpinner spinner_Hora;
    private javax.swing.JSpinner spinner_Minutos;
    // End of variables declaration//GEN-END:variables

}
