package sic.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.test.context.junit4.SpringRunner;
import sic.builder.ClienteBuilder;
import sic.builder.EmpresaBuilder;
import sic.builder.FacturaVentaBuilder;
import sic.builder.ProductoBuilder;
import sic.builder.TransportistaBuilder;
import sic.modelo.Cliente;
import sic.modelo.CondicionIVA;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.FacturaVenta;
import sic.modelo.Medida;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.RenglonFactura;
import sic.modelo.Movimiento;
import sic.modelo.Usuario;
import sic.repository.FacturaRepository;

@RunWith(SpringRunner.class)
public class FacturaServiceImplTest {
    
    @Mock
    private FacturaRepository facturaRepository;    
    
    @Mock
    private ProductoServiceImpl productoService;
    
    @InjectMocks
    private FacturaServiceImpl facturaService;

    @Test
    public void shouldGetTipoFacturaCompraWhenEmpresaYProveedorDiscriminanIVA() {
        Empresa empresa = Mockito.mock(Empresa.class);
        CondicionIVA condicionIVAqueDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.TRUE);
        when(empresa.getCondicionIVA()).thenReturn(condicionIVAqueDiscrimina);
        empresa.setCondicionIVA(condicionIVAqueDiscrimina);
        Proveedor proveedor = Mockito.mock(Proveedor.class);
        when(proveedor.getCondicionIVA()).thenReturn(condicionIVAqueDiscrimina);
        String[] expResult = new String[3];
        expResult[0] = "Factura A";
        expResult[1] = "Factura B";
        expResult[2] = "Factura X";
        String[] result = facturaService.getTipoFacturaCompra(empresa, proveedor);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void shouldGetTipoFacturaCompraWhenEmpresaDiscriminaIVAYProveedorNO() {
        Empresa empresa = Mockito.mock(Empresa.class);
        Proveedor proveedor = Mockito.mock(Proveedor.class);
        CondicionIVA condicionIVAqueDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.TRUE);
        CondicionIVA condicionIVAqueNoDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueNoDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.FALSE);
        when(empresa.getCondicionIVA()).thenReturn(condicionIVAqueDiscrimina);
        when(proveedor.getCondicionIVA()).thenReturn(condicionIVAqueNoDiscrimina);
        String[] expResult = new String[2];
        expResult[0] = "Factura C";
        expResult[1] = "Factura X";
        String[] result = facturaService.getTipoFacturaCompra(empresa, proveedor);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void shouldGetTipoFacturaCompraWhenEmpresaNoDiscriminaIVAYProveedorSI() {
        Empresa empresa = Mockito.mock(Empresa.class);
        Proveedor proveedor = Mockito.mock(Proveedor.class);
        CondicionIVA condicionIVAqueDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.TRUE);
        CondicionIVA condicionIVAqueNoDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueNoDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.FALSE);
        when(empresa.getCondicionIVA()).thenReturn(condicionIVAqueNoDiscrimina);
        when(proveedor.getCondicionIVA()).thenReturn(condicionIVAqueDiscrimina);
        String[] expResult = new String[2];
        expResult[0] = "Factura B";
        expResult[1] = "Factura X";
        empresa.getCondicionIVA().isDiscriminaIVA();
        String[] result = facturaService.getTipoFacturaCompra(empresa, proveedor);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void shouldGetTipoFacturaCompraWhenEmpresaNoDiscriminaYProveedorTampoco() {
        Empresa empresa = Mockito.mock(Empresa.class);
        Proveedor proveedor = Mockito.mock(Proveedor.class);
        CondicionIVA condicionIVAqueNoDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueNoDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.FALSE);
        when(empresa.getCondicionIVA()).thenReturn(condicionIVAqueNoDiscrimina);
        when(proveedor.getCondicionIVA()).thenReturn(condicionIVAqueNoDiscrimina);
        String[] expResult = new String[2];
        expResult[0] = "Factura C";
        expResult[1] = "Factura X";
        empresa.getCondicionIVA().isDiscriminaIVA();
        String[] result = facturaService.getTipoFacturaCompra(empresa, proveedor);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void shouldGetTipoFacturaVentaWhenEmpresaDiscriminaYClienteTambien() {
        Empresa empresa = Mockito.mock(Empresa.class);
        Cliente cliente = Mockito.mock(Cliente.class);
        CondicionIVA condicionIVAqueDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.TRUE);
        when(empresa.getCondicionIVA()).thenReturn(condicionIVAqueDiscrimina);
        when(cliente.getCondicionIVA()).thenReturn(condicionIVAqueDiscrimina);
        String[] expResult = {"Factura A", "Factura B", "Factura X", "Factura Y", "Pedido"};
        String[] result = facturaService.getTipoFacturaVenta(empresa, cliente);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void shouldGetTipoFacturaVentaWhenEmpresaDiscriminaYClienteNo() {
        Empresa empresa = Mockito.mock(Empresa.class);
        Cliente cliente = Mockito.mock(Cliente.class);
        CondicionIVA condicionIVAqueDiscrimina = Mockito.mock(CondicionIVA.class);
        CondicionIVA condicionIVAqueNoDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.TRUE);
        when(condicionIVAqueNoDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.FALSE);
        when(empresa.getCondicionIVA()).thenReturn(condicionIVAqueDiscrimina);
        when(cliente.getCondicionIVA()).thenReturn(condicionIVAqueNoDiscrimina);
        String[] expResult = {"Factura B", "Factura X", "Factura Y", "Pedido"};
        String[] result = facturaService.getTipoFacturaVenta(empresa, cliente);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void shouldGetTipoFacturaVentaWhenEmpresaNoDiscriminaYClienteSi() {
        Empresa empresa = Mockito.mock(Empresa.class);
        Cliente cliente = Mockito.mock(Cliente.class);
        CondicionIVA condicionIVAqueDiscrimina = Mockito.mock(CondicionIVA.class);
        CondicionIVA condicionIVAqueNoDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.TRUE);
        when(condicionIVAqueNoDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.FALSE);
        when(empresa.getCondicionIVA()).thenReturn(condicionIVAqueNoDiscrimina);
        when(cliente.getCondicionIVA()).thenReturn(condicionIVAqueDiscrimina);
        String[] expResult = {"Factura C", "Factura X", "Factura Y", "Pedido"};
        String[] result = facturaService.getTipoFacturaVenta(empresa, cliente);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void shouldGetTipoFacturaVentaWhenEmpresaNoDiscriminaIVAYClienteNO() {
        Empresa empresa = Mockito.mock(Empresa.class);
        Cliente cliente = Mockito.mock(Cliente.class);
        CondicionIVA condicionIVAqueNoDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueNoDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.TRUE);
        when(condicionIVAqueNoDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.FALSE);
        when(empresa.getCondicionIVA()).thenReturn(condicionIVAqueNoDiscrimina);
        when(cliente.getCondicionIVA()).thenReturn(condicionIVAqueNoDiscrimina);
        String[] expResult = {"Factura C", "Factura X", "Factura Y", "Pedido"};
        String[] result = facturaService.getTipoFacturaVenta(empresa, cliente);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void shouldGetTiposFacturaWhenEmpresaDiscriminaIVA() {
        Empresa empresa = Mockito.mock(Empresa.class);
        CondicionIVA condicionIVAqueDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.TRUE);
        when(empresa.getCondicionIVA()).thenReturn(condicionIVAqueDiscrimina);
        char[] expResult = {'A', 'B', 'X', 'Y'};
        char[] result = facturaService.getTiposFacturaSegunEmpresa(empresa);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void shouldGetTiposFacturaWhenEmpresaNoDiscriminaIVA() {
        Empresa empresa = Mockito.mock(Empresa.class);
        CondicionIVA condicionIVAqueNoDiscrimina = Mockito.mock(CondicionIVA.class);
        when(condicionIVAqueNoDiscrimina.isDiscriminaIVA()).thenReturn(Boolean.FALSE);
        when(empresa.getCondicionIVA()).thenReturn(condicionIVAqueNoDiscrimina);
        char[] expResult = {'C', 'X', 'Y'};
        char[] result = facturaService.getTiposFacturaSegunEmpresa(empresa);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void shouldDividirFactura() {
        when(facturaRepository.buscarMayorNumFacturaSegunTipo('X', 1L, new EmpresaBuilder().build().getId_Empresa())).thenReturn(1L);
        when(facturaRepository.buscarMayorNumFacturaSegunTipo('A', 1L, new EmpresaBuilder().build().getId_Empresa())).thenReturn(1L);
        RenglonFactura renglon1 = Mockito.mock(RenglonFactura.class);
        RenglonFactura renglon2 = Mockito.mock(RenglonFactura.class);
        Producto producto = Mockito.mock(Producto.class);
        when(producto.getId_Producto()).thenReturn(1L);
        when(producto.getCodigo()).thenReturn("1");
        when(producto.getDescripcion()).thenReturn("producto test");
        Medida medida = Mockito.mock(Medida.class);
        when(producto.getMedida()).thenReturn(medida);
        when(producto.getPrecioVentaPublico()).thenReturn(1.0);
        when(producto.getIva_porcentaje()).thenReturn(21.00);
        when(producto.getImpuestoInterno_porcentaje()).thenReturn(0.0);
        when(producto.getPrecioLista()).thenReturn(1.0);
        when(productoService.getProductoPorId(1L)).thenReturn(producto);
        when(renglon1.getId_ProductoItem()).thenReturn(1L);
        when(renglon2.getId_ProductoItem()).thenReturn(1L);
        when(renglon1.getCantidad()).thenReturn(4.00);
        when(renglon2.getCantidad()).thenReturn(7.00);
        List<RenglonFactura> renglones = new ArrayList<>();
        renglones.add(renglon1);
        renglones.add(renglon2);
        FacturaVenta factura = new FacturaVenta();
        factura.setRenglones(renglones);
        factura.setFecha(new Date());
        factura.setTransportista(new TransportistaBuilder().build());
        factura.setEmpresa(new EmpresaBuilder().build());
        factura.setCliente(new ClienteBuilder().build());
        Usuario usuario = new Usuario();
        usuario.setNombre("Marian Jhons  help");
        factura.setUsuario(usuario);
        factura.setTipoFactura('A');
        int[] indices = {0, 1};
        int cantidadDeFacturasEsperadas = 2;
        List<Factura> result = facturaService.dividirFactura(factura, indices);
        double cantidadRenglon1PrimeraFactura = result.get(0).getRenglones().get(0).getCantidad();
        double cantidadRenglon2PrimeraFactura = result.get(0).getRenglones().get(1).getCantidad();
        double cantidadRenglon1SegundaFactura = result.get(1).getRenglones().get(0).getCantidad();
        double cantidadRenglon2SegundaFactura = result.get(1).getRenglones().get(1).getCantidad();
        assertEquals(cantidadDeFacturasEsperadas, result.size());
        assertEquals(2, cantidadRenglon1SegundaFactura, 0); 
        assertEquals(4, cantidadRenglon2SegundaFactura, 0); 
        assertEquals(2, cantidadRenglon1PrimeraFactura, 0);
        assertEquals(3, cantidadRenglon2PrimeraFactura, 0);
    }

    //Calculos
    @Test
    public void shouldCalcularSubTotal() {
        RenglonFactura renglon1 = new RenglonFactura();
        renglon1.setImporte(5.601);
        RenglonFactura renglon2 = new RenglonFactura();
        renglon2.setImporte(18.052);
        RenglonFactura renglon3 = new RenglonFactura();
        renglon3.setImporte(10.011);
        List<RenglonFactura> renglones = new ArrayList<>();
        renglones.add(renglon1);
        renglones.add(renglon2);
        renglones.add(renglon3);
        double[] importes = new double[renglones.size()];
        int indice = 0;
        for(RenglonFactura renglon : renglones) {
            importes[indice] = renglon.getImporte();
            indice++;
        }
        double resultadoEsperado = 33.664;
        double resultadoObtenido = facturaService.calcularSubTotal(importes);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCacularDescuentoNeto() {
        Double resultadoEsperado = 11.773464749999999;
        Double resultadoObtenido = facturaService.calcularDescuento_neto(78.255, 15.045);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularRecargoNeto() {
        double resultadoEsperado = 12.11047244;
        double resultadoObtenido = facturaService.calcularRecargo_neto(78.122, 15.502);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularSubTotal_neto() {
        double resultadoEsperado = 220.477;
        double resultadoObtenido = facturaService.calcularSubTotal_neto(225.025, 10.454, 15.002);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularIva_netoWhenLaFacturaEsA() {
        RenglonFactura renglon1 = new RenglonFactura();
        renglon1.setImporte(5.601);
        renglon1.setIva_porcentaje(21);
        RenglonFactura renglon2 = new RenglonFactura();
        renglon2.setImporte(18.052);
        renglon2.setIva_porcentaje(21);
        RenglonFactura renglon3 = new RenglonFactura();
        renglon3.setImporte(10.011);
        renglon3.setIva_porcentaje(10.5);
        List<RenglonFactura> renglones = new ArrayList<>();
        renglones.add(renglon1);
        renglones.add(renglon2);
        renglones.add(renglon3);
        //El renglon3 no lo deberia tener en cuenta para el calculo ya que NO es 21% de IVA
        double resultadoEsperado = 5.7066859857;
        double[] importes = new double[renglones.size()];
        double[] ivaPorcentaje = new double[renglones.size()];
        int i = 0;
        for (RenglonFactura r : renglones) {
            importes[i] = r.getImporte();
            ivaPorcentaje[i] = r.getIva_porcentaje();
            i++;
        }       
        double resultadoObtenido = facturaService.calcularIva_neto("Factura A", 10.201, 25.09, importes, ivaPorcentaje, 21);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularIva_netoWhenLaFacturaEsX() {
        RenglonFactura renglon1 = new RenglonFactura();
        renglon1.setImporte(5.601);
        renglon1.setIva_porcentaje(21);
        RenglonFactura renglon2 = new RenglonFactura();
        renglon2.setImporte(18.052);
        renglon2.setIva_porcentaje(21);
        RenglonFactura renglon3 = new RenglonFactura();
        renglon3.setImporte(10.011);
        renglon3.setIva_porcentaje(10.5);
        List<RenglonFactura> renglones = new ArrayList<>();
        renglones.add(renglon1);
        renglones.add(renglon2);
        renglones.add(renglon3);
        double resultadoEsperado = 0;
        double[] importes = new double[renglones.size()];
        double[] ivaPorcentaje = new double[renglones.size()];
        int i = 0;
        for (RenglonFactura r : renglones) {
            importes[i] = r.getImporte();
            ivaPorcentaje[i] = r.getIva_porcentaje();
            i++;
        } 
        double resultadoObtenido = facturaService.calcularIva_neto("Factura X", 10, 25, importes, ivaPorcentaje, 21);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularImpInterno_neto() {
        RenglonFactura renglon1 = new RenglonFactura();
        renglon1.setImporte(5.601);
        renglon1.setImpuesto_porcentaje(15.304);
        RenglonFactura renglon2 = new RenglonFactura();
        renglon2.setImporte(18.052);
        renglon2.setImpuesto_porcentaje(9.043);
        RenglonFactura renglon3 = new RenglonFactura();
        renglon3.setImporte(10.011);
        renglon3.setImpuesto_porcentaje(4.502);
        List<RenglonFactura> renglones = new ArrayList<>();
        renglones.add(renglon1);
        renglones.add(renglon2);
        renglones.add(renglon3);
        double[] importes = new double[renglones.size()];
        double[] impuestoPorcentajes = new double[renglones.size()];
        int indice = 0;
        for(RenglonFactura renglon : renglones) {
            importes[indice] = renglon.getImporte();
            impuestoPorcentajes[indice] = renglon.getImpuesto_porcentaje();
            indice++;
        }
        double resultadoEsperado = 3.3197328185647996;
        double resultadoObtenido = facturaService.calcularImpInterno_neto("Factura A", 9.104, 22.008, importes, impuestoPorcentajes);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularTotal() {
        double resultadoEsperado = 460.8830000000001;
        double resultadoObtenido = facturaService.calcularTotal(350.451, 10.753, 25.159, 1.451, 84.525, 10.050);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }
  
//    @Test
//    public void shouldCalcularTotalFacturadoVenta() {
//        List<FacturaVenta> facturasDeVenta = new ArrayList<>();
//        FacturaVenta factura1 = new FacturaVentaBuilder()
//                               .withTotal(3424.08)
//                               .build();
//        FacturaVenta factura2 = new FacturaVentaBuilder()
//                               .withTotal(3424.08)
//                               .build();
//        FacturaVenta factura3 = new FacturaVentaBuilder()
//                               .withTotal(21124.50)
//                               .build();
//        facturasDeVenta.add(factura1);
//        facturasDeVenta.add(factura2);
//        facturasDeVenta.add(factura3);
//        double resultadoEsperado = 27972.66;
//        double resultadoObtenido = facturaService.calcularTotalFacturadoVenta(facturasDeVenta);
//        assertEquals(resultadoEsperado, resultadoObtenido, 0);
//    }

//    @Test
//    public void shouldCalcularTotalFacturadoCompra() {
//        List<FacturaCompra> facturasDeCompra = new ArrayList<>();
//        FacturaCompra factura1 = new FacturaCompra();
//        factura1.setTotal(1024.759);
//        FacturaCompra factura2 = new FacturaCompra();
//        factura2.setTotal(3424.089);
//        FacturaCompra factura3 = new FacturaCompra();
//        factura3.setTotal(21124.504);
//        facturasDeCompra.add(factura1);
//        facturasDeCompra.add(factura2);
//        facturasDeCompra.add(factura3);
//        double resultadoEsperado = 25573.352;
//        double resultadoObtenido = facturaService.calcularTotalFacturadoCompra(facturasDeCompra);
//        assertEquals(resultadoEsperado, resultadoObtenido, 0);
//    }

//    @Test
//    public void shouldCalcularIvaVenta() {
//        List<FacturaVenta> facturasDeVenta = new ArrayList<>();
//        FacturaVenta factura1 = new FacturaVentaBuilder()
//                               .withIva_105_neto(0)
//                               .withIva_21_neto(35)
//                               .build();
//        FacturaVenta factura2 = new FacturaVentaBuilder()
//                               .withIva_105_neto(0)
//                               .withIva_21_neto(30)
//                               .build();
//        FacturaVenta factura3 = new FacturaVentaBuilder()
//                               .withIva_105_neto(25)
//                               .withIva_21_neto(0)
//                               .build();
//        facturasDeVenta.add(factura1);
//        facturasDeVenta.add(factura2);
//        facturasDeVenta.add(factura3);
//        double resultadoEsperado = 90;
//        double resultadoObtenido = facturaService.calcularIVA_Venta(facturasDeVenta);
//        assertEquals(resultadoEsperado, resultadoObtenido, 0);
//    }

//    @Test
//    public void shouldCalcularIvaCompra() {
//        List<FacturaCompra> facturasDeCompra = new ArrayList<>();
//        FacturaCompra factura1 = new FacturaCompra();
//        factura1.setIva_105_neto(0);
//        factura1.setIva_21_neto(35);
//        FacturaCompra factura2 = new FacturaCompra();
//        factura2.setIva_105_neto(0);
//        factura2.setIva_21_neto(30);
//        FacturaCompra factura3 = new FacturaCompra();
//        factura3.setIva_105_neto(25);
//        factura3.setIva_21_neto(0);
//        facturasDeCompra.add(factura1);
//        facturasDeCompra.add(factura2);
//        facturasDeCompra.add(factura3);
//        double resultadoEsperado = 90;
//        double resultadoObtenido = facturaService.calcularIVA_Compra(facturasDeCompra);
//        assertEquals(resultadoEsperado, resultadoObtenido, 0);
//    }

    @Test
    public void shouldCalcularImporte() {
        double resultadoEsperado = 90;
        double cantidad = 10;
        double precioUnitario = 10;
        double descuento = 1;
        double resultadoObtenido = facturaService.calcularImporte(cantidad, precioUnitario, descuento);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularIVANetoWhenCompra() {
        Producto producto = new ProductoBuilder()
                            .withPrecioCosto(100)
                            .withPrecioVentaPublico(121)
                            .withImpuestoInterno_neto(0.0)
                            .withIva_porcentaje(21).build();
        double resultadoEsperado = 21;
        double resultadoObtenido = facturaService.calcularIVA_neto(Movimiento.COMPRA, producto, 0.0);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularIVANetoWhenVenta() {
        Producto producto = new ProductoBuilder()
                            .withPrecioCosto(100)
                            .withPrecioVentaPublico(121)
                            .withImpuestoInterno_neto(0.0)
                            .withIva_porcentaje(21).build();
        double resultadoEsperado = 25.41;
        double resultadoObtenido = facturaService.calcularIVA_neto(Movimiento.VENTA, producto, 0.0);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularPrecioUnitarioWhenEsUnaVentaConFacturaA() {
        Producto producto = new ProductoBuilder()
                            .withPrecioCosto(100)
                            .withPrecioVentaPublico(121)
                            .withImpuestoInterno_neto(0.0)
                            .withIva_porcentaje(21).build();
        double resultadoEsperado = 121;
        double resultadoObtenido = facturaService.calcularPrecioUnitario(Movimiento.VENTA, "Factura A", producto);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularPrecioUnitarioWhenEsUnaVentaConFacturaX() {
        Producto producto = new ProductoBuilder()
                            .withPrecioCosto(100)
                            .withPrecioVentaPublico(121)
                            .withImpuestoInterno_neto(0.0)
                            .withIva_porcentaje(21).build(); 
        double resultadoEsperado = 121;
        double resultadoObtenido = facturaService.calcularPrecioUnitario(Movimiento.VENTA, "Factura X", producto);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularPrecioUnitarioWhenEsUnaCompraConFacturaA() {
        Producto producto = new ProductoBuilder()
                            .withPrecioCosto(100)
                            .withPrecioVentaPublico(121)
                            .withImpuestoInterno_neto(0.0)
                            .withIva_porcentaje(21).build(); 
        double resultadoEsperado = 100;
        double resultadoObtenido = facturaService.calcularPrecioUnitario(Movimiento.COMPRA, "Factura A", producto);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularPrecioUnitarioWhenEsUnaCompraConFacturaX() {
        Producto producto = new ProductoBuilder()
                            .withPrecioCosto(100)
                            .withPrecioVentaPublico(121)
                            .withImpuestoInterno_neto(0.0)
                            .withIva_porcentaje(21).build(); 
        double resultadoEsperado = 100;
        double resultadoObtenido = facturaService.calcularPrecioUnitario(Movimiento.COMPRA, "Factura X", producto);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularPrecioUnitarioWhenEsUnaCompraConFacturaB() {
        Producto producto = new ProductoBuilder()
                            .withPrecioCosto(100)
                            .withPrecioVentaPublico(121)
                            .withImpuestoInterno_neto(0.0)
                            .withIva_porcentaje(21).build(); 
        double resultadoEsperado = 121;
        double resultadoObtenido = facturaService.calcularPrecioUnitario(Movimiento.COMPRA, "Factura B", producto);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularPrecioUnitarioWhenEsUnaCompraConFacturaC() {
        Producto producto = new ProductoBuilder()
                            .withPrecioCosto(100)
                            .withPrecioVentaPublico(121)
                            .withImpuestoInterno_neto(0.0)
                            .withIva_porcentaje(21).build();  
        double resultadoEsperado = 121;
        double resultadoObtenido = facturaService.calcularPrecioUnitario(Movimiento.COMPRA, "Factura C", producto);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularPrecioUnitarioWhenEsUnaCompraConFacturaY() {
        Producto producto = new ProductoBuilder()
                            .withPrecioCosto(100)
                            .withPrecioVentaPublico(121)
                            .withImpuestoInterno_neto(0.0)
                            .withIva_porcentaje(21).build();
        double resultadoEsperado = 121;
        double resultadoObtenido = facturaService.calcularPrecioUnitario(Movimiento.COMPRA, "Factura Y", producto);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularPrecioUnitarioWhenEsUnaVentaConFacturaB() {
        Producto producto = new ProductoBuilder()
                           .withPrecioCosto(100.00)
                           .withGanancia_neto(100)
                           .withIva_neto(42)
                           .withPrecioVentaPublico(200)
                           .withPrecioLista(242)
                           .withImpuestoInterno_neto(0.0)
                           .withIva_porcentaje(21)
                           .build();
        double resultadoEsperado = 242;
        double resultadoObtenido = facturaService.calcularPrecioUnitario(Movimiento.VENTA, "Factura B", producto);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularPrecioUnitarioWhenEsUnaVentaConFacturaC() {
        Producto producto = new ProductoBuilder()
                           .withPrecioCosto(100.00)
                           .withGanancia_neto(100)
                           .withIva_neto(42)
                           .withPrecioVentaPublico(200)
                           .withPrecioLista(242)
                           .withImpuestoInterno_neto(0.0)
                           .withIva_porcentaje(21)
                           .build();  
        double resultadoEsperado = 242;
        double resultadoObtenido = facturaService.calcularPrecioUnitario(Movimiento.VENTA, "Factura C", producto);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }

    @Test
    public void shouldCalcularPrecioUnitarioWhenVentaYFacturaY() {
        Producto producto = new ProductoBuilder()
                           .withPrecioCosto(100.00)
                           .withGanancia_neto(100)
                           .withIva_neto(42)
                           .withPrecioVentaPublico(200)
                           .withPrecioLista(242)
                           .withImpuestoInterno_neto(0.0)
                           .withIva_porcentaje(21)
                           .build();  
        double resultadoEsperado = 221;
        double resultadoObtenido = facturaService.calcularPrecioUnitario(Movimiento.VENTA, "Factura Y", producto);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }
}
