package org.atlanmod

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.resource.{Resource, ResourceSet}
import org.eclipse.emf.ecore.xmi.impl.{EcoreResourceFactoryImpl, XMIResourceFactoryImpl}
import org.eclipse.emf.ecore.{EObject, EPackage, EcorePackage}

object EMFTool {

    // Singleton
    private var rs : ResourceSet = null

    private def instantiateRS: Unit = {
        // Instantiate a new Resource Set
        if (rs == null) {
            rs = new ResourceSetImpl
            // Get map
            val reg = Resource.Factory.Registry.INSTANCE
            val m = reg.getExtensionToFactoryMap
            // Put Factories
            m.put("xmi", new XMIResourceFactoryImpl)
            m.put("ecore", new EcoreResourceFactoryImpl)
        }
    }

    def loadXMI(uri: String, packageName: String, pack: EPackage): EObject = {
        // Instantiate the resource set if needed
        instantiateRS
        // Add the package to the registry if not existing yet
        val packageRegistry = rs.getPackageRegistry()
        if (!packageRegistry.containsKey(packageName)) packageRegistry.put(packageName, pack)
        // Load the resource
        val resource = rs.getResource(URI.createURI(uri), true)
        resource.getContents.get(0)
    }

    def loadEcore(uri: String): Resource = {
        // Instantiate the resource set if needed
        instantiateRS
        val ecorePackage: EcorePackage = EcorePackage.eINSTANCE
        // Get the URI of the model file.
        URI.createPlatformPluginURI(uri, false)
        val res = rs.getResource(URI.createURI(uri), true)
        res
    }
}

