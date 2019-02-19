package de.chrlembeck.codegen.model.info;

public interface ElementGetter<C, P> {

    P getElement(C object, int index);
}