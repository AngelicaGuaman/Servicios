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
@XmlRootElement(name="Usuarios")
public class Usuarios implements Serializable
{

    private Collection<Usuario> u;
    
    public Usuarios()
    {
    //requerido por jax-b
    }
    
    @XmlElement(required=true)
    //xmlelement por cada par get y sets
    public Collection<Usuario> getU()
    {
        return u;
    }
    
    public void setU(Collection<Usuario> usu)
    {
        this.u=usu;
    }
    
}
