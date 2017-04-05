/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entidades;

import java.io.Serializable;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angelica
 */
@XmlRootElement(name="Citas")
public class Citas implements Serializable
{    
    private Collection<Cita> cita;
    
    public Citas()
    {
        
    }
    
    @XmlElement(required=true)
    public Collection<Cita> getCita()
    {
        return cita;
    }
    public void setCita(Collection<Cita> citas)
    {
        this.cita=citas;
    }
    
}
