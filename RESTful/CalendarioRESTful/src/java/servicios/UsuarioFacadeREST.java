/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servicios;

import entidades.Calendario;
import entidades.Calendarios;
import entidades.Cita;
import entidades.Citas;
import entidades.Usuario;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Angelica
 */
@Stateless
@Path("entidades.usuario")
public class UsuarioFacadeREST extends AbstractFacade<Usuario> {
    @PersistenceContext(unitName = "CalendarioRESTfulPU")
    private EntityManager em;

    private @Context UriInfo uriInfo;
    
    public UsuarioFacadeREST() {
        super(Usuario.class);
    }
    
    
    @POST
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public Response crear(@PathParam("id") Integer id, Usuario entity)
    {
        Usuario usu;
        Response response;
        
        //primero averiguamos si el usuario existe en BD
        usu = em.find(Usuario.class, id);//si existe devuelve el usuario asociado a id, sino null.
        
        if(usu == null)
        {
            super.create(entity);
            response = Response.status(Response.Status.CREATED).location(uriInfo.getAbsolutePath()).build();
        }else
            response=Response.status(Response.Status.CONFLICT).build();
        
        return response;
    }
    
    
    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public Response modificar(@PathParam("id") Integer id, Usuario entity)
    {
        Usuario usu;
        Response response;
        
        //primero averiguamos si el usuario existe en BD
        usu = em.find(Usuario.class, id);//si existe devuelve el usuario asociado a id, sino null.
        
        //si existe lo modificamos
        if(usu != null)
        {
            super.edit(entity);
            response = Response.status(Response.Status.OK).location(uriInfo.getAbsolutePath()).build();
        }else//no existe
            response=Response.status(Response.Status.NOT_MODIFIED).build();
        
        return response;
           
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") Integer id) {
        
        Usuario usu;
        Response response;
        
        //primero averiguamos si el usuario existe en BD
        usu = em.find(Usuario.class, id);//si existe devuelve el usuario asociado a id, sino null.
        
        if(usu != null)
        {//si existe borramos
            super.remove(usu);
            response = Response.status(Response.Status.OK).location(uriInfo.getAbsolutePath()).build();
        }else//no, mostramos un mensaje que no sse puede
            response=Response.status(Response.Status.NO_CONTENT).build();
        
        return response;
    }
    
     //FUNCIONAAAAAAAAAAAA!!!
    @POST
    @Path("{id}/calendarios")
    @Consumes({"application/xml", "application/json"})
    public Response crearCalendario(@PathParam("id") Integer id, Calendario calendario)
    {
        Response response;
        javax.persistence.Query query;
        
        Usuario usuario;
        Calendario calen;
        
        usuario=em.find(Usuario.class,id);
        calen = em.find(Calendario.class, calendario.getIdCalendario());
        
        //si existe el usuario y no existe el calendario en la BBDD
        if(usuario != null && calen==null)
        {
            //creamos el calendario si no existe
            em.persist(calendario);
            URI loc = URI.create(uriInfo.getAbsolutePath()+"/"+calendario.getIdCalendario().intValue());
            response=Response.status(Response.Status.CREATED).location(loc).build();
        }else
            response=Response.status(Response.Status.CONFLICT).build();
        
        return response;
        
    }
    
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Usuario find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Usuario> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Usuario> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    
    
    /*
       URIs y recursos
        .../ -> te devuelve todos los usuarios
        .../{id} -> te devuelve el usuario asociado a id
        .../{id}/calendarios?idUsuario=id2 -> te devuelve los calendarios del usuario id2, si id2==id, se devolveran todos los calendarios sino solo los publicos(de id2)    
    */
    @GET
    @Path("{id}/calendarios")
    @Produces({"application/xml", "application/json"})
    public Response buscarCalendariosDeUnUsuario(@PathParam("id") Integer id,
     @QueryParam("idUsuario") Integer idUsuario)
    {
        Response response;
        Usuario usu1, usu2;
        javax.persistence.Query query;
        Calendarios calendarios=new Calendarios();
    
        //primero averiguamos si el usuario existe en BD
        usu1 = em.find(Usuario.class, id);
          //primero averiguamos si el usuario existe en BD
        usu2 = em.find(Usuario.class, idUsuario);
        
        //si existe
        if(usu1 != null && usu2 != null)
        {
            if(id.intValue()==idUsuario.intValue())
                query=em.createNamedQuery("Calendario.findAllCalendariosDeUsuario");//todos los calendarios
            else
                query = em.createNamedQuery("Calendario.findPublicCalendarsFromUser");//solo los publicos
            
            query.setParameter("idusuario", new Usuario(idUsuario));
            
           calendarios.setCalendario(query.getResultList());
           
         response = Response.status(Response.Status.OK).entity(calendarios).build();
            
        }else
            response = Response.status(Response.Status.NOT_FOUND).build();//si no existe un not found
        
        return response;
    }
    
    //FUNCIONAAAAAAAAAAAA!!!
    @GET
    @Path("{id}/calendarios/{idCalendario}")
    @Produces({"application/xml", "application/json"})
    public Response encontrarCalendarioUsuario(@PathParam("id")Integer id,
            @PathParam("idCalendario") Integer idCalendario)
    {
        Response response;
        javax.persistence.Query query;
        List<Calendario> calendarios = new ArrayList<Calendario>();
        
        //recuperamos todos los calendarios de un usuario
        query=em.createNamedQuery("Calendario.findAllCalendariosDeUsuario");
        
        query.setParameter("idUsuario", new Usuario(id));
        
        calendarios.addAll(query.getResultList());
        
        Calendario calendario = em.find(Calendario.class, idCalendario);
        
        Usuario usuario = em.find(Usuario.class, id);
        
        //si el id del calendario no existe en la BBDD no se pueden mostrar los datos
        if(usuario!= null && calendario!=null && calendarios.contains(calendario))
            response=Response.status(Response.Status.OK).entity(calendario).build();
        else
            response=Response.status(Response.Status.NOT_FOUND).build();
        
        return response;
        
    }
    
    
    @PUT
    @Path("{id}/calendarios")
    public Response actualizarCalendarioUsuario(@PathParam("id") Integer id,
            @QueryParam("idCalendario") Integer idCalendario, @QueryParam("nombreCalendario") String nombreCalendario)
    {
       Response response;
       Usuario usuario;
       Calendario calendario;
       
       usuario = em.find(Usuario.class, id);
       calendario = em.find(Calendario.class, idCalendario);
       
       if(usuario != null && calendario != null)
       {
           //HAY QUE BUSCAR QUE ESTEN REFERENCIADOS
           
           calendario.setNombreCalendario(nombreCalendario);
           
           response=Response.status(Response.Status.CREATED).location(uriInfo.getAbsolutePath()).build();
       }else
           
           response=Response.status(Response.Status.CONFLICT).build();
           
       return response;
    }
    /*
       URIs y recursos
        .../ -> te devuelve todos los usuarios
        .../{id} -> te devuelve el usuario asociado a id
        .../{id}/calendarios?idUsuario=id2 -> te devuelve los calendarios del usuario id2, si id2==id, se devolveran todos los calendarios sino solo los publicos(de id2)    
    */
    
    //../{id}/citas?idUsuario=id2
    @GET
    @Path("{id}/citas")
    @Produces({"application/xml", "application/json"})
    @SuppressWarnings("empty-statement")
    public Response obtenerCitasDeUnUsuario (@PathParam("id") Integer id, @QueryParam("idUsuario") Integer idUsuario,
                @DefaultValue("-")@QueryParam("from") String from,
                @DefaultValue("-")@QueryParam("to") String to,
                @DefaultValue("0")@QueryParam("idCalendario") Integer idCalendario,
                @DefaultValue("0")@QueryParam("max") Integer max) throws ParseException
    {

        Response response;
        javax.persistence.Query query;

    
        Citas misCitas ;
        List<Cita> lista = new ArrayList<Cita>();
        List<Cita> devolver = new ArrayList<Cita>();

        Usuario usuario1;
        Usuario usuario2;
        //comprobamos que existen los usuarios en la BBDD
        usuario1=em.find(Usuario.class, id);
        usuario2=em.find(Usuario.class, idUsuario);

        //si no existen devolvemos un NO CONTENT
        if(usuario1 == null && usuario2==null)
            response = Response.status(Response.Status.NO_CONTENT).build();
        else//existen los usarios
        {
            //si son iguales devolvemos todas las citas de ese usuario
            if( id.intValue() == idUsuario.intValue() )
                query = em.createNamedQuery("Cita.findByIdUsuario");
            else//se devulven los solo los que sean publicos
                query = em.createNamedQuery("Cita.findByIdUsuarioOnlyPublic");

            query.setParameter("idUsuario", new Usuario(idUsuario) );

            //recogemos nuestra lista 
            lista.addAll(query.getResultList());

            //comprobamos los parametros de entrada para mostrar los datos pedidos
            if( (lista.size() > 0) && (!from.equals("-") || !to.equals("-")) )
            {
                //pasamos la fecha a nuestro formato con el que lo tenemos almacenado
                Date dateFrom = null, dateTo = null, fecha;
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                if(!from.equals("-"))
                    dateFrom = df.parse(from);

                if(!to.equals("-"))
                    dateTo = df.parse(to);

                //Si desde es ! de default mostramos la fecha indicada
                if(!from.equals("-") && to.equals("-"))
                {
                    for(Cita ci : lista)
                    {
                        fecha = ci.getFecha();

                        //devolvemos la fecha a partir de la indicada
                        //si solo pongo after muestra despues de la fecha indicada
                        //sería lógico que se muestren todas las citas a partir de la fecha indicada
                        if(fecha.after(dateFrom) || fecha.equals(dateFrom))
                            devolver.add(ci);
                    }
                }
                else if(from.equals("-") && !to.equals("-"))
                {
                     for(Cita ci : lista)
                     {
                        fecha = ci.getFecha();

                        if(fecha.before(dateTo) || fecha.equals(dateTo))
                            devolver.add(ci);
                     }

                }
                else//caso en que los 2 son distinto de default
                {
                      for(Cita ci : lista)
                      {
                        fecha = ci.getFecha();
                        
                        if(fecha.after(dateFrom) && fecha.before(dateTo))
                            devolver.add(ci);
                      }
                }

                lista = devolver;

            }

            //mostrar un maximo número de citas
            if(max.intValue() != 0)
            {
                devolver=new ArrayList<Cita>();
                
                //solo guardamos hasta max
                for(int i=0; i < max; i++)
                    devolver.add(i, lista.get(i));

                lista = devolver;
            }
       
            //para mostrar las citas por idCalendario
            if(idCalendario.intValue() != 0)
            {
                Integer idCalendar;
                
                devolver = new ArrayList<Cita>();

                for(Cita ci : lista)
                {
                    idCalendar = ci.getCalendarioidCalendario().getIdCalendario();

                    //comprobamos si son iguales para guardarlo en la lista a devolver
                    if(idCalendar.intValue() == idCalendario.intValue())
                        devolver.add(ci);
                }

                lista = devolver;
            }

            //hacemos una instancia de colecciones de citas que son las que queremos devolver
            misCitas = new Citas();
            misCitas.setCita(lista);

            response = Response.status(Response.Status.OK).entity(misCitas).build();

        }

        return response;
        
    }
    
    /*Eliminar un calendario*/
    @DELETE
    @Path("{id}/calendarios")
    public Response deleteCalendarioDeUsuario(@PathParam("id") Integer id, @QueryParam("idCalendar")Integer idCalendar)
    {
        Response response;
        
        Calendario calendario = em.find(Calendario.class, idCalendar);
        
        if(calendario != null)
        {
            em.remove(idCalendar);
            response=Response.status(Response.Status.NO_CONTENT).build();
        }else//no existe el calendario
            response=Response.status(Response.Status.NOT_FOUND).build();
        
        return response;
    }
    
    
}
