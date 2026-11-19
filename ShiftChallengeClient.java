package com.example.shiftchallenge;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class ShiftChallengeClient implements ClientModInitializer {
    public static final String MOD_ID = "shiftchallenge";

    private static KeyMapping menuKey;
    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        ChallengeConfig.load();

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main"));

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.shiftchallenge.menu", InputConstants.Type.KEYSYM, InputConstants.KEY_K, category));
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.shiftchallenge.toggle", InputConstants.Type.KEYSYM, InputConstants.KEY_J, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.consumeClick()) {
                if (client.player != null && client.screen == null) {
                    client.setScreen(new ChallengeScreen());
                }
            }
            while (toggleKey.consumeClick()) {
                if (client.player != null) ChallengeManager.toggle();
            }
            ChallengeManager.tick(client);
        });

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "challenge_hud"),
                ChallengeHud::render);
    }
}
