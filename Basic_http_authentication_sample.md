# Basic HTTP Authentication Sample Application #

Basic HTTP authentication solve following security problems.

  * Get username and password from http request
  * Fetch the applicable method security details
  * Verify if user is authorized to access the API
  * Return valid error codes in case of invalid access

In this tutorial, we show you how to develop a simple  RESTfull web service application with HTTP basic authentication using Cuubez framwork.

Technologies and Tools used in this article:
  1. cuubez 1.1.1
  1. JDK 1.6
  1. Tomcat 6.0
  1. Maven 3.0.3
  1. Intellij IDEA 13.1.1

**Note:**
If you want to know what and how REST works, just search on Google, ton of available resources.

# 1. Directory Structure #
This is the final web project structure of this tutorial.

![http://cuubez.com/downloads/examples/basic_authentication/packageStructure.png](http://cuubez.com/downloads/examples/basic_authentication/packageStructure.png)

# 2. Standard Web Project #
Create a standard Maven web project structure.

```
mvn archetype:generate -DgroupId=com.cuubez -DartifactId=basic_authentication -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false
```


**Note:**
To support IntelliJ IDEA, use Maven command :
```
mvn idea:idea
```

# 3. Project Dependencies #

Cuubez is published in  Maven repository. To develop cuubez REST application , just declares “cuubez-core” in Maven pom.xml.

_File : pom.xml_

```
 <dependency>
   <groupId>com.cuubez</groupId>
   <artifactId>cuubez-core</artifactId>
   <version>1.1.1</version>
 </dependency>

```

# 4. REST Service #

Simple REST service with basic HTTP authentication annotations.

  * @PermitAll: Specifies that all security roles are allowed to invoke the specified method(s)
  * @RolesAllowed: Specifies the list of roles permitted to access method(s)
  * @DenyAll: Specifies that no security roles are allowed to invoke the specified method(s)

```

@Path("/users/{userId}")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static Log log = LogFactory.getLog(UserResource.class);


    @PermitAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response userGet(@HeaderParam(value = "name") String name, @PathParam(value = "userId") String id, @QueryParam(value = "age") Double age) {

        User user = new User(id, age, name);
        return Response.ok().entity(user).build();
    }

    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public void userPost(User user) {
        log.info("POST = [" + user + "]");
    }

    @DenyAll
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void userPut(User user) {
        log.info("PUT = [" + user + "]");

    }

}

```


# 5. Authentication filter #
The security interceptor is build by implementing **com.cuubez.core.Interceptor.RequestInterceptor** interface. This interface has one method which need to implement.

```
@Provider
public class AuthenticationFilter implements RequestInterceptor {

    private final InterceptorResponseContext ACCESS_FORBIDDEN = new InterceptorResponseContext("No access", HttpServletResponse.SC_FORBIDDEN);

    public InterceptorResponseContext process(InterceptorRequestContext interceptorRequestContext) {


        if (interceptorRequestContext.isAnnotationContain(DenyAll.class)) {

            return ACCESS_FORBIDDEN; //Return access denied to user

        } else if (interceptorRequestContext.isAnnotationContain(PermitAll.class)) {

            return null; //Return null to continue request processing

        } else if (interceptorRequestContext.isAnnotationContain(RolesAllowed.class)) {

            //get encoded user name and password
            String encodedUserName = interceptorRequestContext.getHeader("userName");
            String encodedPassword = interceptorRequestContext.getHeader("password");

            //decode user name and password
            String decodedUserName = new String(Base64.decodeBase64(encodedUserName.getBytes()));
            String decodedPassword = new String(Base64.decodeBase64(encodedPassword.getBytes()));

            //get allowed user roles
            String[] allowedRoles = ((RolesAllowed) interceptorRequestContext.getAnnotation(RolesAllowed.class)).value();

            //Access userAccessor to retrieve user details(UserAccessor is not providing by framework, developer need to implement it according their requirement)
            UserAccessor userAccessor = new UserAccessor();
            String role = userAccessor.getUserRole(decodedUserName, decodedPassword);

            if(isAllow(allowedRoles, role)) {
                return null;
            } else {
                return ACCESS_FORBIDDEN;
            }

        }

        return null;

    }

    private boolean isAllow(final String[] allowedRoles, final String userRole) {

        for (String allRole : allowedRoles) {

            if (allRole.equals(userRole)) {
                return true;
            }
        }

        return false;
    }
}
```

This interceptor mechanism provide full flexibility to developer.

# 6. web.xml #
The **ContextLoaderListner** context listener has to be deployed in order to create the registry for cuubez ,while the **ServiceInitiator** servlet is used so that incoming requests are correctly routed to the appropriate services. We have configured the specific servlet, named **“cuubez”**, to intercept requests under the **“/rest/”** path.

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

# 7. Demo #

In this example, web request from **“projectURL/rest/users/{userId}”** will match to **“UserResource“**, via **@Path("/users/{userId}")**.

> ### 1. GET request ###
(GET resource annotated by **@PermitAll** annotation. Specifies that all security roles are allowed to invoke the specified method(s))

![http://cuubez.com/downloads/examples/basic_authentication/get.png](http://cuubez.com/downloads/examples/basic_authentication/get.png)

> ### 2. POST request ###
(POST request annotated by **@RolesAllowed** annotation. **ADMIN** role permitted to access method(s))

  * Forbidden request (Wrong encoded(Base64) user name and password passed as a header variables)
![http://cuubez.com/downloads/examples/basic_authentication/post_unsuccess.png](http://cuubez.com/downloads/examples/basic_authentication/post_unsuccess.png)

  * Successful request (Correct encoded(Base64) user name and password passed as a header variables)
![http://cuubez.com/downloads/examples/basic_authentication/post_success.png](http://cuubez.com/downloads/examples/basic_authentication/post_success.png)

> ### 3. PUT request ###
(PUT resource annotated by **@DenyAll** annotation. Specifies that no security roles are allowed to invoke the specified method(s))

![http://cuubez.com/downloads/examples/basic_authentication/put.png](http://cuubez.com/downloads/examples/basic_authentication/put.png)

# 8. Download #

### Download Basic Authentication Sample Application - [Basic-Authentication.zip](http://cuubez.com/downloads/examples/basic_authentication/basic-authentication.zip) ###