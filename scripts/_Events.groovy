eventCreateWarStart = { String warName, File stagingDir ->

    def libDir = new File("${stagingDir}/WEB-INF/lib")
    def jarsToUnpack = []

    libDir.eachFileMatch(~/jetty-.*\.jar/) { jarsToUnpack << it }
    libDir.eachFileMatch(~/tomcat-servlet-api-.*\.jar/) { jarsToUnpack << it }

    jarsToUnpack.each {
        println it
        ant.unjar(src: it, dest: "${stagingDir}") {
            ant.patternset() {
                ant.exclude(name: "META-INF/**")
                ant.exclude(name: "jetty-dir.css")
                ant.exclude(name: "jndi.properties")
                ant.exclude(name: "about.html")
            }
        }
        it.delete()
    }

    ant.move(file: "${stagingDir}/WEB-INF/classes/standalone", todir: "${stagingDir}")

    ant.manifest(file: "${stagingDir}/META-INF/MANIFEST.MF", mode:'update') {
        attribute(name: "Main-Class", value: "standalone.Start")
    }

    Map bc = grailsSettings.config.flatten()
    def port = bc.get('grails.server.port.http') ?: "8080"

    Properties p = new Properties()
    p.setProperty("jetty.port", port as String)

    Map cfg = new ConfigSlurper(grailsEnv).parse(new File(baseFile, "grails-app/conf/Config.groovy").text).flatten()
    cfg.each { k, v ->
        if (k instanceof String && k.startsWith("jetty.")) p.setProperty(k as String, v as String)
    }

    def out = new File(stagingDir, "standalone/start.properties").newOutputStream()
    p.store(out, null)
    out.close()
}
