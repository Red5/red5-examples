# Spring MVC Example

To use the Spring MVC in Red5, the Spring MVC library must be placed in the `red5/lib` directory alongside the other Spring Framework jars.

## Configuration

In this example app, the name of __dispatcher__ is used for the servlet name and its associated context file. To start, the servlet context file (dispatcher-servlet.xml) must be created:
```xml
<beans xmlns="http://www.springframework.org/schema/beans" 
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context 
        http://www.springframework.org/schema/context/spring-context.xsd">
    <context:component-scan base-package="org.red5.examples.springmvc.controller" />
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix">
            <value>/WEB-INF/pages/</value>
        </property>
        <property name="suffix">
            <value>.jsp</value>
        </property>
    </bean>
</beans>
``` 

The context file is separate from the Red5 application context xml file (red5-web.xml), but you must add it as an import as shown here:
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:lang="http://www.springframework.org/schema/lang"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/lang http://www.springframework.org/schema/lang/spring-lang.xsd">
    <bean id="placeholderConfig"
        class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="location" value="/WEB-INF/red5-web.properties" />
    </bean>
    <bean id="web.context" class="org.red5.server.Context" autowire="byType" />
    <bean id="web.scope" class="org.red5.server.scope.WebScope">
        <property name="server" ref="red5.server" />
        <property name="parent" ref="global.scope" />
        <property name="context" ref="web.context" />
        <property name="handler" ref="web.handler" />
        <property name="contextPath" value="${webapp.contextPath}" />
        <property name="virtualHosts" value="${webapp.virtualHosts}" />
    </bean>
    <!-- Red5 application -->
    <bean id="web.handler" class="org.red5.examples.springmvc.Application" />
    <!--
    Import your MVC servlet context xml and ensure the name matches the referenced servlet in your web.xml file.
    In this example, the name used is "dispatcher".
     -->
    <import resource="dispatcher-servlet.xml"/>
</beans>
```

Lastly, the web application xml file (web.xml), should be constructed as follows: 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
    version="3.0">
    <display-name>springmvc</display-name>
    <context-param>
        <param-name>webAppRootKey</param-name>
        <param-value>/springmvc</param-value>
    </context-param>
    <listener>
        <listener-class>org.red5.logging.ContextLoggingListener</listener-class>
    </listener>
    <filter>
        <filter-name>LoggerContextFilter</filter-name>
        <filter-class>org.red5.logging.LoggerContextFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>LoggerContextFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <!--
    The MVC dispatcher servlet-name must match the context xml file.
    In this example, the name used is "dispatcher", so the context file is named "dispatcher-servlet.xml".
     -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```

Ensure that your web.xml file does __NOT__ contain either of these two nodes:
```xml
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/mvc-dispatcher-servlet.xml</param-value>
    </context-param>
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
```
The _contextConfigLocation_ will override the Red5 Tomcat loader if its specified and that will break the loading of the Red5 application. In addition,
the Spring web context loader listener will also cause problems in the Red5 application load.


## Creating your own App

Create the pom and initial webapp files
```
mvn archetype:generate -DgroupId=org.red5.example -DartifactId=springmvc -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false
```

Creates the Eclipse files for a WAR project
```
mvn eclipse:eclipse -Dwtpversion=2.0 
```

Several changes must be made to convert what the architype creates into a Red5 application; the steps will be documented here later, but for now
please compare the configuration of this example application and use it as a guide.


### Additional Spring MVC Examples
[Example app](http://www.mkyong.com/maven/how-to-create-a-web-application-project-with-maven/)