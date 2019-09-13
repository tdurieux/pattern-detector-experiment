package sic.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "transportista")
@NamedQueries({
    @NamedQuery(name = "Transportista.buscarPorId",
            query = "SELECT t FROM Transportista t "
                    + "WHERE t.eliminado = false AND t.id_Transportista= :id"),
    @NamedQuery(name = "Transportista.buscarTodos",
            query = "SELECT t FROM Transportista t "
                    + "WHERE t.empresa = :empresa AND t.eliminado = false "
                    + "ORDER BY t.nombre ASC"),
    @NamedQuery(name = "Transportista.buscarPorNombre",
            query = "SELECT t FROM Transportista t "
                    + "WHERE t.empresa = :empresa AND t.nombre = :nombre AND t.eliminado = false "
                    + "ORDER BY t.nombre ASC")
})
@Data
@EqualsAndHashCode(of = {"nombre", "empresa"})
public class Transportista implements Serializable {

    @Id
    @GeneratedValue
    private long id_Transportista;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "id_Localidad", referencedColumnName = "id_Localidad")
    private Localidad localidad;

    @Column(nullable = false)
    private String web;

    @Column(nullable = false)
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "id_Empresa", referencedColumnName = "id_Empresa")
    private Empresa empresa;

    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }

}
