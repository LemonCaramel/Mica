package net.neoforged.neoforge.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;

public interface IConfigScreenFactory {

    Screen createScreen(ModContainer modContainer, Screen modListScreen);
}
