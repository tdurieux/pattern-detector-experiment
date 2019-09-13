package sic.util;

import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValidatorTest {

    @Test
    public void shouldEsNumericoPositivoWhenNegativo() {
        String cadena = "-3";
        boolean expResult = false;
        boolean result = Validator.esNumericoPositivo(cadena);
        assertEquals(expResult, result);
    }

    @Test
    public void shouldEsVacioWhenNoEsVacio() {
        String campo = "no Vacio";
        boolean expResult = false;
        boolean result = Validator.esVacio(campo);
        assertEquals(expResult, result);
    }

    @Test
    public void shouldEsLongitudCaracteresValidaWhenNoEsValida() {
        String cadena = "esLongitudCaracteresValida";
        int cantCaracteresValidos = 0;
        boolean expResult = false;
        boolean result = Validator.esLongitudCaracteresValida(cadena, cantCaracteresValidos);
        assertEquals(expResult, result);
    }

    @Test
    public void shouldEsLongitudNumericaValidaWhenNoValida() {
        String cadena = "111.11111";
        double longitudValida = 0.0;
        boolean expResult = false;
        boolean result = Validator.esLongitudNumericaValida(cadena, longitudValida);
        assertEquals(expResult, result);
    }

    @Test
    public void shouldEsEmailValidoWhenNoLoEs() {
        String cadena = "No es un mail Valido";
        boolean expResult = false;
        boolean result = Validator.esEmailValido(cadena);
        assertEquals(expResult, result);
    }

    @Test
    public void shouldCompararFechasWhenFechasIguales() {
        Date fechaAnterior = new Date();
        Date fechaSiguiente = new Date();
        int expResult = 0;
        int result = Validator.compararFechas(fechaAnterior, fechaSiguiente);
        assertEquals(expResult, result);
    }

}
