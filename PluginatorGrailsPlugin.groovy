import org.slf4j.Logger
import org.slf4j.LoggerFactory

import grails.util.BuildSettings

class PluginatorGrailsPlugin {
	def version = '0.2'
	def grailsVersion = '1.3 > *'
	def author = 'Sergey Bondarenko'
	def authorEmail = 'enterit@gmail.com'
	def title = 'Pluginator'
	def description = 'This plugin lets you define callbacks and properties in an application that are normally only available in plugins, e.g. doWithWebDescriptor, doWithDynamicMethods, loadAfter, observe, etc.'
	def documentation = 'http://grails.org/plugin/pluginator'
	def license = 'GPL3'
//	def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/???']
	def scm = [url: 'https://github.com/bluesliverx/grails-pluginator']
	def developers = [
		[name: 'Brian Saville', email: 'bksaville@gmail.com'],
		[name: 'Burt Beckwith', email: 'beckwithb@vmware.com']
	]

	private Logger log = LoggerFactory.getLogger('grails.plugin.pluginator.PluginatorGrailsPlugin')
	private applicationPlugin

	def artefacts = []
	def evict = []
	def loadAfter = []
	def loadBefore = []
	def observe = []
	def providedArtefacts = []
	def watchedResources = []

	PluginatorGrailsPlugin() {
		initApplicationPlugin()
		if (!applicationPlugin) return

		for (String name in ['artefacts', 'evict', 'loadAfter', 'loadBefore',
		                     'observe', 'providedArtefacts', 'watchedResources']) {
			if (applicationPlugin.properties[name]) {
				def value = applicationPlugin."$name"
				if (log.isDebugEnabled()) {
					log.debug "Setting '$name' value -> '$value'"
				}
				this."$name" = value
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

	def doWithApplicationContext = { ctx ->
		callApplicationPluginAction 'doWithApplicationContext', delegate, [ctx]
	}

	def onChange = { event ->
		callApplicationPluginAction 'onChange', delegate, [event]
	}

	def onConfigChange = { event ->
		callApplicationPluginAction 'onConfigChange', delegate, [event]
	}

	def onShutdown = { event ->
		callApplicationPluginAction 'onShutdown', delegate, [event]
	}

	private void initApplicationPlugin() {
		if (applicationPlugin) {
			return
		}

		def basedir = System.getProperty(BuildSettings.APP_BASE_DIR)
		def applicationPluginFile = new File(basedir, 'grails-app/conf/ApplicationPlugin.groovy')
		if (applicationPluginFile.exists()) {
			def loader = new GroovyClassLoader(getClass().getClassLoader())
			applicationPlugin = loader.parseClass(applicationPluginFile).newInstance()
		}
	}

	private callApplicationPluginAction(String actionName, delegate, args = []) {
		initApplicationPlugin()
		if (!applicationPlugin) return

		if (!(applicationPlugin.properties[actionName] instanceof Closure)) {
			return
		}

		if (log.isTraceEnabled()) {
			log.trace "Calling '$actionName' with args $args"
		}
		else if (log.isDebugEnabled()) {
			log.debug "Calling '$actionName'"
		}

		Closure action = applicationPlugin."$actionName"
		action.delegate = delegate
		action.call(*args)
	}
}
