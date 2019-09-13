package sic.modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "usuario")
@NamedQueries({
    @NamedQuery(name = "Usuario.buscarPorId",
            query = "SELECT u FROM Usuario u "
                    + "WHERE u.eliminado = false AND u.id_Usuario= :id"),
    @NamedQuery(name = "Usuario.buscarTodos",
            query = "SELECT u FROM Usuario u "
                    + "WHERE u.eliminado = false "
                    + "ORDER BY u.nombre ASC"),
    @NamedQuery(name = "Usuario.buscarPorNombre",
            query = "SELECT u FROM Usuario u "
                    + "WHERE u.eliminado = false AND u.nombre = :nombre"),
    @NamedQuery(name = "Usuario.buscarUsuariosAdministradores",
            query = "SELECT u FROM Usuario u "
                    + "WHERE u.eliminado = false AND u.permisosAdministrador = true "
                    + "ORDER BY u.nombre ASC"),
    @NamedQuery(name = "Usuario.buscarPorNombreContrasenia",
            query = "SELECT u FROM Usuario u "
                    + "WHERE u.eliminado = false AND u.nombre = :nombre AND u.password = :password")
})
@Data
@EqualsAndHashCode(of = {"nombre"})
public class Usuario implements Serializable {

    @Id
    @GeneratedValue
    private long id_Usuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String password;

    private boolean permisosAdministrador;

    private boolean eliminado;

    @Override
    public String toString() {
        return nombre;
    }
}
