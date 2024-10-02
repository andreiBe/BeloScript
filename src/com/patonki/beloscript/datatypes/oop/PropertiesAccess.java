package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.parser.nodes.Node;

import java.util.Set;

public interface PropertiesAccess {
    Set<String> getPropertyKeys();
    Node getPropertyValue(String key);
    BeloClass getStaticProperty(String key);
}
