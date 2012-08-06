War-Exec Plugin
===============

Grails plugin to make Grails war files executable (java -jar myapp.war) by embedding Jetty. Jetty can be configured
using properties in Config.groovy.

Using
-----

Install the plugin:

    grails install-plugin war-exec

Add a dependency to BuildConfig.groovy:

    plugins {
        runtime ":war-exec:1.0.0"
        ...
    }

Now you can do:

    $ grails dev war
    $ java -jar target/com.brandseye.myapp.war

Your application will be running with your development environment configuration. If you just do "grails war" the
war file is configured for your production environment. Note that the war file can still be deployed to Tomcat et
al normally if needed.

Jetty listens on the grails.server.port.http from BuildConfig.groovy (8080 by default). You can set this and
other properties in Config.groovy (and have different values for different environments):

    jetty.host=127.0.0.1
    jetty.port=8080
    jetty.context.path=... // defaults "/" for production wars otherwise to what grails run-app uses
    jetty.max.threads=254
    jetty.min.threads=8
    jetty.tmp=...    // where the war file is unpacked, defaults to ${user.home}/.jetty-tmp

These be overridden using system properties:

    java -Djetty.host=0.0.0.0 -jar myapp.jar


How It Works
------------

The Jetty jars are pulled in via a runtime dependency in BuildConfig.groovy.

A ''eventCreateWarStart'' event handler in scripts/_Events.groovy unpacks jetty-*.jar and servlet-api-*.jar into the
root of the war file and deletes them from from WEB-INF/lib. It also sets the Main-Class in the MANIFEST.MF file to
standalone.Start (src/java/standalone/Start.java) and creates standalone/start.properties containing jetty.*
properties from Config.groovy. The Start class uses these to configure Jetty.


Proxying Your Application Using Apache
--------------------------------------

Make sure mod_proxy and mod_proxy_http are loaded and enabled:

    LoadModule proxy_module modules/mod_proxy.so
    LoadModule proxy_http_module modules/mod_proxy_http.so

Then you can configure the virtual hosts to proxy your application:

    <VirtualHost *:80>
      ServerName myapp.brandseye.com

      ProxyRequests off
      <Proxy *>
      Order deny,allow
      Allow from all
      </Proxy>

      ProxyPass / http://localhost:8080/
      ProxyPreserveHost On
    </VirtualHost>

Note that this works with HTTPS virtual hosts as well.

