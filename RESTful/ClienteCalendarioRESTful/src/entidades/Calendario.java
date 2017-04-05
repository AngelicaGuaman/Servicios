/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entidades;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Angelica
 */
@Entity
@Table(name = "Calendario")
@XmlRootElement
@NamedQueries({
    
    //obtener los calendarios de un usuario, para ello creamos una namedquerie
    @NamedQuery(name = "Calendario.findByIdUsuario", query = "SELECT c FROM Calendario c WHERE c.usuarioidUsuario = :idUsuario "),
    //por partes:     @NamedQuery(name = "ENTIDAD.NOMBRE_DE_LA_QUERY", query = "QUERY EN JPA, SI NECESITAS UN PARAMETRO = :nombreParamaetroEnLaLllamda"),
    @NamedQuery(name = "Calendario.findAll", query = "SELECT c FROM Calendario c"),
    @NamedQuery(name = "Calendario.findByIdCalendario", query = "SELECT c FROM Calendario c WHERE c.idCalendario = :idCalendario"),
    @NamedQuery(name = "Calendario.findByNombreCalendario", query = "SELECT c FROM Calendario c WHERE c.nombreCalendario = :nombreCalendario"),
    @NamedQuery(name = "Calendario.findByVisibilidad", query = "SELECT c FROM Calendario c WHERE c.visibilidad = :visibilidad"),
    @NamedQuery(name = "Calendario.findAllCalendariosDeUsuario", query = "SELECT c FROM Calendario c WHERE c.usuarioidUsuario = :idUsuario"),
    @NamedQuery(name = "Calendario.findPublicCalendarsFromUser", query = "SELECT c FROM Calendario c WHERE c.visibilidad = 1 AND c.usuarioidUsuario = :idUsuario")})
public class Calendario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idCalendario")
    private Integer idCalendario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "nombreCalendario")
    private String nombreCalendario;
    @Basic(optional = false)
    @NotNull
    @Column(name = "visibilidad")
    private int visibilidad;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calendarioidCalendario")
    private Collection<Cita> citaCollection;
    @JoinColumn(name = "Usuario_idUsuario", referencedColumnName = "idUsuario")
    @ManyToOne(optional = false)
    private Usuario usuarioidUsuario;

    public Calendario() {
    }

    public Calendario(Integer idCalendario) {
        this.idCalendario = idCalendario;
    }

    public Calendario(Integer idCalendario, String nombreCalendario, int visibilidad) {
        this.idCalendario = idCalendario;
        this.nombreCalendario = nombreCalendario;
        this.visibilidad = visibilidad;
    }

    public Integer getIdCalendario() {
        return idCalendario;
    }

    public void setIdCalendario(Integer idCalendario) {
        this.idCalendario = idCalendario;
    }

    public String getNombreCalendario() {
        return nombreCalendario;
    }

    public void setNombreCalendario(String nombreCalendario) {
        this.nombreCalendario = nombreCalendario;
    }

    public int getVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(int visibilidad) {
        this.visibilidad = visibilidad;
    }

    @XmlTransient
    public Collection<Cita> getCitaCollection() {
        return citaCollection;
    }

    public void setCitaCollection(Collection<Cita> citaCollection) {
        this.citaCollection = citaCollection;
    }

    public Usuario getUsuarioidUsuario() {
        return usuarioidUsuario;
    }

    public void setUsuarioidUsuario(Usuario usuarioidUsuario) {
        this.usuarioidUsuario = usuarioidUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCalendario != null ? idCalendario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Calendario)) {
            return false;
        }
        Calendario other = (Calendario) object;
        if ((this.idCalendario == null && other.idCalendario != null) || (this.idCalendario != null && !this.idCalendario.equals(other.idCalendario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Calendario[ idCalendario=" + idCalendario + " ]";
    }
    
}
