import org.codehaus.groovy.grails.commons.GrailsClassUtils

class PluginatorGrailsPlugin {
    // the plugin version
    def version = "0.1.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Sergey Bondarenko"
    def authorEmail = "enterit@gmail.com"
    def title = "This plugin adds ability to hook into application's runtime configuration like if it is a plugin by itself."
    def description = '''\\
Plaginator allows to write the following configuration hooks in application:
doWithWebDescriptor, doWithSpring, doWithDynamicMethods, doWithApplicationContext, onChange, onConfigChange

For instance, with doWithSpring you can adjust your web-xml if you need precise control over web-xml generation.
Just add dependency on this plugin at the last line in your application.properties.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/pluginator"

    def applicationPlugin

    List watchedResources

    PluginatorGrailsPlugin() {
        if (getApplicationPlugin() && getApplicationPlugin().properties['watchedResources']) {
            watchedResources = getApplicationPlugin().watchedResources
        }
    }

    def getApplicationPlugin() {
        if (!applicationPlugin) {
            def basedir = System.getProperty(grails.util.BuildSettings.APP_BASE_DIR)
            def applicationPluginFile = new File("$basedir/grails-app/conf/ApplicationPlugin.groovy")
            if (applicationPluginFile.exists()) {
                def loader = new GroovyClassLoader(getClass().getClassLoader())
                applicationPlugin = loader.parseClass(applicationPluginFile).newInstance()
            }
        }
        return applicationPlugin
    }

    def callApplicationPluginAction(actionName, delegate, args = []) {
        if (getApplicationPlugin()) {
            if (getApplicationPlugin().properties[actionName]) {
                def action = getApplicationPlugin()."$actionName"
                action.setDelegate(delegate);
                action.call(*args)
            }
        }
    }

    def doWithWebDescriptor = { xml ->
        callApplicationPluginAction 'doWithWebDescriptor', delegate, [xml]
    }

    def doWithSpring = {
        callApplicationPluginAction 'doWithSpring', delegate
    }

    def doWithDynamicMethods = { ctx ->
        callApplicationPluginAction 'doWithDynamicMethods', delegate, [ctx]
    }

    def doWithApplicationContext = { applicationContext ->
        callApplicationPluginAction 'doWithApplicationContext', delegate, [applicationContext]
    }

    def onChange = { event ->
        callApplicationPluginAction 'onChange', delegate, [event]
    }

    def onConfigChange = { event ->
        callApplicationPluginAction 'onConfigChange', delegate, [event]
    }

}
