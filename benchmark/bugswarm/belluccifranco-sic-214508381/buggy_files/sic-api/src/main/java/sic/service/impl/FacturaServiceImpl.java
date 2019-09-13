package sic.service.impl;

import sic.modelo.BusquedaFacturaCompraCriteria;
import sic.modelo.BusquedaFacturaVentaCriteria;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Cliente;
import sic.modelo.ConfiguracionDelSistema;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.Pago;
import sic.modelo.Pedido;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.RenglonFactura;
import sic.service.IConfiguracionDelSistemaService;
import sic.service.IFacturaService;
import sic.service.IPagoService;
import sic.service.IPedidoService;
import sic.service.IProductoService;
import sic.modelo.Movimiento;
import sic.service.BusinessServiceException;
import sic.service.ServiceException;
import sic.modelo.TipoDeOperacion;
import sic.util.Utilidades;
import sic.util.Validator;
import sic.repository.FacturaRepository;

@Service
public class FacturaServiceImpl implements IFacturaService {

    private final FacturaRepository facturaRepository;
    private final IProductoService productoService;
    private final IConfiguracionDelSistemaService configuracionDelSistemaService;
    private final IPedidoService pedidoService;
    private final IPagoService pagoService;
    private static final Logger LOGGER = Logger.getLogger(FacturaServiceImpl.class.getPackage().getName());
    private static final char FACTURA_A = 'A';
    private static final char FACTURA_B = 'B';

    @Autowired
    @Lazy
    public FacturaServiceImpl(FacturaRepository facturaRepository,
            IProductoService productoService,
            IConfiguracionDelSistemaService configuracionDelSistemaService,
            IPedidoService pedidoService,
            IPagoService pagoService) {
        this.facturaRepository = facturaRepository;
        this.productoService = productoService;
        this.configuracionDelSistemaService = configuracionDelSistemaService;
        this.pedidoService = pedidoService;
        this.pagoService = pagoService;
    }
    
    @Override
    public Factura getFacturaPorId(Long id_Factura) {
        Factura factura = facturaRepository.findOne(id_Factura);
        if (factura == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_eliminada"));
        }
        return factura;
    }

    @Override
    public List<Factura> getFacturasDelPedido(Long idPedido) {
        return facturaRepository.findAllByPedidoAndEliminada(pedidoService.getPedidoPorId(idPedido), false);
    }
    
    @Override
    public String[] getTipoFacturaCompra(Empresa empresa, Proveedor proveedor) {
        //cuando la Empresa discrimina IVA
        if (empresa.getCondicionIVA().isDiscriminaIVA()) {
            if (proveedor.getCondicionIVA().isDiscriminaIVA()) {
                //cuando la Empresa discrimina IVA y el Proveedor tambien
                String[] tiposPermitidos = new String[3];
                tiposPermitidos[0] = "Factura A";
                tiposPermitidos[1] = "Factura B";
                tiposPermitidos[2] = "Factura X";
                return tiposPermitidos;
            } else {
                //cuando la Empresa discrminina IVA y el Proveedor NO
                String[] tiposPermitidos = new String[2];
                tiposPermitidos[0] = "Factura C";
                tiposPermitidos[1] = "Factura X";
                return tiposPermitidos;
            }
        } else {
            //cuando la Empresa NO discrimina IVA                
            if (proveedor.getCondicionIVA().isDiscriminaIVA()) {
                //cuando Empresa NO discrimina IVA y el Proveedor SI
                String[] tiposPermitidos = new String[2];
                tiposPermitidos[0] = "Factura B";
                tiposPermitidos[1] = "Factura X";
                return tiposPermitidos;
            } else {
                //cuando la Empresa NO discrminina IVA y el Proveedor tampoco
                String[] tiposPermitidos = new String[2];
                tiposPermitidos[0] = "Factura C";
                tiposPermitidos[1] = "Factura X";
                return tiposPermitidos;
            }
        }
    }

    @Override
    public String[] getTipoFacturaVenta(Empresa empresa, Cliente cliente) {
        //cuando la Empresa discrimina IVA
        if (empresa.getCondicionIVA().isDiscriminaIVA()) {
            if (cliente.getCondicionIVA().isDiscriminaIVA()) {
                //cuando la Empresa discrimina IVA y el Cliente tambien
                String[] tiposPermitidos = new String[5];
                tiposPermitidos[0] = "Factura A";
                tiposPermitidos[1] = "Factura B";
                tiposPermitidos[2] = "Factura X";
                tiposPermitidos[3] = "Factura Y";
                tiposPermitidos[4] = "Pedido";
                return tiposPermitidos;
            } else {
                //cuando la Empresa discrminina IVA y el Cliente NO
                String[] tiposPermitidos = new String[4];
                tiposPermitidos[0] = "Factura B";
                tiposPermitidos[1] = "Factura X";
                tiposPermitidos[2] = "Factura Y";
                tiposPermitidos[3] = "Pedido";
                return tiposPermitidos;
            }
        } else {
            //cuando la Empresa NO discrimina IVA
            if (cliente.getCondicionIVA().isDiscriminaIVA()) {
                //cuando Empresa NO discrimina IVA y el Cliente SI
                String[] tiposPermitidos = new String[4];
                tiposPermitidos[0] = "Factura C";
                tiposPermitidos[1] = "Factura X";
                tiposPermitidos[2] = "Factura Y";
                tiposPermitidos[3] = "Pedido";
                return tiposPermitidos;
            } else {
                //cuando la Empresa NO discrminina IVA y el Cliente tampoco
                String[] tiposPermitidos = new String[4];
                tiposPermitidos[0] = "Factura C";
                tiposPermitidos[1] = "Factura X";
                tiposPermitidos[2] = "Factura Y";
                tiposPermitidos[3] = "Pedido";
                return tiposPermitidos;
            }
        }
    }

    @Override
    public char[] getTiposFacturaSegunEmpresa(Empresa empresa) {
        if (empresa.getCondicionIVA().isDiscriminaIVA()) {
            char[] tiposPermitidos = new char[4];
            tiposPermitidos[0] = 'A';
            tiposPermitidos[1] = 'B';
            tiposPermitidos[2] = 'X';
            tiposPermitidos[3] = 'Y';
            return tiposPermitidos;
        } else {
            char[] tiposPermitidos = new char[3];
            tiposPermitidos[0] = 'C';
            tiposPermitidos[1] = 'X';
            tiposPermitidos[2] = 'Y';
            return tiposPermitidos;
        }
    }

    @Override
    public List<RenglonFactura> getRenglonesDeLaFactura(Long id_Factura) {
        return this.getFacturaPorId(id_Factura).getRenglones();
    }  

    @Override
    public String getTipoFactura(Factura factura) {
        String tipoComprobante = new String();
        switch (factura.getTipoFactura()) {
            case 'A':
                tipoComprobante = "Factura A";
                break;
            case 'B':
                tipoComprobante = "Factura B";
                break;
            case 'C':
                tipoComprobante = "Factura C";
                break;
            case 'Y':
                tipoComprobante = "Factura Y";
                break;
            case 'X':
                tipoComprobante = "Factura X";
                break;
            case 'P':
                tipoComprobante = "Pedido";
                break;
        }
        return tipoComprobante;
    }

    @Override
    public Movimiento getTipoMovimiento(Factura factura) {
        if (factura instanceof FacturaVenta) {
            return Movimiento.VENTA;
        } else {
            return Movimiento.COMPRA;
        }
    }

    @Override
    public List<FacturaCompra> buscarFacturaCompra(BusquedaFacturaCompraCriteria criteria) {
        //Empresa
        if (criteria.getEmpresa() == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        //Fecha de Factura        
        if (criteria.isBuscaPorFecha() == true & (criteria.getFechaDesde() == null | criteria.getFechaHasta() == null)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_fechas_busqueda_invalidas"));
        }
        if (criteria.isBuscaPorFecha() == true) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(criteria.getFechaDesde());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            criteria.setFechaDesde(cal.getTime());
            cal.setTime(criteria.getFechaHasta());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            criteria.setFechaHasta(cal.getTime());
        }
        //Proveedor
        if (criteria.isBuscaPorProveedor() == true && criteria.getProveedor() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_proveedor_vacio"));
        }
        return facturaRepository.buscarFacturasCompra(criteria);
    }

    @Override
    public List<FacturaVenta> buscarFacturaVenta(BusquedaFacturaVentaCriteria criteria) {
        //Empresa
        if(criteria.getEmpresa() == null ) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        //Fecha de Factura        
        if (criteria.isBuscaPorFecha() == true && (criteria.getFechaDesde() == null || criteria.getFechaHasta() == null)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_fechas_busqueda_invalidas"));
        }
        if (criteria.isBuscaPorFecha() == true) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(criteria.getFechaDesde());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            criteria.setFechaDesde(cal.getTime());
            cal.setTime(criteria.getFechaHasta());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            criteria.setFechaHasta(cal.getTime());
        }
        //Cliente
        if (criteria.isBuscaCliente() == true && criteria.getCliente() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_cliente_vacio"));
        }
        //Usuario
        if (criteria.isBuscaUsuario() == true && criteria.getUsuario() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_usuario_vacio"));
        }
        return facturaRepository.buscarFacturasVenta(criteria);
    }

    private Factura procesarFactura(Factura factura) {
        factura.setEliminada(false);
        if (factura instanceof FacturaVenta) {
            factura.setNumSerie(1); //Serie de la factura hardcodeada a 1
            factura.setNumFactura(this.calcularNumeroFactura(factura.getTipoFactura(),
                    factura.getNumSerie(), factura.getEmpresa().getId_Empresa()));
        }
        this.validarFactura(factura);    
        return factura;
    }
    
    @Override
    @Transactional
    public List<Factura> guardar(List<Factura> facturas, Long idPedido) {
        List<Factura> facturasProcesadas = new ArrayList<>();
        facturas.forEach((f) -> {
            productoService.actualizarStock(f, TipoDeOperacion.ALTA);
        });
        if (idPedido != null) {
            Pedido pedido = pedidoService.getPedidoPorId(idPedido);
            facturas.forEach((f) -> {
                f.setPedido(pedido);
            });
            pedido.setFacturas(facturasProcesadas);
            pedidoService.actualizar(pedido);
            facturasProcesadas.stream().forEach((f) -> {
                this.actualizarFacturaEstadoPagada(f);
                LOGGER.warn("La Factura " + f + " se guardó correctamente.");
            });
            pedidoService.actualizarEstadoPedido(pedido, facturasProcesadas);
        } else {
            facturasProcesadas = new ArrayList<>();
            for (Factura f : facturas) {
                List<Pago> pagosFactura = f.getPagos();
                f.setPagos(null);
                Factura facturaGuardada = facturaRepository.save(this.procesarFactura(f));
                facturasProcesadas.add(facturaGuardada);
                LOGGER.warn("La Factura " + facturaGuardada + " se guardó correctamente.");
                if (!pagosFactura.isEmpty()) {
                    pagosFactura.forEach((p) -> {
                        pagoService.guardar(p);
                    });
                    f.setPagos(pagosFactura);
                }
                this.actualizarFacturaEstadoPagada(facturaGuardada);
            }            
        }
        return facturasProcesadas;
    }

    @Override
    @Transactional
    public void actualizar(Factura factura) {
        facturaRepository.save(factura);
    }

    @Override
    @Transactional
    public void eliminar(long[] idsFactura) {
        for (long idFactura : idsFactura) {
            Factura factura = this.getFacturaPorId(idFactura);
            factura.setEliminada(true);
            this.eliminarPagosDeFactura(factura);
            productoService.actualizarStock(factura, TipoDeOperacion.ELIMINACION);            
            if (factura.getPedido() != null) {
                List<Factura> facturas = new ArrayList<>();
                facturas.add(factura);
                pedidoService.actualizarEstadoPedido(factura.getPedido(), facturas);                
            }            
        }       
    }

    private void eliminarPagosDeFactura(Factura factura) {
        pagoService.getPagosDeLaFactura(factura.getId_Factura()).stream().forEach((pago) -> {
            pagoService.eliminar(pago.getId_Pago());
        });
    }

    private void validarFactura(Factura factura) {
        //Entrada de Datos
        if (factura.getFechaVencimiento() != null) {
            //quitamos la parte de hora de la Fecha de Vencimiento
            Calendar calFechaVencimiento = new GregorianCalendar();
            calFechaVencimiento.setTime(factura.getFechaVencimiento());
            calFechaVencimiento.set(Calendar.HOUR, 0);
            calFechaVencimiento.set(Calendar.MINUTE, 0);
            calFechaVencimiento.set(Calendar.SECOND, 0);
            calFechaVencimiento.set(Calendar.MILLISECOND, 0);
            factura.setFechaVencimiento(calFechaVencimiento.getTime());
            //quitamos la parte de hora de la Fecha Actual para poder comparar correctamente
            Calendar calFechaDeFactura = new GregorianCalendar();
            calFechaDeFactura.setTime(factura.getFecha());
            calFechaDeFactura.set(Calendar.HOUR, 0);
            calFechaDeFactura.set(Calendar.MINUTE, 0);
            calFechaDeFactura.set(Calendar.SECOND, 0);
            calFechaDeFactura.set(Calendar.MILLISECOND, 0);
            if (Validator.compararFechas(factura.getFechaVencimiento(), calFechaDeFactura.getTime()) > 0) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_factura_fecha_invalida"));
            }
        }
        //Requeridos
        if (factura.getFecha() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_fecha_vacia"));
        }
        if (factura.getTipoFactura() == ' ') {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_tipo_factura_vacia"));
        }
        if (factura.getTransportista() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_transportista_vacio"));
        }
        if (factura.getRenglones() == null || factura.getRenglones().isEmpty()) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_renglones_vacio"));
        }
        if (factura.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_empresa_vacia"));
        }
        if (factura instanceof FacturaCompra) {
            FacturaCompra facturaCompra = (FacturaCompra) factura;
            if (facturaCompra.getProveedor() == null) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_factura_proveedor_vacio"));
            }
        }
        if (factura instanceof FacturaVenta) {
            FacturaVenta facturaVenta = (FacturaVenta) factura;
            if (facturaVenta.getCliente() == null) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_factura_cliente_vacio"));
            }
            if (facturaVenta.getUsuario() == null) {
                throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                        .getString("mensaje_factura_usuario_vacio"));
            }
        }
    }

    @Override
    @Transactional
    public Factura actualizarFacturaEstadoPagada(Factura factura) {
        double totalFactura = Utilidades.round(factura.getTotal(), 2);
        double totalPagado = Utilidades.round(this.getTotalPagado(factura), 2);
        if (totalPagado >= totalFactura) {               
            factura.setPagada(true);
        } else {
            factura.setPagada(false);
        }
        return factura;
    }
    
    @Override
    public double getTotalPagado(Factura factura) {
        double pagado = 0.0;
        for (Pago pago : pagoService.getPagosDeLaFactura(factura.getId_Factura())) {
            pagado = pagado + pago.getMonto();
        }
        return pagado;
    }
    
    @Override
    public List<Factura> ordenarFacturasPorFechaAsc(List<Factura> facturas) {
        Comparator comparador = (Comparator<Factura>) (Factura f1, Factura f2) -> f1.getFecha().compareTo(f2.getFecha());
        facturas.sort(comparador);
        return facturas;
    }

    @Override
    public boolean validarFacturasParaPagoMultiple(List<Factura> facturas, Movimiento movimiento) {
        return (this.validarClienteProveedorParaPagosMultiples(facturas, movimiento)
                && this.validarFacturasImpagasParaPagoMultiple(facturas));
    }

    @Override
    public boolean validarClienteProveedorParaPagosMultiples(List<Factura> facturas, Movimiento movimiento) {
        boolean resultado = true;
        if (movimiento == Movimiento.VENTA) {
            if (facturas != null) {
                if (facturas.isEmpty()) {
                    resultado = false;
                } else {
                    Cliente cliente = ((FacturaVenta) facturas.get(0)).getCliente();
                    for (Factura factura : facturas) {
                        if (!cliente.equals(((FacturaVenta) factura).getCliente())) {
                            resultado = false;
                            break;
                        }
                    }
                }
            } else {
                resultado = false;
            }
        }
        if (movimiento == Movimiento.COMPRA) {
            if (facturas != null) {
                if (facturas.isEmpty()) {
                    resultado = false;
                } else {
                    Proveedor proveedor = ((FacturaCompra) facturas.get(0)).getProveedor();
                    for (Factura factura : facturas) {
                        if (!proveedor.equals(((FacturaCompra) factura).getProveedor())) {
                            resultado = false;
                            break;
                        }
                    }
                }
            } else {
                resultado = false;
            }
        }
        return resultado;
    }

    @Override
    public boolean validarFacturasImpagasParaPagoMultiple(List<Factura> facturas) {
        boolean resultado = true;
        if (facturas != null) {
            if (facturas.isEmpty()) {
                resultado = false;
            } else {
                for (Factura factura : facturas) {
                    if (factura.isPagada()) {
                        resultado = false;
                        break;
                    }
                }
            }
        } else {
            resultado = false;
        }
        return resultado;
    }

    @Override
    public boolean validarCantidadMaximaDeRenglones(int cantidad, Empresa empresa) {
        ConfiguracionDelSistema cds = configuracionDelSistemaService
                .getConfiguracionDelSistemaPorEmpresa(empresa);
        int max = cds.getCantidadMaximaDeRenglonesEnFactura();
        return cantidad < max;
    }

    @Override
    public double calcularSubTotal(double[] importes) {
        double resultado = 0;
        for (double importe : importes) {
            resultado += importe;
        }
        return resultado;
    }

    @Override
    public double calcularDescuento_neto(double subtotal, double descuento_porcentaje) {
        double resultado = 0;
        if (descuento_porcentaje != 0) {
            resultado = (subtotal * descuento_porcentaje) / 100;
        }
        return resultado;
    }

    @Override
    public double calcularRecargo_neto(double subtotal, double recargo_porcentaje) {
        double resultado = 0;
        if (recargo_porcentaje != 0) {
            resultado = (subtotal * recargo_porcentaje) / 100;
        }
        return resultado;
    }

    @Override
    public double calcularSubTotal_neto(double subtotal, double recargo_neto, double descuento_neto) {
        return (subtotal + recargo_neto - descuento_neto);
    }

    @Override
    public double calcularIva_neto(String tipoDeFactura, double descuento_porcentaje, double recargo_porcentaje,
            double[] importes, double[] ivaPorcentaje, double iva_porcentaje) {

        double resultado = 0;
        int indice = importes.length;
        if (tipoDeFactura.charAt(tipoDeFactura.length() - 1) == 'A') {
            for (int i = 0 ; i < indice; i++) {
                double descuento = 0;
                if (descuento_porcentaje != 0) {                    
                    descuento = (importes[i] * descuento_porcentaje) / 100;
                }
                double recargo = 0;
                if (recargo_porcentaje != 0) {                    
                    recargo = (importes[i] * recargo_porcentaje) / 100;
                }
                double iva_neto = 0;
                if (ivaPorcentaje[i] == iva_porcentaje) {                    
                    iva_neto = ((importes[i] + recargo - descuento) * ivaPorcentaje[i]) / 100;
                }
                resultado += iva_neto;
            }
        }
        return resultado;
    }

    @Override
    public double calcularImpInterno_neto(String tipoDeFactura, double descuento_porcentaje,
            double recargo_porcentaje, double[] importes, double [] impuestoPorcentajes) {

        double resultado = 0;
        if (tipoDeFactura.charAt(tipoDeFactura.length() - 1) == 'A') {
            int longitudImportes = importes.length;
            int longitudImpuestos = impuestoPorcentajes.length;
            if (longitudImportes == longitudImpuestos) {
                for (int i = 0; i < longitudImportes; i++) {                
                double descuento = 0;
                if (descuento_porcentaje != 0) {
                    descuento = (importes[i] * descuento_porcentaje) / 100;
                }
                double recargo = 0;
                if (recargo_porcentaje != 0) {
                    recargo = (importes[i]  * recargo_porcentaje) / 100;
                }
                double impInterno_neto = ((importes[i]  + recargo - descuento) * impuestoPorcentajes[i]) / 100;
                resultado += impInterno_neto;
                }
            }
        }
        return resultado;
    }

    @Override
    public double calcularTotal(double subTotal, double descuento_neto, double recargo_neto,
            double iva105_neto, double iva21_neto, double impInterno_neto) {

        double resultado;
        resultado = (subTotal + recargo_neto - descuento_neto) + iva105_neto + iva21_neto + impInterno_neto;
        return resultado;
    }

    @Override
    public double calcularTotalFacturadoVenta(BusquedaFacturaVentaCriteria criteria) {
        //Empresa
        if(criteria.getEmpresa() == null ) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        //Fecha de Factura        
        if (criteria.isBuscaPorFecha() == true && (criteria.getFechaDesde() == null || criteria.getFechaHasta() == null)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_fechas_busqueda_invalidas"));
        }
        if (criteria.isBuscaPorFecha() == true) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(criteria.getFechaDesde());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            criteria.setFechaDesde(cal.getTime());
            cal.setTime(criteria.getFechaHasta());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            criteria.setFechaHasta(cal.getTime());
        }
        //Cliente
        if (criteria.isBuscaCliente() == true && criteria.getCliente() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_cliente_vacio"));
        }
        //Usuario
        if (criteria.isBuscaUsuario() == true && criteria.getUsuario() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_usuario_vacio"));
        }
        return facturaRepository.calcularTotalFacturadoVenta(criteria);
    }

    @Override
    public double calcularTotalFacturadoCompra(BusquedaFacturaCompraCriteria criteria) {
        //Empresa
        if (criteria.getEmpresa() == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        //Fecha de Factura        
        if (criteria.isBuscaPorFecha() == true & (criteria.getFechaDesde() == null | criteria.getFechaHasta() == null)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_fechas_busqueda_invalidas"));
        }
        if (criteria.isBuscaPorFecha() == true) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(criteria.getFechaDesde());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            criteria.setFechaDesde(cal.getTime());
            cal.setTime(criteria.getFechaHasta());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            criteria.setFechaHasta(cal.getTime());
        }
        //Proveedor
        if (criteria.isBuscaPorProveedor() == true && criteria.getProveedor() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_proveedor_vacio"));
        }
        return facturaRepository.calcularTotalFacturadoCompra(criteria);
    }

    @Override
    public double calcularIVA_Venta(BusquedaFacturaVentaCriteria criteria) {
        //Empresa
        if(criteria.getEmpresa() == null ) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        //Fecha de Factura        
        if (criteria.isBuscaPorFecha() == true && (criteria.getFechaDesde() == null || criteria.getFechaHasta() == null)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_fechas_busqueda_invalidas"));
        }
        if (criteria.isBuscaPorFecha() == true) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(criteria.getFechaDesde());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            criteria.setFechaDesde(cal.getTime());
            cal.setTime(criteria.getFechaHasta());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            criteria.setFechaHasta(cal.getTime());
        }
        //Cliente
        if (criteria.isBuscaCliente() == true && criteria.getCliente() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_cliente_vacio"));
        }
        //Usuario
        if (criteria.isBuscaUsuario() == true && criteria.getUsuario() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_usuario_vacio"));
        }
        char[] tipoFactura = {FACTURA_A, FACTURA_B};
        return facturaRepository.calcularIVA_Venta(criteria, tipoFactura);
    }

    @Override
    public double calcularIVA_Compra(BusquedaFacturaCompraCriteria criteria) {
        //Empresa
        if (criteria.getEmpresa() == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        //Fecha de Factura        
        if (criteria.isBuscaPorFecha() == true & (criteria.getFechaDesde() == null | criteria.getFechaHasta() == null)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_fechas_busqueda_invalidas"));
        }
        if (criteria.isBuscaPorFecha() == true) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(criteria.getFechaDesde());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            criteria.setFechaDesde(cal.getTime());
            cal.setTime(criteria.getFechaHasta());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            criteria.setFechaHasta(cal.getTime());
        }
        //Proveedor
        if (criteria.isBuscaPorProveedor() == true && criteria.getProveedor() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_proveedor_vacio"));
        }
        char[] tipoFactura = {FACTURA_A};
        return facturaRepository.calcularIVA_Compra(criteria, tipoFactura);
    }

    @Override
    public double calcularGananciaTotal(BusquedaFacturaVentaCriteria criteria) {
        //Empresa
        if (criteria.getEmpresa() == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_empresa_no_existente"));
        }
        //Fecha de Factura        
        if (criteria.isBuscaPorFecha() == true && (criteria.getFechaDesde() == null || criteria.getFechaHasta() == null)) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_fechas_busqueda_invalidas"));
        }
        if (criteria.isBuscaPorFecha() == true) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(criteria.getFechaDesde());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            criteria.setFechaDesde(cal.getTime());
            cal.setTime(criteria.getFechaHasta());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            criteria.setFechaHasta(cal.getTime());
        }
        //Cliente
        if (criteria.isBuscaCliente() == true && criteria.getCliente() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_cliente_vacio"));
        }
        //Usuario
        if (criteria.isBuscaUsuario() == true && criteria.getUsuario() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_factura_usuario_vacio"));
        }
        return facturaRepository.calcularGananciaTotal(criteria);
    }

    @Override
    public double calcularIVA_neto(Movimiento movimiento, Producto producto, double descuento_neto) {
        double resultado = 0;
        if (movimiento == Movimiento.COMPRA) {
            resultado = ((producto.getPrecioCosto() - descuento_neto) * producto.getIva_porcentaje()) / 100;
        }
        if (movimiento == Movimiento.VENTA) {
            resultado = ((producto.getPrecioVentaPublico() - descuento_neto) * producto.getIva_porcentaje()) / 100;
        }
        return resultado;
    }

    @Override
    public double calcularImpInterno_neto(Movimiento movimiento, Producto producto, double descuento_neto) {
        double resultado = 0;
        if (movimiento == Movimiento.COMPRA) {
            resultado = ((producto.getPrecioCosto() - descuento_neto) * producto.getImpuestoInterno_porcentaje()) / 100;
        }
        if (movimiento == Movimiento.VENTA) {
            resultado = ((producto.getPrecioVentaPublico() - descuento_neto) * producto.getImpuestoInterno_porcentaje()) / 100;
        }
        return resultado;
    }

    @Override
    public double calcularPrecioUnitario(Movimiento movimiento, String tipoDeFactura, Producto producto) {
        double iva_resultado;
        double impInterno_resultado;
        double resultado = 0;
        if (movimiento == Movimiento.COMPRA) {
            if (tipoDeFactura.equals("Factura A") || tipoDeFactura.equals("Factura X")) {
                resultado = producto.getPrecioCosto();
            } else {
                iva_resultado = (producto.getPrecioCosto() * producto.getIva_porcentaje()) / 100;
                impInterno_resultado = (producto.getPrecioCosto() * producto.getImpuestoInterno_porcentaje()) / 100;
                resultado = producto.getPrecioCosto() + iva_resultado + impInterno_resultado;
            }
        }
        if (movimiento == Movimiento.VENTA) {
            if (tipoDeFactura.equals("Factura A") || tipoDeFactura.equals("Factura X")) {
                resultado = producto.getPrecioVentaPublico();
            } else if (tipoDeFactura.equals("Factura Y")) {
                iva_resultado = (producto.getPrecioVentaPublico() * producto.getIva_porcentaje() / 2) / 100;
                impInterno_resultado = (producto.getPrecioVentaPublico() * producto.getImpuestoInterno_porcentaje()) / 100;
                resultado = producto.getPrecioVentaPublico() + iva_resultado + impInterno_resultado;
            } else {
                resultado = producto.getPrecioLista();
            }
        }
        if (movimiento == Movimiento.PEDIDO) {
            resultado = producto.getPrecioLista();
        }
        return resultado;
    }

    @Override
    public long calcularNumeroFactura(char tipoDeFactura, long serie, long idEmpresa) {
        Long numeroFactura = facturaRepository.buscarMayorNumFacturaSegunTipo(tipoDeFactura, serie, idEmpresa);
        if (numeroFactura == null) {
            return 1; // No existe ninguna Factura anterior
        } else {
            return 1 + numeroFactura;
        }
    }

    @Override
    public double calcularVuelto(double importeAPagar, double importeAbonado) {
        if (importeAbonado <= importeAPagar) {
            return 0;
        } else {
            return importeAbonado - importeAPagar;
        }
    }

    @Override
    public double calcularImporte(double cantidad, double precioUnitario, double descuento_neto) {
        double resultado = (precioUnitario - descuento_neto) * cantidad;
        return resultado;
    }

    @Override
    public byte[] getReporteFacturaVenta(Factura factura) {
        ClassLoader classLoader = FacturaServiceImpl.class.getClassLoader();
        InputStream isFileReport = classLoader.getResourceAsStream("sic/vista/reportes/FacturaVenta.jasper");
        Map params = new HashMap();
        ConfiguracionDelSistema cds = configuracionDelSistemaService
                .getConfiguracionDelSistemaPorEmpresa(factura.getEmpresa());
        params.put("preImpresa", cds.isUsarFacturaVentaPreImpresa());
        String formasDePago = "";
        formasDePago = pagoService.getPagosDeLaFactura(factura.getId_Factura())
                                  .stream()
                                  .map((pago) -> pago.getFormaDePago().getNombre() + " -")
                                  .reduce(formasDePago, String::concat);
        params.put("formasDePago", formasDePago);
        params.put("facturaVenta", factura);
        params.put("nroComprobante", factura.getNumSerie() + "-" + factura.getNumFactura());
        params.put("logo", Utilidades.convertirByteArrayIntoImage(factura.getEmpresa().getLogo()));
        List<RenglonFactura> renglones = this.getRenglonesDeLaFactura(factura.getId_Factura());
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(renglones);
         try {
            return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(isFileReport, params, ds));
        } catch (JRException ex) {
             throw new ServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_error_reporte"), ex);
        }
    }

    @Override
    public List<RenglonFactura> convertirRenglonesPedidoARenglonesFactura(Pedido pedido, String tipoDeComprobante) {
        List<RenglonFactura> renglonesRestantes = new ArrayList<>();
        HashMap<Long, RenglonFactura> renglonesDeFacturas = pedidoService.getRenglonesFacturadosDelPedido(pedido.getId_Pedido());
        List<Factura> facturasDePedido = this.getFacturasDelPedido(pedido.getId_Pedido());
        if (facturasDePedido != null) {
            pedido.getRenglones().stream().forEach((renglon) -> {
                if (renglonesDeFacturas.containsKey(renglon.getProducto().getId_Producto())) {
                    if (renglon.getCantidad() > renglonesDeFacturas.get(renglon.getProducto().getId_Producto()).getCantidad()) {
                        renglonesRestantes.add(this.calcularRenglon(tipoDeComprobante,
                                Movimiento.VENTA, renglon.getCantidad() - renglonesDeFacturas.get(renglon.getProducto().getId_Producto()).getCantidad(),
                                renglon.getProducto().getId_Producto(), renglon.getDescuento_porcentaje()));
                    }
                } else {
                    renglonesRestantes.add(this.calcularRenglon(tipoDeComprobante, Movimiento.VENTA,
                            renglon.getCantidad(), renglon.getProducto().getId_Producto(), renglon.getDescuento_porcentaje()));
                }
            });
        } else {
            pedido.getRenglones().stream().forEach((renglon) -> {
                renglonesRestantes.add(this.calcularRenglon(tipoDeComprobante, Movimiento.VENTA,
                        renglon.getCantidad(), renglon.getProducto().getId_Producto(), renglon.getDescuento_porcentaje()));
            });
        }
        return renglonesRestantes;
    }

    @Override
    public RenglonFactura calcularRenglon(String tipoDeFactura, Movimiento movimiento,
            double cantidad, Long idProducto, double descuento_porcentaje) {

        Producto producto = productoService.getProductoPorId(idProducto);
        RenglonFactura nuevoRenglon = new RenglonFactura();
        nuevoRenglon.setId_ProductoItem(producto.getId_Producto());
        nuevoRenglon.setCodigoItem(producto.getCodigo());
        nuevoRenglon.setDescripcionItem(producto.getDescripcion());
        nuevoRenglon.setMedidaItem(producto.getMedida().getNombre());
        nuevoRenglon.setCantidad(cantidad);
        nuevoRenglon.setPrecioUnitario(this.calcularPrecioUnitario(movimiento, tipoDeFactura, producto));
        nuevoRenglon.setDescuento_porcentaje(descuento_porcentaje);
        nuevoRenglon.setDescuento_neto(this.calcularDescuento_neto(nuevoRenglon.getPrecioUnitario(), descuento_porcentaje));
        nuevoRenglon.setIva_porcentaje(producto.getIva_porcentaje());
        if (tipoDeFactura.equals("Factura Y")) {
            nuevoRenglon.setIva_porcentaje(producto.getIva_porcentaje() / 2);
        }
        nuevoRenglon.setIva_neto(this.calcularIVA_neto(movimiento, producto, nuevoRenglon.getDescuento_neto()));
        nuevoRenglon.setImpuesto_porcentaje(producto.getImpuestoInterno_porcentaje());
        nuevoRenglon.setImpuesto_neto(this.calcularImpInterno_neto(movimiento, producto, nuevoRenglon.getDescuento_neto()));
        nuevoRenglon.setGanancia_porcentaje(producto.getGanancia_porcentaje());
        nuevoRenglon.setGanancia_neto(producto.getGanancia_neto());
        nuevoRenglon.setImporte(this.calcularImporte(cantidad, nuevoRenglon.getPrecioUnitario(), nuevoRenglon.getDescuento_neto()));
        return nuevoRenglon;
    }

    @Override
    public List<Factura> dividirFactura(FacturaVenta facturaADividir, int[] indices) {
        FacturaVenta facturaSinIVA = new FacturaVenta();
        facturaSinIVA.setCliente(facturaADividir.getCliente());
        facturaSinIVA.setUsuario(facturaADividir.getUsuario());
        facturaSinIVA.setPedido(facturaADividir.getPedido());
        FacturaVenta facturaConIVA = new FacturaVenta();
        facturaConIVA.setCliente(facturaADividir.getCliente());
        facturaConIVA.setUsuario(facturaADividir.getUsuario());
        facturaConIVA.setPedido(facturaADividir.getPedido());
        facturaConIVA.setTipoFactura(facturaADividir.getTipoFactura());
        List<Factura> facturas = new ArrayList<>();
        facturaSinIVA = this.agregarRenglonesAFacturaSinIVA(facturaSinIVA, indices, facturaADividir.getRenglones());
        facturaConIVA = this.agregarRenglonesAFacturaConIVA(facturaConIVA, indices,facturaADividir.getRenglones());
        if (facturaSinIVA.getRenglones().size() > 0) {
            facturaSinIVA = this.procesarFacturaSinIVA(facturaADividir, facturaSinIVA);
            facturas.add(facturaSinIVA);
        }
        facturaConIVA = this.procesarFacturaConIVA(facturaADividir, facturaConIVA);
        facturas.add(facturaConIVA);
        return facturas;
    }
    
    private FacturaVenta procesarFacturaSinIVA(FacturaVenta facturaADividir, FacturaVenta facturaSinIVA) {
        double[] importe = new double[facturaSinIVA.getRenglones().size()];
        double[] ivaPorcentaje = new double[facturaSinIVA.getRenglones().size()];
        double[] impuestoPorcentajes = new double[facturaSinIVA.getRenglones().size()];
        int indice = 0;
        List<RenglonFactura> listRenglonesSinIVA = new ArrayList<>(facturaSinIVA.getRenglones());
        facturaSinIVA.setFecha(facturaADividir.getFecha());
        facturaSinIVA.setTipoFactura('X');
        facturaSinIVA.setFechaVencimiento(facturaADividir.getFechaVencimiento());
        facturaSinIVA.setTransportista(facturaADividir.getTransportista());
        facturaSinIVA.setRenglones(listRenglonesSinIVA);
        for (RenglonFactura renglon : facturaSinIVA.getRenglones()) {
            importe[indice] = renglon.getImporte();
            ivaPorcentaje[indice] = renglon.getIva_porcentaje();
            impuestoPorcentajes[indice] = renglon.getImpuesto_porcentaje();
            indice++;
        }
        facturaSinIVA.setSubTotal(this.calcularSubTotal(importe));
        facturaSinIVA.setDescuento_neto(this.calcularDescuento_neto(facturaSinIVA.getSubTotal(), facturaSinIVA.getDescuento_porcentaje()));
        facturaSinIVA.setRecargo_neto(this.calcularRecargo_neto(facturaSinIVA.getSubTotal(), facturaSinIVA.getRecargo_porcentaje()));
        facturaSinIVA.setSubTotal_neto(this.calcularSubTotal_neto(facturaSinIVA.getSubTotal(), facturaSinIVA.getRecargo_neto(), facturaSinIVA.getDescuento_neto()));
        facturaSinIVA.setIva_105_neto(this.calcularIva_neto(this.getTipoFactura(facturaSinIVA),
                facturaSinIVA.getDescuento_porcentaje(),
                facturaSinIVA.getRecargo_porcentaje(),
                importe, ivaPorcentaje, 10.5));
        facturaSinIVA.setIva_21_neto(this.calcularIva_neto(this.getTipoFactura(facturaSinIVA),
                facturaSinIVA.getDescuento_porcentaje(),
                facturaSinIVA.getRecargo_porcentaje(),
                importe, ivaPorcentaje, 21));
        facturaSinIVA.setImpuestoInterno_neto(this.calcularImpInterno_neto(this.getTipoFactura(facturaSinIVA), facturaSinIVA.getDescuento_porcentaje(), facturaSinIVA.getRecargo_porcentaje(), importe, impuestoPorcentajes));
        facturaSinIVA.setTotal(this.calcularTotal(facturaSinIVA.getSubTotal(), facturaSinIVA.getDescuento_neto(), facturaSinIVA.getRecargo_neto(), facturaSinIVA.getIva_105_neto(), facturaSinIVA.getIva_21_neto(), facturaSinIVA.getImpuestoInterno_neto()));
        facturaSinIVA.setObservaciones(facturaADividir.getObservaciones());
        facturaSinIVA.setPagada(facturaADividir.isPagada());
        facturaSinIVA.setEmpresa(facturaADividir.getEmpresa());
        facturaSinIVA.setEliminada(facturaADividir.isEliminada());
        return facturaSinIVA;
    }

    private FacturaVenta procesarFacturaConIVA(FacturaVenta facturaADividir, FacturaVenta facturaConIVA) {
        double[] importe = new double[facturaConIVA.getRenglones().size()];
        double[] ivaPorcentaje = new double[facturaConIVA.getRenglones().size()];
        double[] impuestoPorcentajes = new double[facturaConIVA.getRenglones().size()];
        int indice = 0;
        List<RenglonFactura> listRenglonesConIVA = new ArrayList<>(facturaConIVA.getRenglones());
        facturaConIVA.setFecha(facturaADividir.getFecha());
        facturaConIVA.setTipoFactura(facturaADividir.getTipoFactura());
        facturaConIVA.setFechaVencimiento(facturaADividir.getFechaVencimiento());
        facturaConIVA.setTransportista(facturaADividir.getTransportista());
        facturaConIVA.setRenglones(listRenglonesConIVA);
        for (RenglonFactura renglon : facturaConIVA.getRenglones()) {
            importe[indice] = renglon.getImporte();
            ivaPorcentaje[indice] = renglon.getIva_porcentaje();
            impuestoPorcentajes[indice] = renglon.getImpuesto_porcentaje();
            indice++;
        }
        facturaConIVA.setSubTotal(this.calcularSubTotal(importe));
        facturaConIVA.setDescuento_neto(this.calcularDescuento_neto(facturaConIVA.getSubTotal(), facturaConIVA.getDescuento_porcentaje()));
        facturaConIVA.setRecargo_neto(this.calcularRecargo_neto(facturaConIVA.getSubTotal(), facturaConIVA.getRecargo_porcentaje()));
        facturaConIVA.setSubTotal_neto(this.calcularSubTotal_neto(facturaConIVA.getSubTotal(), facturaConIVA.getRecargo_neto(), facturaConIVA.getDescuento_neto()));
        facturaConIVA.setIva_105_neto(this.calcularIva_neto(this.getTipoFactura(facturaConIVA),
                facturaConIVA.getDescuento_porcentaje(),
                facturaConIVA.getRecargo_porcentaje(),
                importe, ivaPorcentaje, 10.5));
        facturaConIVA.setIva_21_neto(this.calcularIva_neto(this.getTipoFactura(facturaConIVA),
                facturaConIVA.getDescuento_porcentaje(),
                facturaConIVA.getRecargo_porcentaje(),
                importe, ivaPorcentaje, 21));
        facturaConIVA.setImpuestoInterno_neto(this.calcularImpInterno_neto(this.getTipoFactura(facturaConIVA), facturaConIVA.getDescuento_porcentaje(), facturaConIVA.getRecargo_porcentaje(), importe, impuestoPorcentajes));
        facturaConIVA.setTotal(this.calcularTotal(facturaConIVA.getSubTotal(), facturaConIVA.getDescuento_neto(), facturaConIVA.getRecargo_neto(), facturaConIVA.getIva_105_neto(), facturaConIVA.getIva_21_neto(), facturaConIVA.getImpuestoInterno_neto()));
        facturaConIVA.setObservaciones(facturaADividir.getObservaciones());
        facturaConIVA.setPagada(facturaADividir.isPagada());
        facturaConIVA.setEmpresa(facturaADividir.getEmpresa());
        facturaConIVA.setEliminada(facturaADividir.isEliminada());
        return facturaConIVA;
    }

    private FacturaVenta agregarRenglonesAFacturaSinIVA(FacturaVenta facturaSinIVA, int[] indices, List<RenglonFactura> renglones) {
        List<RenglonFactura> renglonesSinIVA = new ArrayList<>();
        double cantidadProductosRenglonFacturaSinIVA = 0;
        int renglonMarcado = 0;
        int numeroDeRenglon = 0;
        for (RenglonFactura renglon : renglones) {
            if (numeroDeRenglon == indices[renglonMarcado]) {
                double cantidad = renglon.getCantidad();
                if (cantidad >= 1) {
                    if ((cantidad % 1 != 0) || (cantidad % 2) == 0) {
                        cantidadProductosRenglonFacturaSinIVA = cantidad / 2;
                    } else if ((cantidad % 2) != 0) {
                        cantidadProductosRenglonFacturaSinIVA = cantidad - (Math.ceil(cantidad / 2));
                    }
                } else {
                    cantidadProductosRenglonFacturaSinIVA = 0;
                }
                RenglonFactura nuevoRenglonSinIVA = this.calcularRenglon("Factura X", Movimiento.VENTA, 
                            cantidadProductosRenglonFacturaSinIVA, renglon.getId_ProductoItem(), renglon.getDescuento_porcentaje());
                if (nuevoRenglonSinIVA.getCantidad() != 0) {
                    renglonesSinIVA.add(nuevoRenglonSinIVA);
                }
                numeroDeRenglon++;
                renglonMarcado++;
            } else {
                numeroDeRenglon++;
            }
        }
        facturaSinIVA.setRenglones(renglonesSinIVA);
        return facturaSinIVA;
    }

    private FacturaVenta agregarRenglonesAFacturaConIVA(FacturaVenta facturaConIVA, int[] indices,  List<RenglonFactura> renglones) {
        List<RenglonFactura> renglonesConIVA = new ArrayList<>();
        double cantidadProductosRenglonFacturaConIVA = 0;
        int renglonMarcado = 0;
        int numeroDeRenglon = 0;
        for (RenglonFactura renglon : renglones) {
            if (numeroDeRenglon == indices[renglonMarcado]) {
                double cantidad = renglon.getCantidad();
                if (cantidad < 1 || cantidad == 1) {
                    cantidadProductosRenglonFacturaConIVA = cantidad;
                } else if ((cantidad % 1 != 0) || (renglon.getCantidad() % 2) == 0) {
                    cantidadProductosRenglonFacturaConIVA = renglon.getCantidad() / 2;
                } else if ((renglon.getCantidad() % 2) != 0) {
                    cantidadProductosRenglonFacturaConIVA = Math.ceil(renglon.getCantidad() / 2);
                }
                RenglonFactura nuevoRenglonConIVA = this.calcularRenglon(this.getTipoFactura(facturaConIVA), Movimiento.VENTA,
                        cantidadProductosRenglonFacturaConIVA, renglon.getId_ProductoItem(), renglon.getDescuento_porcentaje());
                renglonesConIVA.add(nuevoRenglonConIVA);
                renglonMarcado++;
                numeroDeRenglon++;
            } else {
                numeroDeRenglon++;
                RenglonFactura nuevoRenglonConIVA = this.calcularRenglon(this.getTipoFactura(facturaConIVA), Movimiento.VENTA,
                        renglon.getCantidad(), renglon.getId_ProductoItem(), renglon.getDescuento_porcentaje());
                renglonesConIVA.add(nuevoRenglonConIVA);
            }
        }
        facturaConIVA.setRenglones(renglonesConIVA);
        return facturaConIVA;
    }

}
