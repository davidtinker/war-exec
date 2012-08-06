grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // Grails/Ivy doesn't like the org.eclipse.jetty.orbit stuff
        // https://jira.codehaus.org/browse/JETTY-1493
        // The exclude gets rid of the servlet api jar so I had to put the one from Tomcat in lib
        def jettyVersion = "8.1.4.v20120524"
        runtime(
                "org.eclipse.jetty.aggregate:jetty-server:${jettyVersion}",
                "org.eclipse.jetty.aggregate:jetty-webapp:${jettyVersion}",
                "org.eclipse.jetty.aggregate:jetty-servlet:${jettyVersion}",
                "org.eclipse.jetty.aggregate:jetty-plus:${jettyVersion}",
                "org.eclipse.jetty.aggregate:jetty-websocket:${jettyVersion}"
        ) {
            excludes group: 'org.eclipse.jetty.orbit'
            excludes 'commons-el', 'ant', 'sl4j-api', 'sl4j-simple', 'jcl104-over-slf4j', 'xmlParserAPIs'
            excludes 'mail', 'commons-lang'
        }
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:2.0.4",
              ":rest-client-builder:1.0.2") {
            export = false
        }
    }
}
