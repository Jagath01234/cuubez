# Cuubez 1.0.0 Beta Documentation #

# 1 RESTful web service overview #
REST is an acronym for the Representational State Transfer architectural style for distributed hypermedia systems.This style was developed in parallel with HTTP/1.1 based on HTTP/1.0 design to work best on the web and it specifies uniform interface constraint. If that is applied to a web service, induces desirable properties, such as performance, scalability and modifiability which enables services to work best on the Web. In REST style, data and operations act as resources .These resources are accessed using uniform resource identifiers (URI). In order to work, REST assumes that resources are capable of being represented a pervasive standard grammar.

## Basic REST principles ##
pplication state and functionality are categorized in to resources
esources are addressable using standard URIs that can be used as hypermedia links
All resources use four HTTP verbs.
  * GET
  * POST
  * DELETE
  * PUT
Resources provide information using the MIME type supported by HTTP.
**The protocol is stateless.**  The protocol is cacheable.

# 2 Cuubez|Rest framework overview #
Cuubez|Rest is a project that enables development and consumption of REST style web services. The core server runtime is based on the unique way to the Cuubez. The project also introduces a client runtime which can leverage certain components of the server-side runtime. Cuubez|Rest will deliver component technology that can be easily integrated into a variety of environments. Simplicity is the major strength of the Cuubez|Rest.

It can be divided in to three major sections

  1. cuubez core
  1. cuubez extensions
  1. cuubez client

**Cuubez core** contains all server side RESTful web service core components. Cuubez extensions provide all cuubez framework related extension solutions. cuubez 1.0 beta release dose not have any extension right now but under the 1.0 stable release, spring framework extension scheduled to release. Cuubez client facilitate to execute cuubez exposed RESTful web service easily.

# 3 Sample application - beta 1.0 #
## 3.1 Expose Java method(with return object) as a RESTful web service. ##

### 3.1.1 Server side implementation. ###

#### 3.1.1.1 Required libraries. ####

  * cuubez-core
  * commons-logging
  * javassist
  * xstream
  * servlet

#### 3.1.1.2 web.xml configuration. ####

Add contextLoaderListner to web.xml. (This is mandatory configuration)
```
<listener>
<listener-class>com.cuubez.core.initiator.ContextLoaderListner</listener-class>
</listener>
```
Add Servlet mapping.

This can be configured with any URL patterns.In this example It configured to redirect all request to Cuubeze RESTful web service framework.
```
<servlet-mapping>
<servlet-name>init</servlet-name>
<url-pattern>/*</url-pattern>
</servlet-mapping>

<servlet>
<servlet-name>init</servlet-name>
<servlet-class>com.cuubez.core.initiator.ServiceInitiator</servlet-class>
</servlet>
```

#### 3.1.1.3 Expose Java method as a RESTful web service ####

**@RestService** annotation is used to expose Java method as a RESTful web service.

Name property Name of the RESTful web service (name should be unique for the application .Same service name cannot be used to different services).

Path property Path property specifying the specific location to the service.

**HttpMethod** property This property specify,which HTTP method is used to expose this RESTful web service.

**MediaType** property Which content type is used to communication. Cuubeze 1.0 beta 1 release only support XML as a mediaType.
```
@RestService(name="userDetails", path="/user", httpMethod=HttpMethod.GET, mediaType = MediaType.XML)
public List<User> getUserDetails() {

}
```

### 3.1.2 Client side implementation ###

#### Required libraries ####

  * cuubez-client
  * commons-logging
  * javassist
  * xstream

First need to define service url and mediaType. Cuubez 1.0 beta 1 release only support XML media type. ServiceUrl? contains server address + application name+rest service specifying location(In this example it is /user) + rest service name(It is specifying as a userDetails).

```
String serviceUrl = "http://localhost:8080/Cuubez_web/user/userDetails";
ClientRequest request = new ClientRequest(serviceUrl, MediaType.XML);
```

RESTful web service is exposed via HTTP GET method hence client needs to communicate via HTTP GET method. Cuubez client library provides APIs to communicate via each of these HTTP methods. Each HTTP method has two different API methods, one for the service which has return object or primitive value and one for the services which hasn't return any object or primitives.

**get(Class>)** API method can be used for this example because of the userDetails service return List as a return object.

```
get(List.class)

List<User> userDetails = request.get(List.class);
```

**Note:-** Client side User class and server side User class package should be the same.

## 3.2 Expose Java method(with parameters) as a RESTful web service ##

### 3.2.1 Server side implementation. ###

#### 3.2.1.1 Required libraries ####

  * cuubez-core
  * commons-logging
  * javassist
  * xstream
  * servlet

#### 3.2.1.2 web.xml configuration ####

Add contextLoaderListner (This is mandatory configuration)
```
<listener>
<listener-class>com.cuubez.core.initiator.ContextLoaderListner</listener-class>
</listener>
```

Add Servlet mapping.

This can configured any url patter.In this example it configures to hit all request to Cuubeze RESTful web service framework.
```
<servlet-mapping>
<servlet-name>init</servlet-name>
<url-pattern>/*</url-pattern>
</servlet-mapping>

<servlet>
<servlet-name>init</servlet-name>
<servlet-class>com.cuubez.core.initiator.ServiceInitiator</servlet-class>
</servlet>
```

#### 3.2.1.3 Expose Java method as a RESTful web service ####

**@RestService** annotation is used to expose Java method as a RESTful web service.

**Name property :** Name of the RESTful web service (name should be unique for the application .Same service name cannot be used to different services).

**Path property :** Path property specifying the specific location to the service.

**HttpMethod property :** This property specify,which HTTP method is used to expose this RESTful web service.

**MediaType property :** Which content type is used to communication.

Cuubez 1.0 beta 1 release only support XML as a mediaType.


```
@RestService(name="saveUserDetails", path="/user", httpMethod=HttpMethod.POST, mediaType = MediaType.XML)
public void saveUserDetails(long userId, User userDetails) {

}
```

### 3.2.2 Client side implementation ###

#### Required libraries ####

  * cuubez-client
  * commons-logging
  * javassist
  * xstream

First need to define service url and mediaType. Cuubez 1.0 beta 1 release only support XML media type. ServiceUrl? contains server address + application name+rest service specifying location(In this example it is /user) + rest service name(It is specifying as a saveUserDetails).
```
String serviceUrl = "http://localhost:8080/Cuubez_web/user/saveUserDetails";
ClientRequest request = new ClientRequest(serviceUrl, MediaType.XML);
```

saveUserDetails service need userId(long) and User object as a parameters.

**Note:-**All parameter objects package structure should be the same(Parameter User class package structure should same in the server side and the client side).
```
Long userId = 1001;
User userDetails = new User(“David”, 28);
request.addParameters(userId, userDetails);
```

addParameters API call is used to pass parameters. We need to strictly following the parameter sequence when parsing to the addParameters method and adding parameter value directly to the add parameter method is not allowed.

**Not allow**
```
request.addParameters(1001, userDetails);
```

saveUserDetails service is exposed via HTTP POST method hence client need to communicate via HTTP POST method.Cuubez client library provide APIs to communicate via each of these HTTP methods. Each HTTP method has two different API methods, one for the service which has return object or primitive value and one for the services which hasn't return any object or primitives.

post() API method can use for this example because of the saveUserDetails service not return any object to the outside
```
request.post();
```