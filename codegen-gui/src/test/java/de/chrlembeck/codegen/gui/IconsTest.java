package de.chrlembeck.codegen.gui;

import javax.swing.Icon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IconsTest {

    @Test
    public void iconsExist() {
        for (final IconFactory iconFactory : IconFactory.values()) {
            final Icon icon = iconFactory.icon();
            Assertions.assertNotNull(icon,
                    "Icon " + iconFactory.name() + " does not exists. :" + iconFactory.getIconName());
        }
    }

    @Test
    public void iconNames() {
        for (final IconFactory iconFactory : IconFactory.values()) {
            final Icon icon = iconFactory.icon();
            Assertions.assertTrue(iconFactory.name().endsWith(Integer.toString(icon.getIconWidth())),
                    "Icon " + iconFactory.name() + " has unexpected width " + icon.getIconWidth());
            Assertions.assertTrue(iconFactory.name().endsWith(Integer.toString(icon.getIconHeight())),
                    "Icon " + iconFactory.name() + " has unexpected height " + icon.getIconHeight());
        }
    }
}