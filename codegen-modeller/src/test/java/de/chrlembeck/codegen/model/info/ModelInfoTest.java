package de.chrlembeck.codegen.model.info;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ModelInfoTest {

    @Test
    public void test() throws NoSuchFieldException, SecurityException {
        final Field elementDataField = MyTest.class.getDeclaredField("strings");
        final ModelInfo<MyTest> modelInfo = new ModelInfo<>(MyTest.class);
        System.out.println(modelInfo.getPropertyInfos());

    }

    public static void main(final String[] args) throws NoSuchFieldException, SecurityException {
        new ModelInfoTest().test();
    }

    class MyTest {

        private List<String> strings = new ArrayList<>();

        private BigInteger[] bigInts;

        public void addString(final String value) {
            strings.add(value);
        }

        public void removeString(final String value) {
            strings.remove(value);
        }
    }
}
