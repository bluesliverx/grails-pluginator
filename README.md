This plugin enables a typical Grails application to tie into the additional hooks and configuration available
to Grails plugins.  It does this by using grails-app/conf/ApplicationPlugin.groovy as a plugin definition file,
allowing the following hooks and configuration properties:

Hooks: doWithWebDescriptor, doWithSpring, doWithDynamicMethods, doWithApplicationContext, onChange, and onConfigChange.
Properties: loadAfter, observe, watchedResources, and artefacts.

For instance, with doWithSpring you can adjust your web-xml if you need precise control over web-xml generation.
You can also declare which other plugins the "plugin" should be loaded after with loadAfter.

To use, simply define the grails-app/conf/ApplicationPlugin.groovy file as the following:

```groovy
class ApplicationPlugin {
	// List of resources - "file:./grails-app/jobs/**"
	def watchedResources = []
	// List of plugins to load this "plugin" after
	def loadAfter = []
	// Observe the following plugins - meaning the onChange event will be
	//	called after their onChange event
	def observe = []
	// The artefact handlers to register
	def artefacts = []

	// See Grails plugin documentation for examples on how to use these
    def doWithWebDescriptor = { xml ->
    }

    def doWithSpring = {
    }

    def doWithDynamicMethods = { ctx ->
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def onChange = { event ->
    }

    def onConfigChange = { event ->
    }
}
```