/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servicios;

import entidades.Cita;
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
import javax.ws.rs.core.Response;

/**
 *
 * @author Angelica
 */
@Stateless
@Path("entidades.cita")
public class CitaFacadeREST extends AbstractFacade<Cita> {
    @PersistenceContext(unitName = "CalendarioRESTfulPU")
    private EntityManager em;

    public CitaFacadeREST() {
        super(Cita.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Cita entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Integer id, Cita entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Cita find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Cita> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Cita> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
    
    /*Obtener el número de citas entre fechas*/
    @GET
    @Path("countCitas")
    @Produces("text/plain")
    public Response countCitasDeCalendario(@DefaultValue("-") @QueryParam("from") String from,
                                           @DefaultValue("-") @QueryParam("to")String to)throws ParseException
    {
        Response response=null;
        javax.persistence.Query query;
        List<Cita> citas = new ArrayList<Cita>();
        List<Cita> devolver = new ArrayList<Cita>();
        
        query=em.createNamedQuery("Cita.findAll");
        
        citas.addAll(query.getResultList());
        
        //comprobamos los parametros de entrada para mostrar los datos pedidos
            if( (citas.size() > 0) && (!from.equals("-") || !to.equals("-")) )
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
                    for(Cita ci : citas)
                    {
                        fecha = ci.getFecha();

                        //devolvemos la fecha a partir de la indicada
                        //si solo pongo after muestra despues de la fecha indicada
                        //sería lógico que se muestren todas las citas a partir de la fecha indicada
                        if(fecha.after(dateFrom))
                            devolver.add(ci);
                    }
                }
                else if(from.equals("-") && !to.equals("-")) //caso en el que from tenga alguna fecha
                {
                     for(Cita ci : citas)
                     {
                        fecha = ci.getFecha();

                        if(fecha.before(dateTo))
                            devolver.add(ci);
                     }
                }
                else//caso en que los 2 son distinto de default
                {
                      for(Cita ci : citas)
                      {
                        fecha = ci.getFecha();

                        if(fecha.after(dateFrom) && fecha.before(dateTo))
                            devolver.add(ci);
                      }
                }
                citas = devolver;
            }
        
            response = Response.status(Response.Status.OK).entity(citas.size()).build();
            
        return response;
    }
    
}
