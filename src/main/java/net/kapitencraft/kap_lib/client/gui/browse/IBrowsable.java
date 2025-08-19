package net.kapitencraft.kap_lib.client.gui.browse;

import net.minecraft.network.chat.Component;

/**
 * interface that contains methods all Browsables must contain
 */
public interface IBrowsable {

    void openScreen();

    Component getName();
}
