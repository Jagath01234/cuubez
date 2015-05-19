# Embedded jetty with Cuubez #

In this tutorial, we show you how to develop a simple RESTfull web service application with embedded jetty server using cuubez framwork.

Technologies and Tools used in this article:

  1. cuubez 1.1.1
  1. JDK 1.7
  1. Maven 3.0.3
  1. Intellij IDEA 13.1.1

**Note:** If you want to know what and how REST works, just search on Google, ton of available resources.


## 1. Directory Structure ##
This is the final web project structure of this tutorial.

![http://www.cuubez.com/downloads/examples/embedded_jetty/jetty-package.png](http://www.cuubez.com/downloads/examples/embedded_jetty/jetty-package.png)

## 2. Standard Java Project ##
Create a standard Maven java project structure.

```
mvn archetype:generate -DgroupId=com.cuubez -DartifactId=cuubez-jetty -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

**Note:** To support IntelliJ IDEA, use Maven command :
```
mvn idea:idea
```

## 3. Project Dependencies ##

Following maven dependencies should add to the pom.xml file.

_File : pom.xml_

```
<dependency>
  <groupId>com.cuubez</groupId>
  <artifactId>cuubez-core</artifactId>
  <version>1.1.1</version>
</dependency>

<dependency>
  <groupId>org.eclipse.jetty</groupId>
  <artifactId>jetty-servlet</artifactId>
  <version>8.0.4.v20111024</version>
</dependency>
```

## 4. REST Service ##

```

@Path("/users/{userId}")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static Log log = LogFactory.getLog(UserResource.class);


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response userGet(@PathParam(value = "userId") String id, @QueryParam(value = "name") String name, @QueryParam(value = "age") int age) {

        User user = new User(id, age, name);
        return Response.ok().entity(user).build();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public void userPost(User user) {
        log.info("POST = [" + user + "]");
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void userPut(User user) {
        log.info("PUT = [" + user + "]");

    }

}
```

## 5. Embedded Jetty Implementation ##

```

public class JettyServer {

    public static void main(String args[]) {

        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);          
        server.setHandler(handler);
        handler.setContextPath("/");
        handler.setResourceBase(".");
        handler.addEventListener(new BootstrapContextListener());  //cuubez bootstrap context listner
        handler.addServlet(HttpServletDispatcher.class, "/rest/*"); //servlet filter


        try {

            server.start();
            server.join();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

```

## 6. Demo ##

In this example, web request from projectURL/rest/users/id-1003 will match to UserResource, via @Path("/users/{userId}"). **{userId}** will match to parameter annotated with @PathParam and **age** and **name** will match to parameters annotated with @QueryParam.

**URL : http://localhost:8080/rest/users/id-1003?name=jhone&age=30**

![http://www.cuubez.com/downloads/examples/embedded_jetty/jetty-demo.png](http://www.cuubez.com/downloads/examples/embedded_jetty/jetty-demo.png)

### Download this example - [cuubez-jetty.zip ](http://www.cuubez.com/downloads/examples/embedded_jetty/cuubez-jetty.zip) ###