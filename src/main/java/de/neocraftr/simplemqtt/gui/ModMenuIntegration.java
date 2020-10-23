package de.neocraftr.simplemqtt.gui;

import de.neocraftr.simplemqtt.SimpleMQTT;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> SimpleMQTT.getSimpleMQTT().getConfigGUI().createGui();
    }
}
