package sic.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "empresa")
@NamedQueries({
    @NamedQuery(name = "Empresa.buscarPorId",
            query = "SELECT e FROM Empresa e "
                    + "WHERE p.eliminada = false AND e.id_Empresa = :id"),
    @NamedQuery(name = "Empresa.buscarTodas",
            query = "SELECT e FROM Empresa e "
                    + "WHERE e.eliminada = false "
                    + "ORDER BY e.nombre ASC"),
    @NamedQuery(name = "Empresa.buscarPorId",
            query = "SELECT e FROM Empresa e "
                    + "WHERE e.id_Empresa = :id AND e.eliminada = false"),
    @NamedQuery(name = "Empresa.buscarPorNombre",
            query = "SELECT e FROM Empresa e "
                    + "WHERE e.nombre LIKE :nombre AND e.eliminada = false "
                    + "ORDER BY e.nombre ASC"),
    @NamedQuery(name = "Empresa.buscarPorCUIP",
            query = "SELECT e FROM Empresa e "
                    + "WHERE e.cuip = :cuip AND e.eliminada = false")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"nombre"})
public class Empresa implements Serializable {

    @Id
    @GeneratedValue
    private long id_Empresa;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String lema;

    @Column(nullable = false)
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "id_CondicionIVA", referencedColumnName = "id_CondicionIVA")
    private CondicionIVA condicionIVA;

    private long cuip;

    private long ingresosBrutos;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicioActividad;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "id_Localidad", referencedColumnName = "id_Localidad")
    private Localidad localidad;

    @Lob
    private byte[] logo;

    private boolean eliminada;

    @Override
    public String toString() {
        return nombre;
    }

}
