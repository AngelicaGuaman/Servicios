/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angelica
 */
@Entity
@Table(name = "Cita")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cita.findAll", query = "SELECT c FROM Cita c"),
    @NamedQuery(name = "Cita.findByIdCita", query = "SELECT c FROM Cita c WHERE c.idCita = :idCita"),
    @NamedQuery(name = "Cita.findByNombreCita", query = "SELECT c FROM Cita c WHERE c.nombreCita = :nombreCita"),
    @NamedQuery(name = "Cita.findByFecha", query = "SELECT c FROM Cita c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Cita.findByIdCalendario", query = "SELECT c FROM Cita c WHERE c.calendarioidCalendario = :idCalendario"),
    @NamedQuery(name = "Cita.findByIdUsuario", query = "SELECT c  FROM Cita c JOIN c.calendarioidCalendario cal WHERE cal.usuarioidUsuario = :idUsuario"),
    @NamedQuery(name = "Cita.findByIdUsuarioOnlyPublic", query = "SELECT c  FROM Cita c JOIN c.calendarioidCalendario calendario WHERE calendario.visibilidad = 1 AND calendario.usuarioidUsuario = :idUsuario")
})
public class Cita implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idCita")
    private Integer idCita;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "nombreCita")
    private String nombreCita;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @JoinColumn(name = "Calendario_idCalendario", referencedColumnName = "idCalendario")
    @ManyToOne(optional = false)
    private Calendario calendarioidCalendario;

    public Cita() {
    }

    public Cita(Integer idCita) {
        this.idCita = idCita;
    }

    public Cita(Integer idCita, String nombreCita, Date fecha) {
        this.idCita = idCita;
        this.nombreCita = nombreCita;
        this.fecha = fecha;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    public String getNombreCita() {
        return nombreCita;
    }

    public void setNombreCita(String nombreCita) {
        this.nombreCita = nombreCita;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Calendario getCalendarioidCalendario() {
        return calendarioidCalendario;
    }

    public void setCalendarioidCalendario(Calendario calendarioidCalendario) {
        this.calendarioidCalendario = calendarioidCalendario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCita != null ? idCita.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cita)) {
            return false;
        }
        Cita other = (Cita) object;
        if ((this.idCita == null && other.idCita != null) || (this.idCita != null && !this.idCita.equals(other.idCita))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Cita[ idCita=" + idCita + " ]";
    }
    
}
