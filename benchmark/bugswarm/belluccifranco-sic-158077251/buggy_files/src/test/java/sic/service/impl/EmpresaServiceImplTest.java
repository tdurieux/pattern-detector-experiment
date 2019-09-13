package sic.service.impl;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sic.App;
import sic.modelo.Empresa;
import sic.modelo.EmpresaActiva;
import sic.service.IEmpresaService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = App.class)
@Service
public class EmpresaServiceImplTest {

    @Autowired
    private IEmpresaService empresaService;

    @Test
    public void shouldGetEmpresaActiva() {
        Empresa empresa = Mockito.mock(Empresa.class);
        empresaService.setEmpresaActiva(empresa);
        Empresa expResult = empresa;
        EmpresaActiva result = empresaService.getEmpresaActiva();
        assertEquals(expResult, result.getEmpresa());
    }
}
