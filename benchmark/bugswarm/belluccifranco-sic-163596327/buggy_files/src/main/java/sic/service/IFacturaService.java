package sic.service;

import java.util.List;
import sic.modelo.BusquedaFacturaCompraCriteria;
import sic.modelo.BusquedaFacturaVentaCriteria;
import sic.modelo.Cliente;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.Pedido;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.RenglonFactura;
import sic.modelo.RenglonPedido;

public interface IFacturaService {
    
    Factura getFacturaPorId(long id_Factura);

    String[] getTipoFacturaCompra(Empresa empresa, Proveedor proveedor);

    String[] getTipoFacturaVenta(Empresa empresa, Cliente cliente);

    char[] getTiposFacturaSegunEmpresa(Empresa empresa);

    List<RenglonFactura> getRenglonesDeLaFactura(Factura factura);

    FacturaVenta getFacturaVentaPorTipoSerieNum(char tipo, long serie, long num);

    FacturaCompra getFacturaCompraPorTipoSerieNum(char tipo, long serie, long num);

    String getTipoFactura(Factura factura);

    Movimiento getTipoMovimiento(Factura factura);

    List<FacturaCompra> buscarFacturaCompra(BusquedaFacturaCompraCriteria criteria);

    List<FacturaVenta> buscarFacturaVenta(BusquedaFacturaVentaCriteria criteria);

    void guardar(Factura factura);
    
    void guardar(Factura factura, Pedido pedido);    

    void actualizar(Factura factura);

    void eliminar(Factura factura);

    List<Factura> ordenarFacturasPorFechaAsc(List<Factura> facturas);

    boolean validarFacturasParaPagoMultiple(List<Factura> facturas, Movimiento movimiento);

    boolean validarClienteProveedorParaPagosMultiples(List<Factura> facturas, Movimiento movimiento);

    boolean validarFacturasImpagasParaPagoMultiple(List<Factura> facturas);

    boolean validarCantidadMaximaDeRenglones(int cantidad, Empresa empresa);

    double calcularSubTotal(List<RenglonFactura> renglones);

    double calcularDescuento_neto(double subtotal, double descuento_porcentaje);

    double calcularRecargo_neto(double subtotal, double recargo_porcentaje);

    double calcularSubTotal_neto(double subtotal, double recargo_neto, double descuento_neto);

    double calcularIva_neto(String tipoDeFactura, double descuento_porcentaje, double recargo_porcentaje, List<RenglonFactura> renglones, double iva_porcentaje);

    double calcularImpInterno_neto(String tipoDeFactura, double descuento_porcentaje, double recargo_porcentaje, List<RenglonFactura> renglones);

    double calcularImpInterno_neto(Movimiento movimiento, Producto producto, double descuento_neto);

    double calcularTotal(double subTotal, double descuento_neto, double recargo_neto, double iva105_neto, double iva21_neto, double impInterno_neto);

    double calcularTotalFacturadoVenta(List<FacturaVenta> facturas);

    double calcularTotalFacturadoCompra(List<FacturaCompra> facturas);

    double calcularIVA_Venta(List<FacturaVenta> facturas);

    double calcularIVA_Compra(List<FacturaCompra> facturas);

    double calcularGananciaTotal(List<FacturaVenta> facturas);

    double calcularIVA_neto(Movimiento movimiento, Producto producto, double descuento_neto);

    double calcularPrecioUnitario(Movimiento movimiento, String tipoDeFactura, Producto producto);

    long calcularNumeroFactura(String tipoDeFactura, long serie);

    double calcularVuelto(double importeAPagar, double importeAbonado);

    double calcularImporte(double cantidad, double precioUnitario, double descuento_neto);    

    byte[] getReporteFacturaVenta(Factura factura);

    List<FacturaVenta> dividirYGuardarFactura(FacturaVenta factura, int[] indices);

    RenglonFactura getRenglonFacturaPorRenglonPedido(RenglonPedido renglon, String tipoComprobante);

    List<RenglonFactura> convertirRenglonesPedidoARenglonesFactura(Pedido pedido, String tipoComprobante);

    RenglonFactura calcularRenglon(String tipoDeFactura, Movimiento movimiento, double cantidad, Producto producto, double descuento_porcentaje);

}
