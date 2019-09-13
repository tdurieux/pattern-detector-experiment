package sic.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sic.modelo.BusquedaFacturaCompraCriteria;
import sic.modelo.BusquedaFacturaVentaCriteria;
import sic.modelo.Cliente;
import sic.modelo.Factura;
import sic.modelo.FacturaCompra;
import sic.modelo.FacturaVenta;
import sic.modelo.Pedido;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.RenglonFactura;
import sic.modelo.Usuario;
import sic.service.IClienteService;
import sic.service.IEmpresaService;
import sic.service.IFacturaService;
import sic.service.IPedidoService;
import sic.service.IProductoService;
import sic.service.IProveedorService;
import sic.service.IUsuarioService;
import sic.service.Movimiento;

@RestController
@RequestMapping("/api/v1")
public class FacturaController {
    
    private final IFacturaService facturaService;
    private final IEmpresaService empresaService;
    private final IProveedorService proveedorService;
    private final IClienteService clienteService;
    private final IUsuarioService usuarioService;
    private final IPedidoService pedidoService;
    private final IProductoService productoService;
    
    @Autowired
    public FacturaController(IFacturaService facturaService, IEmpresaService empresaService,
                             IProveedorService proveedorService, IClienteService clienteService,
                             IUsuarioService usuarioService, IPedidoService pedidoService,
                             IProductoService productoService) {
        this.facturaService = facturaService;
        this.empresaService = empresaService;
        this.proveedorService = proveedorService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
        this.productoService = productoService;
    }
    
    @GetMapping("/facturas/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Factura getFacturaPorId(@PathVariable("id") long id) {
        return facturaService.getFacturaPorId(id);
    }
    
    @PostMapping("/facturas")
    @ResponseStatus(HttpStatus.CREATED)
    public Factura guardar(@RequestBody Factura factura) {
        facturaService.guardar(factura);
        return this.facturaService.getFacturaPorId(factura.getId_Factura());
    }
    
    @PostMapping("/facturas/pedidos/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Factura guardar(@PathVariable("id") long id,
                           @RequestBody Factura factura) {
        facturaService.guardar(factura, pedidoService.getPedidoPorNumeroConFacturas(id)); // cambiar por el getPorId
        return this.facturaService.getFacturaPorId(factura.getId_Factura());
    }
    
    @PutMapping("/facturas")
    @ResponseStatus(HttpStatus.OK)
    public Factura actualizar(@RequestBody Factura factura) {
        if(facturaService.getFacturaPorId(factura.getId_Factura()) != null) {
            facturaService.actualizar(factura);
        }
        return facturaService.getFacturaPorId(factura.getId_Factura()); 
    }
    
    @GetMapping("/facturass/{id}/renglones")
    @ResponseStatus(HttpStatus.OK)
    public List<RenglonFactura> getRenglonesDeLaFactura(@PathVariable("id") long id) {
        return facturaService.getRenglonesDeLaFactura(facturaService.getFacturaPorId(id));
    }
    
    @GetMapping("/facturas/compra/busqueda/criteria")
    @ResponseStatus(HttpStatus.OK)
    public List<FacturaCompra> buscarFacturaCompra(@RequestParam(value = "idEmpresa") Long idEmpresa,
                                        @RequestParam(value = "desde", required = false) Long desde,
                                        @RequestParam(value = "hasta", required = false) Long hasta,
                                        @RequestParam(value = "idProveedor", required = false) Long idProveedor,
                                        @RequestParam(value = "nroSerie", required = false) Integer nroSerie,
                                        @RequestParam(value = "nroFactura", required = false) Integer nroFactura,
                                        @RequestParam(value = "soloImpagas", required = false) Boolean soloImpagas,
                                        @RequestParam(value = "soloPagas", required = false) Boolean soloPagas) {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        if ((desde != null) && (hasta != null)) {
            fechaDesde.setTimeInMillis(desde);            
           fechaHasta.setTimeInMillis(hasta);
        }
        Proveedor proveedor = proveedorService.getProveedorPorId(idProveedor);
        if ((soloImpagas != null) && (soloPagas != null)) {
            if ((soloImpagas == true) && (soloPagas == true)) {
                soloImpagas = false;
                soloPagas = false;
            }
        }
        BusquedaFacturaCompraCriteria criteria = BusquedaFacturaCompraCriteria.builder()
                                                 .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                                 .buscaPorFecha((desde != null) && (hasta != null))
                                                 .fechaDesde(fechaDesde.getTime())
                                                 .fechaHasta(fechaHasta.getTime())
                                                 .buscaPorProveedor(idProveedor != null)
                                                 .proveedor(proveedor)
                                                 .buscaPorNumeroFactura((nroSerie != null) && (nroFactura != null))
                                                 .numSerie((nroSerie != null) ? nroSerie : 0)
                                                 .numFactura((nroFactura != null) ? nroFactura : 0)
                                                 .buscarSoloInpagas(soloImpagas)
                                                 .buscaSoloPagadas(soloPagas)
                                                 .cantRegistros(0)
                                                 .build();
        return facturaService.buscarFacturaCompra(criteria);
    }
    
    @GetMapping("/facturas/venta/busqueda/criteria")
    @ResponseStatus(HttpStatus.OK)
    public List<FacturaVenta> buscarFacturaVenta(@RequestParam(value = "idEmpresa") Long idEmpresa,
                                                 @RequestParam(value = "desde", required = false) Long desde,
                                                 @RequestParam(value = "hasta", required = false) Long hasta,
                                                 @RequestParam(value = "idCliente", required = false) Long idCliente,
                                                 @RequestParam(value = "nroSerie", required = false) Integer nroSerie,
                                                 @RequestParam(value = "nroFactura", required = false) Integer nroFactura,
                                                 @RequestParam(value = "tipoFactura", required = false) Character tipoFactura,
                                                 @RequestParam(value = "idUsuario", required = false) Long idUsuario,
                                                 @RequestParam(value = "nroPedido", required = false) Long nroPedido,
                                                 @RequestParam(value = "soloImpagas", required = false) Boolean soloImpagas,
                                                 @RequestParam(value = "soloPagas", required = false) Boolean soloPagas) {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        if ((desde != null) && (hasta != null)) {
            fechaDesde.setTimeInMillis(desde);
            fechaHasta.setTimeInMillis(hasta);
        }
        Cliente cliente = clienteService.getClientePorId(idCliente);
        Usuario usuario = usuarioService.getUsuarioPorId(idUsuario);
        if ((soloImpagas != null) && (soloPagas != null)) {
            if ((soloImpagas == true) && (soloPagas == true)) {
                soloImpagas = false;
                soloPagas = false;
            }
        }
        BusquedaFacturaVentaCriteria criteria = BusquedaFacturaVentaCriteria.builder()
                                                 .empresa(empresaService.getEmpresaPorId(idEmpresa))
                                                 .buscaPorFecha((desde != null) && (hasta != null))
                                                 .fechaDesde(fechaDesde.getTime())
                                                 .fechaHasta(fechaHasta.getTime())
                                                 .buscaCliente(cliente != null)
                                                 .cliente(cliente)
                                                 .buscaUsuario(usuario != null)
                                                 .usuario(usuario)
                                                 .buscaPorNumeroFactura((nroSerie != null) && (nroFactura != null))
                                                 .numSerie((nroSerie != null)? nroSerie : 0)
                                                 .numFactura((nroFactura != null) ? nroFactura : 0)
                                                 .buscarPorPedido(nroPedido != null)
                                                 .nroPedido((nroPedido != null) ? nroPedido : 0)
                                                 .buscaPorTipoFactura(tipoFactura != null)
                                                 .tipoFactura((tipoFactura != null) ? tipoFactura : new Character('-'))
                                                 .buscaSoloImpagas(soloImpagas)
                                                 .buscaSoloPagadas(soloPagas)
                                                 .cantRegistros(0)
                                                 .build();
        return facturaService.buscarFacturaVenta(criteria);
    }
    
    @GetMapping("/facturas/empresa/{idEmpresa}/proveedor/{idProveedor}/tipos")
    @ResponseStatus(HttpStatus.OK)
    public String[] getTipoFacturaCompra(@PathVariable("idEmpresa") long idEmpresa, @PathVariable("idProveedor") long idProveedor) {
        return facturaService.getTipoFacturaCompra(empresaService.getEmpresaPorId(idEmpresa), proveedorService.getProveedorPorId(idProveedor));
    }
    
    @GetMapping("/facturas/empresa/{idEmpresa}/cliente/{idCliente}/tipos")
    @ResponseStatus(HttpStatus.OK)
    public String[] getTipoFacturaVenta(@PathVariable("idEmpresa") long idEmpresa, @PathVariable("idCliente") long idCliente) {
        return facturaService.getTipoFacturaVenta(empresaService.getEmpresaPorId(idEmpresa), clienteService.getClientePorId(idCliente));
    }
    
    @GetMapping("/facturas/empresa/{idEmpresa}/tipos")
    @ResponseStatus(HttpStatus.OK)
    public char[] getTiposFacturaSegunEmpresa(@PathVariable("idEmpresa") long idEmpresa) {
        return facturaService.getTiposFacturaSegunEmpresa(empresaService.getEmpresaPorId(idEmpresa));
    }
    
    @GetMapping("/facturas/venta/tipo-serie-num")
    @ResponseStatus(HttpStatus.OK)
    public FacturaVenta getFacturaVentaPorTipoSerieNum(@RequestParam(value = "tipo") char tipo,
                                                       @RequestParam(value = "serie") long serie,
                                                       @RequestParam(value = "tipo") long num) {
        return facturaService.getFacturaVentaPorTipoSerieNum(tipo, serie, num);
    }
    
    @GetMapping("/facturas/compra/tipo-serie-num")
    @ResponseStatus(HttpStatus.OK)
    public FacturaCompra getFacturaCompraPorTipoSerieNum(@RequestParam(value = "tipo") char tipo,
                                                         @RequestParam(value = "serie") long serie,
                                                         @RequestParam(value = "tipo") long num) {
        return facturaService.getFacturaCompraPorTipoSerieNum(tipo, serie, num);
    }
    
    @GetMapping("/facturas/{id}/tipo")
    @ResponseStatus(HttpStatus.OK)
    public String getTipoFactura(@PathVariable("id") long id) {
        return facturaService.getTipoFactura(facturaService.getFacturaPorId(id));
    }
    
    @GetMapping("/facturas/pago-multiple")
    @ResponseStatus(HttpStatus.OK)
    public boolean validarFacturasParaPagoMultiple(@RequestParam("id") long[] ids,
                                                   @RequestParam("movimiento") Movimiento movimiento) {
        List<Factura> facturas = new ArrayList<>();
        for(long id : ids) {
            facturas.add(facturaService.getFacturaPorId(id));
        }
        return facturaService.validarClienteProveedorParaPagosMultiples(facturas, movimiento);
    }
    
    @GetMapping("/facturas/pago-multiple-cliente-proveedor")
    @ResponseStatus(HttpStatus.OK)
    public boolean validarClienteProveedorParaPagosMultiples(@RequestParam("id") long[] ids,
                                                             @RequestParam("movimiento") Movimiento movimiento) {
        List<Factura> facturas = new ArrayList<>();
        for(long id : ids) {
            facturas.add(facturaService.getFacturaPorId(id));
        }
        return facturaService.validarClienteProveedorParaPagosMultiples(facturas, movimiento);
    }
    
    @GetMapping("/facturas/pago-multiple-impagas")
    @ResponseStatus(HttpStatus.OK)
    public boolean validarFacturasImpagasParaPagoMultiple(@RequestParam("id") long[] ids,
                                                          @RequestParam("movimiento") Movimiento movimiento) {
        List<Factura> facturas = new ArrayList<>();
        for(long id : ids) {
            facturas.add(facturaService.getFacturaPorId(id));
        }
        return facturaService.validarFacturasImpagasParaPagoMultiple(facturas);
    }
    
    @GetMapping("/facturas/empresa/{id}/cantidad-renglones/{cantidad}")
    @ResponseStatus(HttpStatus.OK)
    public boolean validarCantidadMaximaDeRenglones(@PathVariable("id") long idEmpresa,
                                                    @PathVariable("cantidad") int cantidad) {
        return facturaService.validarCantidadMaximaDeRenglones(cantidad, empresaService.getEmpresaPorId(idEmpresa));
    }
    
    @PostMapping("/facturas/subtotal")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularSubTotal(@RequestBody List<RenglonFactura> renglones) {
        return facturaService.calcularSubTotal(renglones);
    }
    
    @PostMapping("/facturas/descuento-neto")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularDescuento_neto(@RequestParam(value = "subTotal", required = false) Double subTotal,
                                         @RequestParam(value = "porcentaje", required = false) Double porcentaje) {
        return facturaService.calcularDescuento_neto(subTotal, porcentaje);
    }
    
    @PostMapping("/facturas/recargo-neto")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularRecargo_neto(@RequestParam(value = "subTotal", required = false) Double subTotal,
                                       @RequestParam(value = "porcentaje", required = false) Double porcentaje) {
        return facturaService.calcularRecargo_neto(subTotal, porcentaje);
    }
    
    @PostMapping("/facturas/subtotal-neto")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularSubTotal_neto(@RequestParam(value = "subTotal", required = false) Double subTotal,
                                        @RequestParam(value = "recargo", required = false) Double recargo,
                                        @RequestParam(value = "descuento", required = false) Double descuento) {
        return facturaService.calcularSubTotal_neto(subTotal, recargo, descuento);
    }
    
    @PostMapping("/facturas/producto/{id}/imp-interno-neto")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularImpInterno_neto(@PathVariable("idProducto") long id,
                                          @RequestParam(value = "descuento", required = false) Double descuento,
                                          @RequestParam("movimiento") Movimiento movimiento) {
        return facturaService.calcularImpInterno_neto(movimiento, productoService.getProductoPorId(id), descuento);
    }
    
    @PostMapping("/facturas/total")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularTotal(@RequestParam(value = "subTotal", required = false) Double subTotal,
                                @RequestParam(value = "descuentoNeto", required = false) Double descuentoNeto,
                                @RequestParam(value = "recargoNeto", required = false) Double recargoNeto,
                                @RequestParam(value = "iva105neto", required = false) Double iva105Neto,
                                @RequestParam(value = "iva21neto", required = false) Double iva21Neto,
                                @RequestParam(value = "impInternoneto", required = false) Double impInternoNeto) {
        return facturaService.calcularTotal(subTotal, descuentoNeto, recargoNeto, iva105Neto, iva21Neto, impInternoNeto);
    }
    
    @PostMapping("/facturas/total-facturado-venta")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularTotalFacturadoVenta(@RequestParam("id") long[] ids) {
        List<FacturaVenta> facturas = new ArrayList<>();
        Factura factura;
        for (long id : ids) {
            factura = facturaService.getFacturaPorId(id);
            if (factura instanceof FacturaVenta) {
                facturas.add((FacturaVenta) factura);
            }
        }
        return facturaService.calcularTotalFacturadoVenta(facturas);
    }
    
    @PostMapping("/facturas/total-facturado-compra")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularTotalFacturadoCompra(@RequestParam("id") long[] ids) {
        List<FacturaCompra> facturas = new ArrayList<>();
        Factura factura;
        for (long id : ids) {
            factura = facturaService.getFacturaPorId(id);
            if (factura instanceof FacturaCompra) {
                facturas.add((FacturaCompra) factura);
            }
        }
        return facturaService.calcularTotalFacturadoCompra(facturas);
    }
    
    @PostMapping("/facturas/total-iva-venta")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularIvaVenta(@RequestParam("id") long[] ids) {
        List<FacturaVenta> facturas = new ArrayList<>();
        Factura factura;
        for (long id : ids) {
            factura = facturaService.getFacturaPorId(id);
            if (factura instanceof FacturaVenta) {
                facturas.add((FacturaVenta) factura);
            }
        }
        return facturaService.calcularIVA_Venta(facturas);
    }
    
    @PostMapping("/facturas/total-iva-compra")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularTotalIvaCompra(@RequestParam("id") long[] ids) {
        List<FacturaCompra> facturas = new ArrayList<>();
        Factura factura;
        for (long id : ids) {
            factura = facturaService.getFacturaPorId(id);
            if (factura instanceof FacturaCompra) {
                facturas.add((FacturaCompra) factura);
            }
        }
        return facturaService.calcularIVA_Compra(facturas);
    }
    
    @PostMapping("/facturas/ganancia-total")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularGananciaTotal(@RequestParam("id") long[] ids) {
        List<FacturaVenta> facturas = new ArrayList<>();
        Factura factura;
        for (long id : ids) {
            factura = facturaService.getFacturaPorId(id);
            if (factura instanceof FacturaVenta) {
                facturas.add((FacturaVenta) factura);
            }
        }
        return facturaService.calcularGananciaTotal(facturas);
    }
    
    @PostMapping("/facturas/producto/{id}/iva-neto")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularIVA_neto(@PathVariable("idProducto") long id,
                                   @RequestParam(value = "descuento", required = false) Double descuento,
                                   @RequestParam("movimiento") Movimiento movimiento) {
        return facturaService.calcularIVA_neto(movimiento, productoService.getProductoPorId(id), descuento);
    }
    
    @PostMapping("/facturas/producto/{id}/precio-unitario")
    @ResponseStatus(HttpStatus.CREATED)
    public double calcularPrecioUnitario(@PathVariable("idProducto") long id,
                                         @RequestParam("tipoDeFactura") String tipoDeFactura,
                                         @RequestParam("movimiento") Movimiento movimiento) {
        return facturaService.calcularPrecioUnitario(movimiento, tipoDeFactura, productoService.getProductoPorId(id));
    }
    
    @GetMapping("/facturas/{id}/reporte")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getReporteFacturaVenta(@PathVariable("id") long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setContentDispositionFormData("factura.pdf", "factura.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        byte[] reportePDF = facturaService.getReporteFacturaVenta(facturaService.getFacturaPorId(id));
        return new ResponseEntity<>(reportePDF, headers, HttpStatus.OK);
    }
    
    @PostMapping("/facturas/pedido/{id}/renglon-pedido-a-renglon-factura")
    @ResponseStatus(HttpStatus.CREATED)
    public List<RenglonFactura> convertirRenglonesPedidoARenglonesFactura(@PathVariable("id") long id,
                                                                          @RequestParam("tipoComprobante") String tipoComprobante) {
        return facturaService.convertirRenglonesPedidoARenglonesFactura(pedidoService.getPedidoPorNumeroConFacturas(id), tipoComprobante);// cambiar por el get por id
    }
    
    @PostMapping("/facturas/pedido/{id}/calculo-renglon")
    @ResponseStatus(HttpStatus.CREATED)
    public RenglonFactura calcularRenglon(@PathVariable("id") long idProducto,
                                          @RequestParam(value = "tipoFactura" , required = false) String tipoDeFactura,
                                          @RequestParam(value = "cantidad" , required = false) Double cantidad,
                                          @RequestParam(value = "descuentoPorcentaje", required = false) Double descuento_porcentaje,
                                          @RequestParam("movimiento") Movimiento movimiento) {
        return facturaService.calcularRenglon(tipoDeFactura, movimiento, 0, productoService.getProductoPorId(idProducto), 0);
    }
    
    @PostMapping("/facturas/divide-guarda")
    @ResponseStatus(HttpStatus.CREATED)
    public List<FacturaVenta> dividirYGuardarFactura(@RequestParam("indices") int[] indices,
                                                     @RequestBody FacturaVenta factura) {
        return facturaService.dividirYGuardarFactura(factura, indices);
    }
    
    @PostMapping("/facturas/producto/{id}/renglon")
    @ResponseStatus(HttpStatus.CREATED)
    public RenglonFactura calcularRenglon(@PathVariable("id") long id,
                                          @RequestParam("tipoDeFactura") String tipoDeFactura,
                                          @RequestParam("movimiento") Movimiento movimiento,
                                          @RequestParam("cantidad") double cantidad, 
                                          @RequestParam("descuentoPorcentaje") double descuento_porcentaje) {
        return facturaService.calcularRenglon(tipoDeFactura, movimiento, cantidad, productoService.getProductoPorId(id), descuento_porcentaje);
    }
    
}
