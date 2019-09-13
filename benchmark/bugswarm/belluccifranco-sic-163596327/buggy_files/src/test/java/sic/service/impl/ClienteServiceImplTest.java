package sic.service.impl;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Service;
import sic.modelo.Cliente;
import sic.modelo.CondicionIVA;
import sic.modelo.Empresa;
import sic.modelo.Localidad;
import sic.repository.IClienteRepository;
import sic.service.IClienteService;
import sic.service.BusinessServiceException;
import sic.service.TipoDeOperacion;

@Service
public class ClienteServiceImplTest {

    private IClienteService clienteService;
    private Empresa empresa;
    private Cliente cliente;
    private Cliente clienteDuplicado;

    @Mock
    private IClienteRepository clienteRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        empresa = Empresa.builder().build();
        cliente = new Cliente();
        clienteDuplicado = new Cliente();
    }

    @Test
    public void shouldSetClientePredeterminado() {
        when(clienteRepository.getClientePredeterminado(empresa)).thenReturn(cliente);
        clienteService = new ClienteServiceImpl(clienteRepository);
        Cliente resultadoEsperado = cliente;
        Cliente resultadoObtenido = clienteService.getClientePredeterminado(empresa);
        assertEquals(resultadoEsperado, resultadoObtenido);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenEmailInvalido() {
        cliente.setEmail("");
        clienteService = new ClienteServiceImpl(clienteRepository);
        clienteService.validarOperacion(TipoDeOperacion.ELIMINACION, cliente);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenCondicionIVAesNull() {
        cliente.setEmail("emailValido@email.com");
        cliente.setRazonSocial("razon Social");
        cliente.setCondicionIVA(null);
        clienteService = new ClienteServiceImpl(clienteRepository);
        clienteService.validarOperacion(TipoDeOperacion.ELIMINACION, cliente);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenLocalidadEsNull() {
        cliente.setEmail("emailValido@email.com");
        cliente.setRazonSocial("razon Social");
        cliente.setCondicionIVA(new CondicionIVA());
        cliente.setLocalidad(null);
        clienteService = new ClienteServiceImpl(clienteRepository);
        clienteService.validarOperacion(TipoDeOperacion.ELIMINACION, cliente);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenEmpresaEsNull() {
        cliente.setEmail("emailValido@email.com");
        cliente.setRazonSocial("razon Social");
        cliente.setCondicionIVA(new CondicionIVA());
        cliente.setLocalidad(new Localidad());
        cliente.setEmpresa(null);
        clienteService = new ClienteServiceImpl(clienteRepository);
        clienteService.validarOperacion(TipoDeOperacion.ELIMINACION, cliente);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenIdFiscalDuplicadoEnAlta() {
        cliente.setEmail("emailValido@email.com");
        cliente.setRazonSocial("razon Social");
        cliente.setCondicionIVA(new CondicionIVA());
        cliente.setLocalidad(new Localidad());
        cliente.setEmpresa(Empresa.builder().build());
        cliente.setId_Fiscal("23111111119");
        clienteDuplicado.setEmail("emailValido@email.com");
        clienteDuplicado.setRazonSocial("razon Social");
        clienteDuplicado.setCondicionIVA(new CondicionIVA());
        clienteDuplicado.setLocalidad(new Localidad());
        clienteDuplicado.setEmpresa(Empresa.builder().build());
        clienteDuplicado.setId_Fiscal("23111111119");
        when(clienteRepository.getClientePorId_Fiscal(cliente.getId_Fiscal(), cliente.getEmpresa())).thenReturn(cliente);
        clienteService = new ClienteServiceImpl(clienteRepository);
        clienteService.validarOperacion(TipoDeOperacion.ALTA, clienteDuplicado);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenIdFiscalDuplicadoEnActualizacion() {
        cliente.setEmail("emailValido@email.com");
        cliente.setRazonSocial("razon Social");
        cliente.setCondicionIVA(new CondicionIVA());
        cliente.setLocalidad(new Localidad());
        cliente.setEmpresa(Empresa.builder().build());
        cliente.setId_Fiscal("23111111119");
        cliente.setId_Cliente(Long.MIN_VALUE);
        clienteDuplicado.setEmail("emailValido@email.com");
        clienteDuplicado.setRazonSocial("razon Social");
        clienteDuplicado.setCondicionIVA(new CondicionIVA());
        clienteDuplicado.setLocalidad(new Localidad());
        clienteDuplicado.setEmpresa(Empresa.builder().build());
        clienteDuplicado.setId_Fiscal("23111111119");
        clienteDuplicado.setId_Cliente(Long.MAX_VALUE);
        when(clienteRepository.getClientePorId_Fiscal(cliente.getId_Fiscal(), cliente.getEmpresa())).thenReturn(cliente);
        clienteService = new ClienteServiceImpl(clienteRepository);
        clienteService.validarOperacion(TipoDeOperacion.ACTUALIZACION, clienteDuplicado);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenRazonSocialDuplicadaEnAlta() {
        cliente.setEmail("emailValido@email.com");
        cliente.setRazonSocial("razon Social");
        cliente.setCondicionIVA(new CondicionIVA());
        cliente.setLocalidad(new Localidad());
        cliente.setEmpresa(Empresa.builder().build());
        cliente.setId_Fiscal("23111111119");
        cliente.setId_Cliente(Long.MIN_VALUE);
        clienteDuplicado.setEmail("emailValido@email.com");
        clienteDuplicado.setRazonSocial("razon Social");
        clienteDuplicado.setCondicionIVA(new CondicionIVA());
        clienteDuplicado.setLocalidad(new Localidad());
        clienteDuplicado.setEmpresa(Empresa.builder().build());
        clienteDuplicado.setId_Fiscal("23111111119");
        clienteDuplicado.setId_Cliente(Long.MAX_VALUE);
        when(clienteRepository.getClientePorRazonSocial(cliente.getRazonSocial(), cliente.getEmpresa())).thenReturn(cliente);
        clienteService = new ClienteServiceImpl(clienteRepository);
        clienteService.validarOperacion(TipoDeOperacion.ALTA, clienteDuplicado);
    }

    @Test(expected = BusinessServiceException.class)
    public void shouldValidarOperacionWhenRazonSocialDuplicadaEnActualizacion() {
        cliente.setEmail("emailValido@email.com");
        cliente.setRazonSocial("razon Social");
        cliente.setCondicionIVA(new CondicionIVA());
        cliente.setLocalidad(new Localidad());
        cliente.setEmpresa(Empresa.builder().build());
        cliente.setId_Fiscal("23111111119");
        cliente.setId_Cliente(Long.MIN_VALUE);
        clienteDuplicado.setEmail("emailValido@email.com");
        clienteDuplicado.setRazonSocial("razon Social");
        clienteDuplicado.setCondicionIVA(new CondicionIVA());
        clienteDuplicado.setLocalidad(new Localidad());
        clienteDuplicado.setEmpresa(Empresa.builder().build());
        clienteDuplicado.setId_Fiscal("23111111119");
        clienteDuplicado.setId_Cliente(Long.MAX_VALUE);
        when(clienteRepository.getClientePorRazonSocial(cliente.getRazonSocial(), cliente.getEmpresa())).thenReturn(cliente);
        clienteService = new ClienteServiceImpl(clienteRepository);
        clienteService.validarOperacion(TipoDeOperacion.ACTUALIZACION, clienteDuplicado);
    }
}
