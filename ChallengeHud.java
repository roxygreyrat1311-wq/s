package com.example.shiftchallenge;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

/** Tampilan counter di layar saat challenge berjalan */
public final class ChallengeHud {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;
    private static final int BLUE = 0xFF5555FF;
    private static final int GOLD = 0xFFFFAA00;
    private static final int GRAY = 0xFFAAAAAA;

    private ChallengeHud() { }

    public static void render(GuiGraphics g, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (ChallengeManager.state == ChallengeManager.State.IDLE) return;

        Font font = mc.font;
        int w = g.guiWidth();
        int h = g.guiHeight();
        float cx = w / 2f;
        long now = System.currentTimeMillis();

        switch (ChallengeManager.state) {
            case COUNTDOWN -> {
                long left = Math.max(0, ChallengeManager.countdownEndMs - now);
                int sec = (int) Math.ceil(left / 1000.0);
                drawScaled(g, font, "Bersiap...", cx, h * 0.22f, 1.5f, GRAY);
                drawScaled(g, font, String.valueOf(Math.max(1, sec)), cx, h * 0.30f, 6f, GOLD);
                drawScaled(g, font, ChallengeManager.runTarget + " shift / " + ChallengeManager.runSeconds + " detik",
                        cx, h * 0.52f, 1.2f, WHITE);
            }
            case RUNNING -> {
                long total = ChallengeManager.runSeconds * 1000L;
                long elapsed = now - ChallengeManager.startMs;
                long left = Math.max(0, total - elapsed);
                double expected = Math.min(1.0, elapsed / (double) total);
                double actual = Math.min(1.0, ChallengeManager.count / (double) ChallengeManager.runTarget);
                int color = actual >= expected ? GREEN : RED;

                drawScaled(g, font, String.valueOf(ChallengeManager.count), cx, 18, 5f, color);
                drawScaled(g, font, "/ " + ChallengeManager.runTarget, cx, 62, 1.5f, GRAY);
                drawScaled(g, font, String.format("%.1fs", left / 1000.0), cx, 80, 2.5f, WHITE);

                int barW = 200;
                int x1 = (int) cx - barW / 2;
                // bar progress shift
                g.fill(x1 - 1, 106, x1 + barW + 1, 116, 0xFF000000);
                g.fill(x1, 107, x1 + (int) (barW * actual), 115, color);
                // bar waktu
                g.fill(x1 - 1, 120, x1 + barW + 1, 126, 0xFF000000);
                g.fill(x1, 121, x1 + (int) (barW * (1.0 - expected)), 125, BLUE);
            }
            case FINISHED -> {
                boolean won = ChallengeManager.won;
                drawScaled(g, font, won ? "MENANG!" : "WAKTU HABIS!", cx, 18, 4f, won ? GOLD : RED);
                drawScaled(g, font, ChallengeManager.count + " / " + ChallengeManager.runTarget + " shift",
                        cx, 58, 2f, WHITE);
                drawScaled(g, font, String.format("%.1f shift/detik", ChallengeManager.lastCps), cx, 78, 1.5f, GRAY);
                if (ChallengeManager.newRecord) {
                    drawScaled(g, font, "REKOR BARU!", cx, 94, 1.5f, GOLD);
                }
            }
            default -> { }
        }
    }

    /** Teks di-center di cx, diperbesar dengan scale. */
    private static void drawScaled(GuiGraphics g, Font font, String text, float cx, float y, float scale, int color) {
        Matrix3x2fStack m = g.pose();
        m.pushMatrix();
        m.translate(cx, y);
        m.scale(scale, scale);
        g.drawString(font, text, -font.width(text) / 2, 0, color, true);
        m.popMatrix();
    }
}
