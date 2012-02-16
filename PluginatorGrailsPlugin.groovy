import org.codehaus.groovy.grails.commons.GrailsClassUtils

class PluginatorGrailsPlugin {
    // the plugin version
    def version = "0.2.0-SNAPSHOT"
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
    def title = "Application as a Plugin"
    def description = '''\
This plugin adds the ability to hook into an application's runtime configuration as if it were a plugin,
including all hooks and code that is available in a plugin descriptor file.

Pluginator allows to write the following configuration hooks in an application:
doWithWebDescriptor, doWithSpring, doWithDynamicMethods, doWithApplicationContext, onChange, and onConfigChange.

For instance, with doWithSpring you can adjust your web-xml if you need precise control over web-xml generation.

You can also declare loadAfter, observe, watchedResources, and custom artefacts.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/pluginator"

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "LGPL"
    // Any additional developers beyond the author specified above.
    def developers = [ [ name: "Brian Saville", email: "bksaville@gmail.com" ]]
    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "GitHub", url: "https://github.com/bluesliverx/grails-pluginator/issues" ]
    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/bluesliverx/grails-pluginator" ]

    def applicationPlugin

    List watchedResources
	List loadAfter
	List observe
	List artefacts

    PluginatorGrailsPlugin() {
        if (getApplicationPlugin() && getApplicationPlugin().properties['watchedResources']) {
            watchedResources = getApplicationPlugin().watchedResources
			loadAfter = getApplicationPlugin().loadAfter
			observe = getApplicationPlugin().observe
			artefacts = getApplicationPlugin().artefacts
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
