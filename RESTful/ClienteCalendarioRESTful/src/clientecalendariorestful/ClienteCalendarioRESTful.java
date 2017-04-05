/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientecalendariorestful;

import entidades.*;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 *
 * @author Angelica
 */
public class ClienteCalendarioRESTful {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // TODO code application logic here
        UsuarioFacadeREST_JerseyClient clienteUsuario = new UsuarioFacadeREST_JerseyClient();
        CalendarioFacadeREST_JerseyClient clienteCalendario = new CalendarioFacadeREST_JerseyClient();
        Response response;
        Usuario usuario=new Usuario();
        Calendario calendario;
        Cita cita;
        Integer id;
        Citas citas;
        Usuarios usuarios;
        
        id=new Integer(130);
        usuario.setNombre("Angelica");
        usuario.setApellido1("Guaman");
        usuario.setApellido2("Albarracin");
        usuario.setIdUsuario(id);
        
        response = clienteUsuario.crear_XML(usuario, "130");
        
        System.out.println("El usuario: "+response.getStatusInfo());
        //crear calendario
        id=new Integer(20);

        calendario = new Calendario();
        
        calendario.setIdCalendario(id);
        calendario.setNombreCalendario("Practica SOS");
        calendario.setVisibilidad(1);
        calendario.setUsuarioidUsuario(usuario);

        response=clienteUsuario.crearCalendario_XML(calendario, "130");
        System.out.println("El calendario: "+response.getStatusInfo());
        //insertar una cita
        
        id=new Integer(390);
        cita = new Cita();
        
        cita.setIdCita(id);
        cita.setCalendarioidCalendario(calendario);
        cita.setNombreCita("Examen SOS");
        
        response=clienteCalendario.insertarUnaCita_XML(cita, "20");
        
         
        System.out.println("Cita insertada: "+response.getStatusInfo());
        
        //OBTENer todas las citas de un calendario
        //260 es el id de un calendario
        citas=clienteCalendario.obtenerCitas_XML(Citas.class, "260");
        System.out.println("Listado de citas ");
        
        for(Cita c: citas.getCita())
            System.out.println("idCita: "+c.getIdCita()+" Nombre: "+c.getNombreCita());
        
        //obtenemos lista de usuarios
      /*  System.out.println("Listado de usuario ");
        usuarios=clienteUsuario.findAll_XML(Usuarios.class);
        
        for(Usuario usu: usuarios.getU())
            System.out.println("idUsuario: "+usu.getIdUsuario());*/
        
        //obtenemos todas las citas de un usuario
        //101 es el id de un usuario
        citas=clienteUsuario.obtenerCitasDeUnUsuario_XML(Citas.class, "101", "-", "1", "101", "-", "260");
        
        System.out.println("Citas de un Usuario");
        
        for(Cita c: citas.getCita())
            System.out.println("idCita: "+c.getIdCita()+" Nombre: "+c.getNombreCita());
        
    }

    static class UsuarioFacadeREST_JerseyClient {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8080/CalendarioRESTful/apiCalendario";

        public UsuarioFacadeREST_JerseyClient() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("entidades.usuario");
        }

        public Response deleteCalendarioDeUsuario(String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/calendarios", new Object[]{id})).request().delete(Response.class);
        }

        public <T> T encontrarCalendarioUsuario_XML(Class<T> responseType, String id, String idCalendario) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/calendarios/{1}", new Object[]{id, idCalendario}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T encontrarCalendarioUsuario_JSON(Class<T> responseType, String id, String idCalendario) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/calendarios/{1}", new Object[]{id, idCalendario}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public <T> T obtenerCitasDeUnUsuario_XML(Class<T> responseType, String id, String to, String max, String idUsuario, String from, String idCalendario) throws ClientErrorException {
            WebTarget resource = webTarget;
            if (to != null) {
                resource = resource.queryParam("to", to);
            }
            if (max != null) {
                resource = resource.queryParam("max", max);
            }
            if (idUsuario != null) {
                resource = resource.queryParam("idUsuario", idUsuario);
            }
            if (from != null) {
                resource = resource.queryParam("from", from);
            }
            if (idCalendario != null) {
                resource = resource.queryParam("idCalendario", idCalendario);
            }
            resource = resource.path(java.text.MessageFormat.format("{0}/citas", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T obtenerCitasDeUnUsuario_JSON(Class<T> responseType, String id, String to, String max, String idUsuario, String from, String idCalendario) throws ClientErrorException {
            WebTarget resource = webTarget;
            if (to != null) {
                resource = resource.queryParam("to", to);
            }
            if (max != null) {
                resource = resource.queryParam("max", max);
            }
            if (idUsuario != null) {
                resource = resource.queryParam("idUsuario", idUsuario);
            }
            if (from != null) {
                resource = resource.queryParam("from", from);
            }
            if (idCalendario != null) {
                resource = resource.queryParam("idCalendario", idCalendario);
            }
            resource = resource.path(java.text.MessageFormat.format("{0}/citas", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public Response remove(String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request().delete(Response.class);
        }

        public String countREST() throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path("count");
            return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
        }

        public <T> T findAll_XML(Class<T> responseType) throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T findAll_JSON(Class<T> responseType) throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public Response crearCalendario_XML(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/calendarios", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_XML).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
        }

        public Response crearCalendario_JSON(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/calendarios", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
        }

        public <T> T buscarCalendariosDeUnUsuario_XML(Class<T> responseType, String id, String idUsuario) throws ClientErrorException {
            WebTarget resource = webTarget;
            if (idUsuario != null) {
                resource = resource.queryParam("idUsuario", idUsuario);
            }
            resource = resource.path(java.text.MessageFormat.format("{0}/calendarios", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T buscarCalendariosDeUnUsuario_JSON(Class<T> responseType, String id, String idUsuario) throws ClientErrorException {
            WebTarget resource = webTarget;
            if (idUsuario != null) {
                resource = resource.queryParam("idUsuario", idUsuario);
            }
            resource = resource.path(java.text.MessageFormat.format("{0}/calendarios", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public Response crear_XML(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_XML).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
        }

        public Response crear_JSON(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
        }

        public Response actualizarCalendarioUsuario(String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/calendarios", new Object[]{id})).request().put(null, Response.class);
        }

        public <T> T findRange_XML(Class<T> responseType, String from, String to) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{from, to}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T findRange_JSON(Class<T> responseType, String from, String to) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{from, to}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public <T> T find_XML(Class<T> responseType, String id) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T find_JSON(Class<T> responseType, String id) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public Response modificar_XML(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_XML).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
        }

        public Response modificar_JSON(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
        }

        public void close() {
            client.close();
        }
    }

    static class CalendarioFacadeREST_JerseyClient {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8080/CalendarioRESTful/apiCalendario";

        public CalendarioFacadeREST_JerseyClient() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("entidades.calendario");
        }

        public Response insertarUnaCita_XML(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/citas", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_XML).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
        }

        public Response insertarUnaCita_JSON(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/citas", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
        }

        public void remove(String id) throws ClientErrorException {
            webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request().delete();
        }

        public String countREST() throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path("count");
            return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
        }

        public <T> T findAll_XML(Class<T> responseType) throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T findAll_JSON(Class<T> responseType) throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public Response borrarCita(String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/citas", new Object[]{id})).request().delete(Response.class);
        }

        public void edit_XML(Object requestEntity, String id) throws ClientErrorException {
            webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_XML).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML));
        }

        public void edit_JSON(Object requestEntity, String id) throws ClientErrorException {
            webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
        }

        public Response crear_XML(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
        }

        public Response crear_JSON(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
        }

        public <T> T obtenerCitas_XML(Class<T> responseType, String id) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/citas", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T obtenerCitas_JSON(Class<T> responseType, String id) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/citas", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public <T> T findRange_XML(Class<T> responseType, String from, String to) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{from, to}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T findRange_JSON(Class<T> responseType, String from, String to) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{from, to}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public Response modificar_XML(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
        }

        public Response modificar_JSON(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
        }

        public Response actualizarUnaCita_XML(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/citas", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_XML).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
        }

        public Response actualizarUnaCita_JSON(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}/citas", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
        }

        public <T> T find_XML(Class<T> responseType, String id) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T find_JSON(Class<T> responseType, String id) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public <T> T obtenerCitaIndicada_XML(Class<T> responseType, String id, String idCita) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/citas/{1}", new Object[]{id, idCita}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
        }

        public <T> T obtenerCitaIndicada_JSON(Class<T> responseType, String id, String idCita) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}/citas/{1}", new Object[]{id, idCita}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
        }

        public void close() {
            client.close();
        }
    }

  
    
}
