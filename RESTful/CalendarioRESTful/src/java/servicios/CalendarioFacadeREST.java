/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servicios;

import entidades.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
@Path("entidades.calendario")
public class CalendarioFacadeREST extends AbstractFacade<Calendario> {
    @PersistenceContext(unitName = "CalendarioRESTfulPU")
    
    private EntityManager em;

    //mis variables
    private @Context UriInfo uriInfo;
    
    public CalendarioFacadeREST() {
        super(Calendario.class);
    }

    /*@POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Calendario entity) {
        super.create(entity);
    }
*/
    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Integer id, Calendario entity) {
        super.edit(entity);
    }
   
    
    @POST
    @Consumes({"application/xml", "application/json"})
    public Response crear(Calendario entity)
    {
        super.create(entity);
        return Response.created(uriInfo.getAbsolutePath().resolve(uriInfo.getPath()+"/"+entity.getIdCalendario())).build();
    
        
    }
    
    @PUT
    @Consumes({"application/xml", "application/json"})
    public Response modificar(Calendario entity)
    {
        super.edit(entity);
        return Response.status(Response.Status.NO_CONTENT).header("Location", this.uriInfo.getAbsolutePath()).build();
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Calendario find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Calendario> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Calendario> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    //FUNCIONA
    @GET
    @Path("{id}/citas")
    @Produces({"application/xml", "application/json"})
    public Response obtenerCitas(@PathParam("id") Integer id)
    {
        Response response;
        javax.persistence.Query query;
        //lo que devolvemos
        Citas citas = new Citas();
        
        Calendario calendario = em.find(Calendario.class, id);
        
        //existe el calendario
        if(calendario != null)
        {
            query=em.createNamedQuery("Cita.findByIdCalendario").setParameter("idCalendario", new Calendario(id));
            citas.setCita(query.getResultList());
            
            response= Response.status(Response.Status.ACCEPTED).entity(citas).build();
        }else
            response = Response.status(Response.Status.NOT_FOUND).build();
        
        return response;
    }
    
    //FUNCIONA
    @GET
    @Path("{id}/citas/{idCita}")
    @Produces({"application/xml", "application/json"})
    public Response obtenerCitaIndicada(@PathParam("id") Integer id,
            @PathParam("idCita") Integer idCita)
    {
        Response response;
        javax.persistence.Query query;
        //lo que devolvemos
        List<Cita> citas = new ArrayList();
       
        //obtenemos las citas
        query=em.createNamedQuery("Cita.findByIdCalendario");
        query.setParameter("idCalendario", new Calendario(id));
        
        citas.addAll(query.getResultList());
        Cita cita=em.find(Cita.class, idCita);
        Calendario calendario=em.find(Calendario.class, id);
        
        //comprobamos si el identificador de la cita y el calendario existen en la BBDD
        if(cita!= null && calendario!=null && citas.contains(cita))
            response=Response.status(Response.Status.OK).entity(cita).build();
        else
            response=Response.status(Response.Status.NOT_FOUND).build();
        
        return response;
    }
    
    //INSERTA UNA CITA....
    
    //FUNCIONA
    @POST
    @Path("{id}/citas")
    @Consumes({"application/xml", "application/json"})
    public Response insertarUnaCita(@PathParam("id") Integer id, Cita nueva)
    {
        Response response;
        javax.persistence.Query query;
        Citas citas = new Citas();//coleccion de citas
        
        //buscamos el calendario id 
        Calendario calendario=em.find(Calendario.class, id);
        
        if(calendario != null)
        {
            em.persist(nueva);
            URI uri= URI.create(uriInfo.getAbsolutePath()+"/"+nueva.getIdCita().intValue());
            //se devuelve el location porque es un POST el cliente no sabe su URI
            //el sistema se lo añade
            response = Response.status(Response.Status.CREATED).location(uri).build();
        }else
            response = Response.status(Response.Status.NOT_FOUND).build();
        
        return response;
    }
    
    //FUNCIONA
    @PUT
    @Path("{id}/citas")
    @Consumes({"application/xml", "application/json"})
    public Response actualizarUnaCita(@PathParam("id") Integer id, Cita actualiza)
    {
        Response response;
        
        //buscamos el calendario
        Calendario calendario=em.find(Calendario.class, id);
        
        //buscamos cita
        Cita cita=em.find(Cita.class, actualiza.getIdCita() );
        
        //si existen podemos actualizar y agregar al calendario
        if(calendario!=null && cita!=null)
        {
            //modificamos los datos con la cita que nos pasan
            cita.setIdCita(actualiza.getIdCita());
            cita.setNombreCita(actualiza.getNombreCita());
            cita.setFecha(actualiza.getFecha());
            cita.setCalendarioidCalendario(new Calendario (actualiza.getCalendarioidCalendario().getIdCalendario()));
            
            //lo añadimos 
            URI uri=URI.create(uriInfo.getAbsolutePath()+"/"+actualiza.getIdCita().intValue());
            
            response=Response.status(Response.Status.CREATED).location(uri).build();
            
        }else
            response=Response.status(Response.Status.CONFLICT).build();
        
        return response;
    }
    
    @DELETE
    @Path("{id}/citas")
    public Response borrarCita(@PathParam("id")Integer id,  @QueryParam("idCita") Integer idCita)
    {
        Response response;
        
        Cita cita = em.find(Cita.class, idCita);
        Calendario calendario = em.find(Calendario.class, id);
        
        //comprobamos si el id del calendario y el id de la cita existe en la BBDD 
        if(cita!=null && calendario!=null)
        {
            //si existe la borramos
            em.remove(cita);
            response=Response.status(Response.Status.OK).build();
        }else
            response=Response.status(Response.Status.NOT_FOUND).build();
        
        return response;
    }
    
    
   
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    
   
    
}
