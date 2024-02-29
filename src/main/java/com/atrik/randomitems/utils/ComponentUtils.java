package com.atrik.randomitems.utils;

import net.minecraft.network.chat.Component;

public class ComponentUtils {

    public static Component doRainbowEffect(String patterns, Component component) {
        StringBuilder textStrBuilder = new StringBuilder();
        String text = component.getString();
        for (int i = 0; i < text.length(); i++) {
            textStrBuilder.append('§').append(patterns.charAt(i % patterns.length())).append(text.charAt(i));
        }
        return Component.literal(textStrBuilder.toString());
    }

}
