package org.atlanmod.model.dynamic;

import org.atlanmod.EMFTool;
import org.atlanmod.tl.model.Model;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scala.Tuple3;
import scala.collection.JavaConverters;

public class DynamicModel implements Model<DynamicElement, DynamicLink> {

//    private DynamicMetamodel metamodel;
    protected List<DynamicElement> elements;
    protected List<DynamicLink> links;

    public DynamicModel(){
        // Instantiate lists
        elements = new ArrayList<>();
        links = new ArrayList<>();
    }

    public DynamicModel(List<DynamicElement> elements, List<DynamicLink> links){
        assert(elements != null && links != null);
        this.elements = elements;
        this.links = links;
    }

    public DynamicModel(scala.collection.immutable.List<DynamicElement> elements, scala.collection.immutable.List<DynamicLink> links){
        assert(elements != null && links != null);
        this.elements = JavaConverters.seqAsJavaList(elements);
        this.links = JavaConverters.seqAsJavaList(links);
    }

    @Override
    public scala.collection.immutable.List<DynamicElement> allModelElements() {
        // convert java.util.List to scala.List
        return JavaConverters.asScalaBuffer(elements).toList();
    }

    @Override
    public scala.collection.immutable.List<DynamicLink> allModelLinks() {
        // convert java.util.List to scala.List
        return JavaConverters.asScalaBuffer(links).toList();
    }

    public void load(String uri){
        Resource resource = EMFTool.loadEcore(uri);
        this.load(resource);
    }

    public void load(Resource resource){
        Map<EObject, DynamicElement> eObj_to_element = new HashMap<>();
        /* Keep links as a Tuple3 with EObject. EObjects will be removed latter thanks to eObj_to_element,
        to instantiate DynamicLinks */
        List<Tuple3<String, EObject, List<EObject>>> futureLinks = new ArrayList<>();

        // List of links: (e, reference.name, Object)
        for (TreeIterator<EObject> it = resource.getAllContents(); it.hasNext(); ) {
            // Get EObject + EClass
            EObject obj = it.next();
            EClass obj_class = obj.eClass();

            String class_name = obj_class.getName();
            DynamicElement e =
                    obj.eResource().getURI() == null ?
                            new DynamicElement(class_name):
                            new DynamicElement(obj.eResource().getURI().toString(), class_name);

            // Adding new e (with attributes) to the list of elements
            Map<String, Object> attributes_of_e = new HashMap<>();
            for(EAttribute attribute : obj_class.getEAllAttributes()){
                attributes_of_e.put(attribute.getName(), obj.eGet(attribute));
            }
            elements.add(new DynamicElement(e, attributes_of_e));

            // Match a EObject to the created DynamicElement
            eObj_to_element.put(obj, e);

            // Create futureLink as a Tuple3: (name, source, target) for references
            for(EReference reference : obj_class.getEAllReferences()){
                Object referred = obj.eGet(reference);
                List<EObject> targeted = new ArrayList<>();
                // In the case of a EReference, the returned 'Object' from eGet() is necessarily: EObject | List<EObject>
                if (reference.isMany())
                    for(Object o: (List<?>) referred)
                        targeted.add((EObject) o);
                else
                    targeted.add((EObject) referred);
                futureLinks.add(new Tuple3<>(reference.getName(), obj, targeted));
            }
        }

        // Create concrete DynamicLinks from futureLinks
        for(Tuple3<String, EObject, List<EObject>> futureLink : futureLinks){
            List<DynamicElement> targets = new ArrayList<>();
            for(EObject a_target : futureLink._3()) {
                targets.add(eObj_to_element.get(a_target));
            }
            links.add(new DynamicLink(futureLink._1(), eObj_to_element.get(futureLink._2()), targets));
        }
    }

    @Override
    public String toString() {
        StringBuilder str_builder = new StringBuilder("");
        str_builder.append("elements (size="+ elements.size() +"):\n------------------------\n");
        for(DynamicElement element : elements)
            str_builder.append(element.toString() + "\n");
        str_builder.append("\n");
        str_builder.append("links (size="+ links.size() +"):\n------------------------\n");
        for(DynamicLink link : links){
            str_builder.append(link.toString(0));
            str_builder.append("\n");
        }
        return str_builder.toString();
    }
}
