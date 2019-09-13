package sic.service.impl;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;
import sic.util.Utilidades;

@RunWith(SpringRunner.class)
public class ProductoServiceImplTest {
    
    @InjectMocks
    private ProductoServiceImpl productoService;
    
    @Test
    public void shouldcalcularGanancia_Porcentaje() {
        double precioCosto = 12.34;
        double pvp = 23.45;
        double resultadoEsperado = 90.03;
        double resultadoObtenido = Utilidades.truncarDecimal(productoService.calcularGanancia_Porcentaje(0.0 ,0.0, 0.0, 0.0,precioCosto, pvp, false), 2);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }
    
    @Test
    public void shouldcalcularCalcularGanancia_Neto() {
        double precioCosto = 12.34;
        double gananciaPorcentaje = 100;
        double resultadoEsperado = 12.34;
        double resultadoObtenido = productoService.calcularGanancia_Neto(precioCosto, gananciaPorcentaje);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }
    
    @Test
    public void shouldcalcularCalcularPVP() {
        double precioCosto = 12.34;
        double gananciaPorcentaje = 100;
        double resultadoEsperado = 24.68;
        double resultadoObtenido = productoService.calcularPVP(precioCosto, gananciaPorcentaje);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }
    
    @Test
    public void shouldcalcularCalcularIVA_Neto() {
        double pvp = 24.68;
        double ivaPorcentaje = 21;
        double resultadoEsperado = 5.18;
        double resultadoObtenido = Utilidades.truncarDecimal(productoService.calcularIVA_Neto(pvp, ivaPorcentaje), 2);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }
    
    @Test
    public void shouldCalcularImpInterno_Neto() {
        double pvp = 24.68;
        double impuestoInternoPorcentaje = 10;
        double resultadoEsperado = 2.468;
        double resultadoObtenido = productoService.calcularImpInterno_Neto(pvp, impuestoInternoPorcentaje);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }
    
    @Test
    public void shouldCalcularPrecioLista() {
        double pvp = 24.68;
        double ivaPorcentaje = 21;
        double impuestoInternoPorcentaje = 10;
        double resultadoEsperado = 32.33;
        double resultadoObtenido = Utilidades.truncarDecimal(productoService.calcularPrecioLista(pvp, ivaPorcentaje, impuestoInternoPorcentaje), 2);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }
    
    @Test
    public void shouldCalcularGananciaEnBaseAlPrecioDeLista() {
        double precioDeCosto = 78.87;
        double ivaPorcentaje = 21;
        double pvp = 94.664;
        double ImpInternoPorcentaje = 10;
        double precioDeListaAnterior = 124.00546;
        double precioDeListaNuevo = 125;
        double resultadoEsperado = 20.98;
        double resultadoObtenido = Utilidades.truncarDecimal(productoService.calcularGananciaPorcentajeSegunPrecioDeLista(precioDeListaNuevo,
                precioDeListaAnterior, pvp, ivaPorcentaje, ImpInternoPorcentaje, precioDeCosto), 2);
        assertEquals(resultadoEsperado, resultadoObtenido, 0);
    }
    
}
