Cuubez|Rest is a project that enables development and consumption of REST style web services. The core server runtime is based on the unique way to the Cuubez . The project also introduces a client runtime which can leverage certain components of the server-side runtime. Cuubez|Rest will deliver component technology that can be easily integrated into a variety of environments. Simplicity is the major strength of the Cuubez|Rest.



## Quick Start ##

In this tutorial, we show you how to develop a simple REST web application with Cuubez.

### Technologies and Tools used in this article: ###

  * Cuubez 1.1.1
  * JDK 1.6
  * Tomcat 6.0
  * Maven 3.0.3
  * Intellij IDEA 13.1.1


Note If you want to know what and how REST works, just search on Google, ton of available resources.

## 1. Directory Structure ##
This is the final web project structure of this tutorial.

![http://cuubez.com/downloads/examples/employee_example/Screenshot1.png](http://cuubez.com/downloads/examples/employee_example/Screenshot1.png)


## 2. Standard Web Project ##
Create a standard Maven web project structure.
```
mvn archetype:generate -DgroupId=com.cuubez -DartifactId=Employee-example
        -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false
```

**Note :** To support IDEA, use Maven command :

```
mvn idea:idea
```


## 3. Project Dependencies ##
The recommended way to get started using cuubez-framework in your project is with a dependency management system – the snippet below can be copied and pasted into your build(pom.xml). Need help? See our getting started guides on building with Maven.

_File : pom.xml_

```
<dependencies>
   <dependency>
     <groupId>com.cuubez</groupId>
     <artifactId>cuubez-core</artifactId>
     <version>1.1.1</version>
   </dependency>
</dependencies>
```

## 4. REST Service ##
Simple REST service with Cuubez

```
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import com.cuubez.example.entity.Employee;
 
 
@Path("/employees")
public class EmployeeResource {
 
 
    @Path("/{empId}")
    @GET
    public Response getEmployee(@PathParam("empId") final String empId) {
 
        Employee employee = new Employee(empId, "jhon powel", "marketing", "No 321, Colombo 4", "+94775993720");
        return Response.ok().entity(employee).build();
    }
}
 
```

## 5. web.xml ##
The ContextLoaderListner context listener has to be deployed in order to create the registry for cuubez ,while the ServiceInitiator servlet is used so that incoming requests are correctly routed to the appropriate services. We have configured the specific servlet, named cuubez, to intercept requests under the /rest/ path.

_File : web.xml_

```
<web-app>
  <display-name>Employee Example</display-name>
 
    <listener>
        <listener-class>com.cuubez.core.servlet.BootstrapContextListener</listener-class>
    </listener>
 
    <servlet-mapping>
     <servlet-name>init</servlet-name>
     <url-pattern>/rest/*</url-pattern>
    </servlet-mapping>
 
    <servlet>
     <servlet-name>init</servlet-name>
     <servlet-class>com.cuubez.core.servlet.HttpServletDispatcher</servlet-class>
    </servlet>
 
</web-app>
 
```


## 6. Demo ##
In this example, web request from projectURL/rest/employees will match to EmployeeResource, via @Path("/employees"). And the {empId}from projectURL/rest/employees/{empId} will match to parameter annotated with @PathParam.

**URL : http://localhost:8080/employee-example-1.0.0/rest/employees/eId-0001**

![http://cuubez.com/downloads/examples/employee_example/Screenshot2.png](http://cuubez.com/downloads/examples/employee_example/Screenshot2.png)


