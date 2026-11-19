package com.example.shiftchallenge;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;

/** Menu pengaturan challenge */
public class ChallengeScreen extends Screen {
    private ValueSlider targetSlider;
    private ValueSlider secondsSlider;

    public ChallengeScreen() {
        super(Component.literal("Shift Challenge"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 4;

        targetSlider = new ValueSlider(cx - 100, y, 200, 20, "Target Shift", 10, 2000, 10,
                ChallengeConfig.target, v -> ChallengeConfig.target = v);
        secondsSlider = new ValueSlider(cx - 100, y + 24, 200, 20, "Waktu (detik)", 5, 120, 1,
                ChallengeConfig.seconds, v -> ChallengeConfig.seconds = v);
        this.addRenderableWidget(targetSlider);
        this.addRenderableWidget(secondsSlider);

        // Preset cepat
        addPreset(cx - 100, y + 52, 62, "500/30", 500, 30);
        addPreset(cx - 31, y + 52, 62, "300/30", 300, 30);
        addPreset(cx + 38, y + 52, 62, "100/10", 100, 10);

        this.addRenderableWidget(Button.builder(Component.literal("MULAI"), b -> {
            ChallengeConfig.save();
            this.onClose();
            ChallengeManager.start();
        }).bounds(cx - 100, y + 100, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Tutup"), b -> this.onClose())
                .bounds(cx - 100, y + 124, 200, 20).build());
    }

    private void addPreset(int x, int y, int w, String label, int target, int seconds) {
        this.addRenderableWidget(Button.builder(Component.literal(label), b -> {
            targetSlider.setInt(target);
            secondsSlider.setInt(seconds);
        }).bounds(x, y, w, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        // super.render menggambar background + widget
        super.render(g, mouseX, mouseY, delta);

        int cx = this.width / 2;
        int y = this.height / 4;

        centered(g, "SHIFT CHALLENGE", cx, y - 22, 0xFFFFAA00);
        centered(g, String.format("Butuh sekitar %.1f shift/detik",
                ChallengeConfig.target / (double) ChallengeConfig.seconds), cx, y + 78, 0xFFFFFFFF);
        centered(g, String.format("Rekor kamu: %.1f shift/detik", ChallengeConfig.bestCps), cx, y + 89, 0xFFAAAAAA);
    }

    private void centered(GuiGraphics g, String text, int cx, int y, int color) {
        g.drawString(this.font, text, cx - this.font.width(text) / 2, y, color, true);
    }

    @Override
    public void removed() {
        ChallengeConfig.save();
    }

    /** Slider dengan nilai integer dan step */
    private static class ValueSlider extends AbstractSliderButton {
        private final String label;
        private final int min, max, step;
        private final IntConsumer onChange;

        ValueSlider(int x, int y, int w, int h, String label, int min, int max, int step,
                    int initial, IntConsumer onChange) {
            super(x, y, w, h, Component.empty(), (initial - min) / (double) (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.step = step;
            this.onChange = onChange;
            updateMessage();
        }

        private int getInt() {
            double raw = min + this.value * (max - min);
            int v = (int) (Math.round(raw / step) * step);
            return ChallengeConfig.clamp(v, min, max);
        }

        void setInt(int v) {
            this.value = (ChallengeConfig.clamp(v, min, max) - min) / (double) (max - min);
            updateMessage();
            applyValue();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(label + ": " + getInt()));
        }

        @Override
        protected void applyValue() {
            onChange.accept(getInt());
        }
    }
}
