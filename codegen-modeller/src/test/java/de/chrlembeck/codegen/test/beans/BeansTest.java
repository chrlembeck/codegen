package de.chrlembeck.codegen.test.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;

public class BeansTest {

    public static void main(final String[] args) throws IntrospectionException {
        final BeanInfo info = Introspector.getBeanInfo(Kunde.class);
        System.out.println(info);
        for (final PropertyDescriptor p : info.getPropertyDescriptors()) {
            System.out.println(p);
        }
    }

    static class Kunde {

        private int kundeId;

        private int anArray;

        private List<Auftrag> auftraege;

        public int getKundeId() {
            return kundeId;
        }

        public void setKundeId(final int kundeId) {
            this.kundeId = kundeId;
        }

        public int getAnArray() {
            return anArray;
        }

        public List<Auftrag> getAuftraege() {
            return auftraege;
        }

        public void setAnArray(final int anArray) {
            this.anArray = anArray;
        }

        public void setAuftraege(final List<Auftrag> auftraege) {
            this.auftraege = auftraege;
        }

        public boolean add(final Auftrag e) {
            return auftraege.add(e);
        }

        public Auftrag get(final int index) {
            return auftraege.get(index);
        }

        public Auftrag set(final int index, final Auftrag element) {
            return auftraege.set(index, element);
        }

        public Auftrag remove(final int index) {
            return auftraege.remove(index);
        }

    }

    static class Auftrag {

        private int auftragId;

        public int getAuftragId() {
            return auftragId;
        }

        public void setAuftragId(final int auftragId) {
            this.auftragId = auftragId;
        }
    }
}
