# Cuubez 1.1.0 Documentation #

# 1. Introduction #
Cuubez|Rest is a project that enables development and consumption of REST style web services. The core server runtime is based on the unique way to the Cuubez . The project also introduces a client runtime which can leverage certain components of the server-side runtime. Cuubez|Rest will deliver component technology that can be easily integrated into a variety of environments. Simplicity is the major strength of the Cuubez|Rest.


# 2. REST Architecture #
For a detailed understanding of the REST architecture refer to the description by Roy Fielding in his dissertation, The Design of Network-based Software Architectures. In particular, Chapter 5 Representational State Transfer (REST) describes the principles of the architecture.

> Figure 1: REST architecture

![http://www.cuubez.com/images/Drawing1.png](http://www.cuubez.com/images/Drawing1.png)

> Figure 1 demonstrates the design principles and components that comprise a REST web service. Cuubez reflects these design principles in the implementation of web services.

# 3. JAX-RS Compliancy #
Cuubez 1.0 is a partial implementation of the JAX-RS v2.0 specification.
JAX-RS is a Java based API for RESTful Web Services is a Java programming language API that provides support in creating web services according to the Representational State Transfer (REST) architectural style. JAX-RS uses annotations, introduced in Java SE 5, to simplify the development and deployment of web service clients and endpoints.

# 4. Framework Architecture #
TBD

# 5. Cuubez Server #

Cuubez server module is a partial implementation of the JAX-RS v 2.0 . Remaining features will release with future release. Majority of most required features released with server 1.0 release.

## 5.1 Configuration ##

The **ContextLoaderListner** context listener has to be deployed in order to create the registry for **cuubez** ,while the **ServiceInitiator**  servlet is used so that incoming requests are correctly routed to the appropriate services. We have configured the specific servlet, named “cuubez”, to intercept requests under the “/rest/**” path.**

Note that the value does not containing the trailing slash nor the wildcard.

```
<?xml version="1.0" encoding="UTF-8"?>

<web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
    
<display-name>cuubez</display-name>

<listener>
 <listener-class>
   com.cuubez.core.servlet.BootstrapContextListener
 </listener-class>
</listener>


<servlet-mapping>
 <servlet-name>cuubez</servlet-name>
 <url-pattern>/rest</url-pattern>
</servlet-mapping>

<servlet>
 <servlet-name>init</servlet-name>
 <servlet-class>
    com.cuubez.core.servlet.HttpServletDispatcher
 </servlet-class>
</servlet>
    
</web-app>
```

## 5.2 Annotation ##


### @Path ###
@Path annotation specify the URL path on which this method will be invoked.
```
@Path("/users/{userId}")
public class UserDetail {

    @Path("/address")   
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    public Response getUserAddress(@PathParam(value = "userId")String userId) {
    }
}

```


### @GET ###
Annotate your Get request methods with @GET
```
@Path("/users/{userId}")
public class UserDetail {

    @GET
    @Path("/")   
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam(value = "userId")String userId) {
    }
}
```

### @POST ###
Annotate POST request methods with @POST.
```
@Path("/users/")
public class UserDetail {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createUser(User user) {

    }
}
```

### @DELETE ###
Annotate DELETE request methods with @DELETE.
```
@Path("/users")
public class UserDetail {

    @DELETE
    @Path("/{userId}")   
    public void deleteUser(@PathParam(value = "userId")String userId) {

    }
}
 
```

### @PUT ###
Annotate PUT request methods with @PUT.
```
@Path("/users")
public class UserDetail {

    @PUT
    @Path("/{userId}")   
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateUser(@PathParam(value = "userId")String id, User user) {

    }
}
```

### @Consume ###
The @Consumes annotation is used to specify the MIME media types a REST resource can consume.
```
@Path("/users/")
@Consumes(MediaType.APPLICATION_XML)
public class UserDetail {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createUser(User user) {

    }

    @POST
    @Path("/{userId}/address")
    public void addAddress(@PathParam(value = "userId")String userId, Address address) {
    
    }
}
```

The addAddress method defaults to the MIME type of the @Consume annotation at the class level. The createUser method's @Consume annotation overrides the class-level @Consume setting, and specifies that the method can produce JSON rather than XML.More than one media type may be declared in the same @Consumes declaration, for example:

```
    @POST
    @Consumes({MediaType.APPLICATION_JSON,   MediaType.APPLICATION_XML})
    public void createUser(User user) {

    }
 
```

### @Produce ###
@Produces annotation specifies the type of output this method (or web service) will produce.
```
@Path("/users/{userId}")
public class UserDetail {

    @GET
    @Path("/")   
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser() {

    }
}
```

@Produces can be applied at both the class and the method levels and more than one media type may be declared in the same @Produces declaration.



### @PathParam ###
We can bind REST-style URL parameters to method arguments using @PathParam annotation as shown below.
```
    @GET
    @Path("/users/{userId}/address")   
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAddress(@PathParam(value = "userId")String userId) {

    }
```


### @QueryParam ###
Request parameters in query string can be accessed using @QueryParam annotation as shown below.
```
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchUserDetails(@PathParam(value = "name")String name) {

    }
```


### @HeaderParam ###
The @HeaderParam annotation allows you to map a request HTTP header onto your method invocation.
```
@Path("/users/{userId}")
public class UserDetail {

    @GET
    @Path("/")   
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@HeaderParam(value = "userName")String userName, @PathParam(value = "userId")String userId) {

    }
}
```


## 5.3 Resource Matching ##

### Normal URI Matching ###
Imagine that you have a school-like database structure that has members, and members can be students and teachers. You want to make an REST API that will return a list of all members and then specifically the list of students and the list of teachers. Let’s see how you could do that:

```
@Path("/members")
@Produces(MediaType.APPLICATION_JSON)
public class School {

	@GET
	public Response getMember() {

		Member output = new Member("This is the list of our members:");

		// . . . work to retrieve the list of members

		return Response.ok().entity(output).build();

	}

	@GET
	@Path("/teachers")
	public Response getTeachers() {

		Teacher output = new Teacher("This is the list of our teachers:");

		// . . . work to retrieve the list of teachers

		return Response.ok().entity(output).build();

	}

	@GET
	@Path("/students")
	public Response getStudents() {

		Student output = new Student("This is the list of our students:");

		// . . . work to retrieve the list of students

		return Response.ok().entity(output).build();
	}
}

```

When you put on your browser:

```
http://localhost:8080/school-info/rest/members
```

Outputs:

```
{"description":"This is the list of our members:"}
```


```
http://localhost:8080/school-info/rest/members/students
```
Outputs:

```
{"description":"This is the list of our students:"}
```


```
http://localhost:8080/school-info/rest/members/teachers
```
Outputs:
```
{"description":"This is the list of our teachers:"}
```


### Parameterized URI Matching ###

This works similarly to the normal URI matching, except this enables user input and the form of  the URI is not restricted to /students or/members. The user can put any form of URI he wants. Then the method can parse that URI and act accordingly. For example let’s say we want to list all information available  about a member and the client has to provide just a username:


```
@Path("/members")
public class School {

	@GET
	@Path("/{username}")
        @Produces(MediaType.APPLICATION_JSON)
	public Response getMemberInfo( @PathParam("username") String username ) {

		Member output = new Member("This is all the info on : " + username);

		// . . . work to retrieve the info of {username}

		return return Response.ok().entity(output).build();;
	}	
}
 
```

As you can see we define a Path Parameter using /{ parameter\_name } syntax ( you can also just put { parameter\_name } without the /). The Path Parameter can be parsed using @PathParam annotation, on the argument of getMemberInfo method. If parsed correctly, the value of the Path Parameter will be available to the method through the String username variable.

When you put on your browser:


```
http://localhost:8080/school-info/rest/members/james
```

Outputs:

```
{"description":"This is all the info on : james"}
```



```
http://localhost:8080/school-info/rest/members/lisa
```

Outputs:

```
{"description":"This is all the info on : lisa"}
```



### Regular Expression URI Matching ###

Cuubez server 1.0 release is not support to regular expression URI matching.

# 6.Security #

## 6.1 Basic HTTP Authentication ##

Basic HTTP authentication solve following security problems.
  * Get username and password from http request
  * Fetch the applicable method security details
  * Verify if user is authorized to access the API
  * Return valid error codes in case of invalid access

These basic security problems are solving by using set of annotations and Authentication filter implementation.

### Basic HTTP Authentication Annotations ###

#### @PermitAll ####
Specifies that all security roles are allowed to invoke the specified method(s). This logic should be define inside the Authentication filter.

```
    @PermitAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchUserDetails(@PathParam(value = "name")String name) {

    }
```

#### @RolesAllowed ####
Specifies the list of roles permitted to access method(s). This logic should be define inside the Authentication filter.

```
    @RolesAllowed("ADMIN")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchUserDetails(@PathParam(value = "name")String name) {

    }
```

#### @DenyAll ####
Specifies that no security roles are allowed to invoke the specified method(s).  This logic should be define inside the Authentication filter.

```
    @DenyAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchUserDetails(@PathParam(value = "name")String name) {

    }
```

### Implement the Security Interceptor ###

The security interceptor is build by implementing com.cuubez.core.Interceptor.RequestInterceptor interface. This interface has one method which need to implement.

```
public InterceptorResponseContext process(InterceptorRequestContext interceptorRequestContext) {
@Provider
public class AuthenticationFilter implements RequestInterceptor {

}

```

interceptorRequestContext parameter provides access to method which will be invoked as a result of calling the REST API.
request parameter provides access to request headers and parameters passed by client.

```
@Provider
public class AuthenticationFilter implements RequestInterceptor {

}

```

Following code block shows how to implement interceptor logic for the @PermitAll and @DenyAll annotations.

```
private final InterceptorResponseContext ACCESS_FORBIDDEN = new InterceptorResponseContext("No access", HttpServletResponse.SC_FORBIDDEN);

    public InterceptorResponseContext process(InterceptorRequestContext interceptorRequestContext) {


        if (interceptorRequestContext.isAnnotationContain(DenyAll.class)) {

            return ACCESS_FORBIDDEN; //Return access denied to user

        } else if (interceptorRequestContext.isAnnotationContain(PermitAll.class)) {

            return null; //Return null to continue request processing 

        }
  }
```


@RolesAllowed annotation interceptor implementation.

```
if (interceptorRequestContext.isAnnotationContain(RolesAllowed.class)) {

   //get encoded user name and password
   String encodedUserName = interceptorRequestContext.getHeader("userName");
   String encodedPassword = interceptorRequestContext.getHeader("password");

   //decode user name and password
    String decodedUserName = new String(Base64.decodeBase64(encodedUserName.getBytes()));
    String decodedPassword = new String(Base64.decodeBase64(encodedPassword.getBytes()));

   //get allowed user roles
    String[] allowedRoles = ((RolesAllowed) interceptorRequestContext.getAnnotation(RolesAllowed.class)).value();

   //Access userAccessor to retrieve user details(UserAccessor is not provided by framework, developer need to implement it according their requirement)
    UserAccessor userAccessor = new UserAccessor();
    String role = userAccessor.getUserRole(decodedUserName, decodedPassword);

    if(isAllow(allowedRoles, role)) {
      return null;
    } else {
      return ACCESS_FORBIDDEN;
    }

 }
```


# 7.Quick Start #

## 7.1 Installing Cuubez ##

### 7.1.1 Step 1: Download cuubez from the Repository ###
The first step is to download the latest cuubez stable release from:
http://www.cuubez.com/index.php/2014-05-20-11-01-54

**Maven repo**
```
<dependency>
    <groupId>com.cuubez</groupId>
    <artifactId>cuubez-core</artifactId>
    <version>1.1.1</version>
</dependency>
```

### 7.1.2 Step 2: Add the following libraries to your Web application ###

**Note:** No need to add following libraries if you use maven dependency

```
  com.thoughtworks.xstream
  commons-logging
  javax.servlet
  javassist
  javax.ws.rs
  com.google.code.gson
```

### 7.1.3 Step 3: Define listeners and bootstrap classes ###

```
<?xml version="1.0" encoding="UTF-8"?>

<web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
    
<display-name>cuubez</display-name>

<listener>
 <listener-class>
   com.cuubez.core.servlet.BootstrapContextListener
 </listener-class>
</listener>


<servlet-mapping>
 <servlet-name>cuubez</servlet-name>
 <url-pattern>/rest</url-pattern>
</servlet-mapping>

<servlet>
 <servlet-name>init</servlet-name>
 <servlet-class>
    com.cuubez.core.servlet.HttpServletDispatcher
 </servlet-class>
</servlet>
    
</web-app>
```

### 7.1.4 Step 4: Create your first RESTful service ###

In a REST based architecture everything is a resource. A resource is accessed via a common interface based on the HTTP standard methods (e.g., POST, GET, PUT or DELETE). Every resource should support the HTTP common operations. Resources are identified by global ID's (which are typically URIs).
In the first example, we will recall a RESTful Web service which returns a **User** object when a certain path is request with an HTTP GET:

**User object :**

```

public class User {
    
    private String id;
    private String name;
    private int age;
    

    public User(String id, String name, int age) {
        this.name = id;
        this.age = age;
        this.name = name;
    }

    public String getId() {return id;}

    public int getAge() {return age;}

    public String getName() {return name;}

    public void setId(String id) {this.id = id;}

    public void setAge(int age) {this.age = age;}

    public void setName(String name) {this.name = name;}
}
```

**REST service class :**

```

@Path("/users")
public class UserDetail {

    @GET
    @Path("/{userId}")   
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam(value = "userId")String userId) {
      User user = new User(userId, "jhone", 35);
      return Response.ok().entity(user).build();
    }
}

```

The **@Path** annotation at class level is used to specify the base URL of the Web service. Then you can assign a **@Path** annotation at method level to specify the single action you want to invoke.

In this example, if you need to invoke the userDetail service, you have to use the following URL: (we suppose that the web application is named cuubez.war)

```
http://localhost:8080/cuubez/rest/users/id11001
```

would return:

```
{
"id":"id11001",
 "name": "jhone",
 "age": 35
}
```