package sic.service.impl;

import sic.service.IGastoService;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sic.modelo.Gasto;
import sic.service.BusinessServiceException;
import sic.repository.GastoRepository;
import sic.service.IEmpresaService;
import sic.service.IFormaDePagoService;

@Service
public class GastoServiceImpl implements IGastoService {

    private final GastoRepository gastoRepository;
    private final IEmpresaService empresaService;
    private final IFormaDePagoService formaDePagoService;
    private static final Logger LOGGER = Logger.getLogger(GastoServiceImpl.class.getPackage().getName());

    @Autowired
    public GastoServiceImpl(GastoRepository gastoRepository, IEmpresaService empresaService, 
                            IFormaDePagoService formaDePagoService) {
        this.gastoRepository = gastoRepository;
        this.empresaService = empresaService;
        this.formaDePagoService = formaDePagoService;
    }
    
    @Override
    public Gasto getGastoPorId(Long idGasto) {
        Gasto gasto = gastoRepository.findOne(idGasto);
        if (gasto == null) {
            throw new EntityNotFoundException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_no_existente"));
        }
        return gasto;
    }

    @Override
    public void validarGasto(Gasto gasto) {
        //Entrada de Datos
        //Requeridos
        if (gasto.getFecha() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_fecha_vacia"));
        }
        if (gasto.getEmpresa() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_empresa_vacia"));
        }
        if (gasto.getUsuario() == null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_usuario_vacio"));
        }
        //Duplicados
        if (gastoRepository.findOne(gasto.getId_Gasto()) != null) {
            throw new BusinessServiceException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_gasto_duplicada"));
        }
    }

    @Override
    @Transactional
    public Gasto guardar(Gasto gasto) {
        this.validarGasto(gasto);
        gasto.setNroGasto(this.getUltimoNumeroDeGasto(gasto.getEmpresa().getId_Empresa()) + 1);
        gasto = gastoRepository.save(gasto);
        LOGGER.warn("El Gasto " + gasto + " se guardó correctamente." );
        return gasto;
    }

    @Override
    public List<Gasto> getGastosPorFecha(Long idEmpresa, Date desde, Date hasta) {
        return gastoRepository.findAllByFechaBetweenAndEmpresaAndEliminado(desde, hasta, empresaService.getEmpresaPorId(idEmpresa), false);
    }

    @Override
    public Gasto getGastosPorNroYEmpreas(Long nroPago, Long idEmpresa) {
        return gastoRepository.findByNroGastoAndEmpresaAndEliminado(nroPago, empresaService.getEmpresaPorId(idEmpresa), false);
    }

    @Override
    public List<Gasto> getGastosPorFechaYFormaDePago(Long idEmpresa, Long idFormaDePago, Date desde, Date hasta) {
        return gastoRepository.findAllByFechaBetweenAndEmpresaAndFormaDePagoAndEliminado(desde, hasta, empresaService.getEmpresaPorId(idEmpresa), 
                formaDePagoService.getFormasDePagoPorId(idFormaDePago), false);
    }

    @Override
    @Transactional
    public void actualizar(Gasto gasto) {
        gastoRepository.save(gasto);
    }
    
    @Override
    @Transactional
    public void eliminar(long idGasto) {
        Gasto gastoParaEliminar = this.getGastoPorId(idGasto);
        gastoParaEliminar.setEliminado(true);
        gastoRepository.save(gastoParaEliminar);
    }
    
    @Override
    public long getUltimoNumeroDeGasto(long idEmpresa) {
        return gastoRepository.findTopByEmpresaAndEliminadoOrderByNroGastoDesc(empresaService.getEmpresaPorId(idEmpresa), false).getNroGasto();
    }

}
