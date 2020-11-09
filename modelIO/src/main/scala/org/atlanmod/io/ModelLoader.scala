package org.atlanmod.tl.sequential.io

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.resource.{Resource, ResourceSet}
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl

object ModelLoader {

        private var resSet : ResourceSet = null

        private def instantiateRS(name: String, obj: Object): Unit = {
            val reg = Resource.Factory.Registry.INSTANCE
            val m = reg.getExtensionToFactoryMap
            m.put("xmi", new XMIResourceFactoryImpl)
            resSet = new ResourceSetImpl
            val packageRegistry = resSet.getPackageRegistry()
            packageRegistry.put(name, obj)
        }

        def loadFromUri(uri: String, packageName: String, obj: Object): EObject = {
            if (resSet == null)
                instantiateRS(packageName, obj)
            // Load the resource
            val resource = resSet.getResource(URI.createURI(uri), true)
            resource.getContents.get(0)
        }
}

