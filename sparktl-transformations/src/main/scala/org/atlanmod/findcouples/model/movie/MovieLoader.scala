package org.atlanmod.findcouples.model.movie

import java.io.File

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.resource.{Resource, ResourceSet}
import org.eclipse.emf.ecore.xmi.impl.{EcoreResourceFactoryImpl, XMIResourceFactoryImpl}

object MovieLoader{

    private var resSet : ResourceSet = null

    private def instantiateRS():Unit =  {
        if (resSet != null) return
        val reg = Resource.Factory.Registry.INSTANCE
        val m = reg.getExtensionToFactoryMap
        m.put("xmi", new XMIResourceFactoryImpl)
        m.put("ecore", new EcoreResourceFactoryImpl)
        resSet = new ResourceSetImpl
    }

    def load(xmi_file: String, ecore_file: String) : MovieModel = {
        instantiateRS()

        val mm_absolutePath = new File(ecore_file).getAbsolutePath
        val mm_uri = URI.createURI(mm_absolutePath)
        val resource_ecore: Resource = resSet.getResource(mm_uri, true)
        val pack : EPackage = resource_ecore.getContents.get(0).asInstanceOf[EPackage]
        val packageRegistry = resSet.getPackageRegistry()
        packageRegistry.put(pack.getNsURI, pack)

        val xmi_absolutePath = new File(xmi_file).getAbsolutePath
        val xmi_uri = URI.createURI(xmi_absolutePath)
        val resource_xmi : Resource = resSet.getResource(xmi_uri, true)

        val res =resource_xmi.getContents.get(0)
//        print(res)

        val elements: List[MovieElement] = List()
        val links: List[MovieLink] = List()
        new MovieModel(elements, links)
    }

}