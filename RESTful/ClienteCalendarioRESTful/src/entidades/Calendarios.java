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
@XmlRootElement(name="Calendarios")
public class Calendarios implements Serializable{
   
    private Collection<Calendario> calendario;
    
    public Calendarios()
    {
        
    }
    
    @XmlElement(required=true) //por cada par de get & set
    public Collection<Calendario> getCalendario() 
    {
        return calendario;
    }
    
    public void setCalendario(Collection<Calendario> c)
    {
        this.calendario=c;
    }

}

