package org.atlanmod.model

import org.atlanmod.EMFTool
import org.atlanmod.tl.model.Model
import org.eclipse.emf.ecore.resource.Resource

class DynamicModel(elements: List[DynamicElement] = List(), links: List[DynamicLink] = List())
  extends Model[DynamicElement, DynamicLink]{

    override def allModelElements: List[DynamicElement] = elements

    override def allModelLinks: List[DynamicLink] = links

    def load(uri: String) : Unit = {
        this.load(EMFTool.loadEcore(uri))
    }

    def load(resource: Resource) : Unit = {
        // TODO
    }

    override def toString: String = {
        var res = ""
        res += "elements (size=" + elements.size + "):\n------------------------\n"
        res += elements.mkString("\n")
        res += "\n\n"
        res += "links (size=" + links.size + "):\n------------------------\n"
        res += links.mkString("\n")
        res
    }
}

// TODO
//public void load(Resource resource){
//    Map<EObject, DynamicElement> eObj_to_element = new HashMap<>();
//    /* Keep links as a Tuple3 with EObject. EObjects will be removed latter thanks to eObj_to_element,
//    to instantiate DynamicLinks */
//    List<Tuple3<String, EObject, List<EObject>>> futureLinks = new ArrayList<>();
//
//    // List of links: (e, reference.name, Object)
//    for (TreeIterator<EObject> it = resource.getAllContents(); it.hasNext(); ) {
//    // Get EObject + EClass
//    EObject obj = it.next();
//    EClass obj_class = obj.eClass();
//
//    String class_name = obj_class.getName();
//    DynamicElement e =
//    obj.eResource().getURI() == null ?
//    new DynamicElement(class_name):
//    new DynamicElement(obj.eResource().getURI().toString(), class_name);
//
//    // Adding new e (with attributes) to the list of elements
//    Map<String, Object> attributes_of_e = new HashMap<>();
//    for(EAttribute attribute : obj_class.getEAllAttributes()){
//    attributes_of_e.put(attribute.getName(), obj.eGet(attribute));
//}
//    Map<String, Object> references_of_e = new HashMap<>();
//    for(EReference reference: obj_class.getEAllReferences()){
//    references_of_e.put(reference.getName(), obj.eGet(reference));
//}
//    elements.add(new DynamicElement(e, attributes_of_e, references_of_e));
//
//    // Match a EObject to the created DynamicElement
//    eObj_to_element.put(obj, e);
//
//    // Create futureLink as a Tuple3: (name, source, target) for references
//    for(EReference reference : obj_class.getEAllReferences()){
//    Object referred = obj.eGet(reference);
//    List<EObject> targeted = new ArrayList<>();
//    // In the case of a EReference, the returned 'Object' from eGet() is necessarily: EObject | List<EObject>
//    if (reference.isMany())
//    for(Object o: (List<?>) referred)
//    targeted.add((EObject) o);
//    else
//    targeted.add((EObject) referred);
//    futureLinks.add(new Tuple3<>(reference.getName(), obj, targeted));
//}
//}
//
//    // Create concrete DynamicLinks from futureLinks
//    for(Tuple3<String, EObject, List<EObject>> futureLink : futureLinks){
//    List<DynamicElement> targets = new ArrayList<>();
//    for(EObject a_target : futureLink._3()) {
//    targets.add(eObj_to_element.get(a_target));
//}
//    links.add(new DynamicLink(futureLink._1(), eObj_to_element.get(futureLink._2()), targets));
//}
//}
