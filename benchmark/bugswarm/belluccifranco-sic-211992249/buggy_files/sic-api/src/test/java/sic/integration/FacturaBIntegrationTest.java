package sic.integration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientResponseException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import sic.builder.ClienteBuilder;
import sic.builder.CondicionIVABuilder;
import sic.builder.EmpresaBuilder;
import sic.builder.FormaDePagoBuilder;
import sic.builder.LocalidadBuilder;
import sic.builder.MedidaBuilder;
import sic.builder.ProductoBuilder;
import sic.builder.ProveedorBuilder;
import sic.builder.RubroBuilder;
import sic.builder.TransportistaBuilder;
import sic.builder.UsuarioBuilder;
import sic.modelo.Cliente;
import sic.modelo.CondicionIVA;
import sic.modelo.Credencial;
import sic.modelo.Empresa;
import sic.modelo.Factura;
import sic.modelo.FacturaVenta;
import sic.modelo.FormaDePago;
import sic.modelo.Localidad;
import sic.modelo.Medida;
import sic.modelo.Movimiento;
import sic.modelo.Pais;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Provincia;
import sic.modelo.RenglonFactura;
import sic.modelo.Rubro;
import sic.modelo.Transportista;
import sic.modelo.Usuario;
import sic.modelo.dto.FacturaVentaDTO;
import sic.repository.UsuarioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FacturaBIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String token;
    
    private final String apiPrefix = "/api/v1";
   
    @Before
    public void setup() {
        String md5Test = "098f6bcd4621d373cade4e832627b4f6";
        usuarioRepository.save(new UsuarioBuilder().withNombre("test").withPassword(md5Test).build());
        // Interceptor de RestTemplate para JWT
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((ClientHttpRequestInterceptor) (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            request.getHeaders().set("Authorization", "Bearer " + token);
            return execution.execute(request, body);
        });        
        restTemplate.getRestTemplate().setInterceptors(interceptors);
        // ErrorHandler para RestTemplate        
        restTemplate.getRestTemplate().setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus.Series series = response.getStatusCode().series();
                return (HttpStatus.Series.CLIENT_ERROR.equals(series) || HttpStatus.Series.SERVER_ERROR.equals(series));
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                String mensaje = IOUtils.toString(response.getBody());                
                throw new RestClientResponseException(mensaje, response.getRawStatusCode(),
                        response.getStatusText(), response.getHeaders(),
                        null, Charset.defaultCharset());
            }
        });
    }

    @Test
    public void test() {
        this.token = restTemplate.postForEntity(apiPrefix + "/login", new Credencial("test", "test"), String.class).getBody();
        Localidad localidad = new LocalidadBuilder().build();
        localidad.getProvincia().setPais(restTemplate.postForObject(apiPrefix + "/paises", localidad.getProvincia().getPais(), Pais.class));
        localidad.setProvincia(restTemplate.postForObject(apiPrefix + "/provincias", localidad.getProvincia(), Provincia.class));
        CondicionIVA condicionIVA = new CondicionIVABuilder().build();          
        Empresa empresa = new EmpresaBuilder()
                .withLocalidad(restTemplate.postForObject(apiPrefix + "/localidades", localidad, Localidad.class))
                .withCondicionIVA(restTemplate.postForObject(apiPrefix + "/condiciones-iva", condicionIVA, CondicionIVA.class))
                .build();
        empresa = restTemplate.postForObject(apiPrefix + "/empresas", empresa, Empresa.class);
        FormaDePago formaDePago = new FormaDePagoBuilder()
                .withAfectaCaja(false)
                .withEmpresa(empresa)
                .withPredeterminado(true)
                .withNombre("Efectivo")
                .build();
        restTemplate.postForObject(apiPrefix + "/formas-de-pago", formaDePago, FormaDePago.class);
        Cliente cliente = new ClienteBuilder()
                .withEmpresa(empresa)
                .withCondicionIVA(empresa.getCondicionIVA())
                .withLocalidad(empresa.getLocalidad())
                .withPredeterminado(true)
                .build();
        cliente = restTemplate.postForObject(apiPrefix + "/clientes", cliente, Cliente.class);
        Transportista transportista = new TransportistaBuilder()
                .withEmpresa(empresa)
                .withLocalidad(empresa.getLocalidad())
                .build();
        transportista = restTemplate.postForObject(apiPrefix + "/transportistas", transportista, Transportista.class);
        Medida medida = new MedidaBuilder().withEmpresa(empresa).build();
        medida = restTemplate.postForObject(apiPrefix + "/medidas", medida, Medida.class);
        Proveedor proveedor = new ProveedorBuilder().withEmpresa(empresa)
                .withLocalidad(empresa.getLocalidad())
                .withCondicionIVA(empresa.getCondicionIVA())
                .build();
        proveedor = restTemplate.postForObject(apiPrefix + "/proveedores", proveedor, Proveedor.class);
        Rubro rubro = new RubroBuilder().withEmpresa(empresa).build();
        rubro = restTemplate.postForObject(apiPrefix + "/rubros", rubro, Rubro.class);
        Producto productoUno = (new ProductoBuilder())
                .withCodigo("1")
                .withDescripcion("uno")
                .withCantidad(10)
                .withIva_porcentaje(21.0)
                .withEmpresa(empresa)
                .withMedida(medida)
                .withProveedor(proveedor)
                .withRubro(rubro)
                .build();

        Producto productoDos = (new ProductoBuilder())
                .withIva_porcentaje(10.5)
                .withCodigo("2")
                .withDescripcion("dos")
                .withCantidad(6)
                .withEmpresa(empresa)
                .withMedida(medida)
                .withProveedor(proveedor)
                .withRubro(rubro)
                .build();
        productoUno = restTemplate.postForObject(apiPrefix + "/productos", productoUno, Producto.class);
        productoDos = restTemplate.postForObject(apiPrefix + "/productos", productoDos, Producto.class);
        assertEquals(10, productoUno.getCantidad(), 0);
        assertEquals(6, productoDos.getCantidad(), 0);
        RenglonFactura renglonUno = restTemplate.getForObject(apiPrefix + "/facturas/renglon?"
                + "idProducto=" + productoUno.getId_Producto()
                + "&tipoComprobante=" + 'B'
                + "&movimiento=" + Movimiento.VENTA
                + "&cantidad=" + 5
                + "&descuentoPorcentaje=" + 0,
                RenglonFactura.class);
        RenglonFactura renglonDos = restTemplate.getForObject(apiPrefix + "/facturas/renglon?"
                + "idProducto=" + productoDos.getId_Producto()
                + "&tipoComprobante=" + 'B'
                + "&movimiento=" + Movimiento.VENTA
                + "&cantidad=" + 2
                + "&descuentoPorcentaje=" + 0,
                RenglonFactura.class);
        List<RenglonFactura> renglones = new ArrayList<>();
        renglones.add(renglonUno);
        renglones.add(renglonDos);        
        double[] importes = new double[renglones.size()];
        double[] ivaRenglones = new double[renglones.size()];
        double[] impuestoPorcentajes = new double[renglones.size()];
        int indice = 0;
        for (RenglonFactura renglon : renglones) {
            importes[indice] = renglon.getImporte();
            ivaRenglones[indice] = renglon.getIva_porcentaje();
            impuestoPorcentajes[indice] = renglon.getImpuesto_porcentaje();
            indice++;
        }
        double subTotal = restTemplate.getRestTemplate().getForObject(apiPrefix +"/facturas/subtotal?"
                    + "importe=" + Arrays.toString(importes).substring(1, Arrays.toString(importes).length() - 1),
                    double.class);
        assertEquals(1270.5, subTotal, 0);
        double recargo_neto = restTemplate.getRestTemplate().getForObject(apiPrefix +"/facturas/recargo-neto?"
                    + "subTotal=" + subTotal
                    + "&recargoPorcentaje=" + 10, double.class);
        assertEquals(127.05, recargo_neto, 0);
        double subTotal_neto = restTemplate.getRestTemplate().getForObject(apiPrefix +"/facturas/subtotal-neto?"
                    + "subTotal=" + subTotal
                    + "&recargoNeto=" + recargo_neto
                    + "&descuentoNeto=0", double.class);
        assertEquals(1397.55, subTotal_neto, 0);
        double iva_105_neto = restTemplate.getRestTemplate().getForObject(apiPrefix +"/facturas/iva-neto?"
                    + "tipoFactura=" + 'B'
                    + "&descuentoPorcentaje=0"
                    + "&recargoPorcentaje=" + 10
                    + "&ivaPorcentaje=10.5"
                    + "&importe=" + Arrays.toString(importes).substring(1, Arrays.toString(importes).length() - 1)
                    + "&ivaRenglones=" + Arrays.toString(ivaRenglones).substring(1, Arrays.toString(ivaRenglones).length() - 1),
                    double.class);
        assertEquals(0, iva_105_neto, 0);
        double iva_21_neto = restTemplate.getRestTemplate().getForObject(apiPrefix +"/facturas/iva-neto?"
                    + "tipoFactura=" + 'B'
                    + "&descuentoPorcentaje=0"
                    + "&recargoPorcentaje=" + 10
                    + "&ivaPorcentaje=21.0"
                    + "&importe=" + Arrays.toString(importes).substring(1, Arrays.toString(importes).length() - 1)
                    + "&ivaRenglones=" + Arrays.toString(ivaRenglones).substring(1, Arrays.toString(ivaRenglones).length() - 1),
                    double.class);
        assertEquals(0, iva_21_neto, 0);
        double impInterno_neto = restTemplate.getRestTemplate().getForObject(apiPrefix +"/facturas/impuesto-interno-neto?"
                    + "tipoFactura=" + 'B'
                    + "&descuentoPorcentaje=0"
                    + "&recargoPorcentaje=" + 10
                    + "&importe=" + Arrays.toString(importes).substring(1, Arrays.toString(importes).length() - 1)
                    + "&impuestoPorcentaje=" + Arrays.toString(impuestoPorcentajes).substring(1, Arrays.toString(impuestoPorcentajes).length() - 1),
                    double.class);
        assertEquals(0, impInterno_neto, 0);
        double total = restTemplate.getRestTemplate().getForObject(apiPrefix +"/facturas/total?"
                    + "subTotal=" + subTotal
                    + "&descuentoNeto=0"
                    + "&recargoNeto=" + recargo_neto
                    + "&iva105Neto=" + iva_105_neto
                    + "&iva21Neto=" + iva_21_neto
                    + "&impuestoInternoNeto=" + impInterno_neto, double.class);        
        assertEquals(1397.55, total, 0);
        FacturaVentaDTO facturaVentaB = new FacturaVentaDTO();
        facturaVentaB.setTipoFactura('B');
        facturaVentaB.setCliente(cliente);
        facturaVentaB.setEmpresa(empresa);
        facturaVentaB.setTransportista(transportista);
        facturaVentaB.setUsuario(restTemplate.getForObject(apiPrefix + "/usuarios/busqueda?nombre=test", Usuario.class));
        facturaVentaB.setRenglones(renglones);
        facturaVentaB.setSubTotal(subTotal);
        facturaVentaB.setRecargo_neto(recargo_neto);
        facturaVentaB.setSubTotal_neto(subTotal_neto);
        facturaVentaB.setIva_105_neto(iva_105_neto);
        facturaVentaB.setIva_21_neto(iva_21_neto);
        facturaVentaB.setImpuestoInterno_neto(impInterno_neto);
        facturaVentaB.setTotal(total);
        facturaVentaB.setFecha(new Date());
        restTemplate.postForObject(apiPrefix + "/facturas", facturaVentaB, Factura[].class);
        FacturaVenta[] facturasRecuperadas = restTemplate.getForObject(apiPrefix + "/facturas/venta/busqueda/criteria?idEmpresa=1&tipoFactura=B&nroSerie=1&nroFactura=1", FacturaVenta[].class);
        if (facturasRecuperadas.length != 1) {
            Assert.fail("No deberia existir mas de una factura");
        } 
        assertEquals(facturaVentaB.getEmpresa(), facturasRecuperadas[0].getEmpresa());
        assertEquals(facturaVentaB.getTipoFactura(), facturasRecuperadas[0].getTipoFactura());
        assertEquals(facturaVentaB.getFecha(), facturasRecuperadas[0].getFecha());
        assertEquals(facturaVentaB.getSubTotal(), facturasRecuperadas[0].getSubTotal(), 0);
        assertEquals(facturaVentaB.getRecargo_neto(), facturasRecuperadas[0].getRecargo_neto(), 0);
        assertEquals(facturaVentaB.getSubTotal_neto(), facturasRecuperadas[0].getSubTotal_neto(), 0);
        assertEquals(facturaVentaB.getIva_105_neto(), facturasRecuperadas[0].getIva_105_neto(), 0);
        assertEquals(facturaVentaB.getIva_21_neto(), facturasRecuperadas[0].getIva_21_neto(), 0);
        assertEquals(facturaVentaB.getImpuestoInterno_neto(), facturasRecuperadas[0].getImpuestoInterno_neto(), 0);
        assertEquals(facturaVentaB.getTotal(), facturasRecuperadas[0].getTotal(), 0);
        RenglonFactura[] renglonesDeFacturaRecuperada = restTemplate.getForObject(apiPrefix + "/facturas/" + facturasRecuperadas[0].getId_Factura() + "/renglones", RenglonFactura[].class);
        if (renglonesDeFacturaRecuperada.length != 2) {
            Assert.fail("La factura no deberia tener mas de dos renglones");
        } 
        long[] idsProductos = new long[renglonesDeFacturaRecuperada.length];
        indice = 0;
        for(RenglonFactura renglon : renglonesDeFacturaRecuperada) {
            idsProductos[indice] = renglon.getId_ProductoItem();
            indice++;
        }
        restTemplate.getForObject(apiPrefix + "/facturas/"+ facturasRecuperadas[0].getId_Factura() + "/reporte", byte[].class);        
        assertEquals(5, restTemplate.getForObject(apiPrefix + "/productos/" + idsProductos[0], Producto.class).getCantidad(), 0);
        assertEquals(4, restTemplate.getForObject(apiPrefix + "/productos/" + idsProductos[1], Producto.class).getCantidad(), 0);
    }

}
