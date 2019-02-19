package de.chrlembeck.codegen.model.info;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PropertyInfoTest {

    @Test
    public void test() throws NoSuchFieldException, SecurityException {
        final Field elementDataField = MyTest.class.getDeclaredField("strings");
        final PropertyInfo<MyTest, Object> info = new PropertyInfo<>(MyTest.class, elementDataField);
        Assertions.assertNotNull(info.getElementAdder());
        Assertions.assertNotNull(info.getElementRemover());

        final MyTest test = new MyTest();

        info.getElementAdder().accept(test, "one");
    }

    public static void main(final String[] args) throws NoSuchFieldException, SecurityException {
        new PropertyInfoTest().test();
    }

    class MyTest {

        private List<String> strings = new ArrayList<>();

        public void addString(final String value) {
            strings.add(value);
        }

        public void removeString(final String value) {
            strings.remove(value);
        }
    }
}