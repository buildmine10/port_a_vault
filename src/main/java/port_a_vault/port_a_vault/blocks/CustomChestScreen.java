package port_a_vault.port_a_vault.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CustomChestScreen extends HandledScreen<CustomChestScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");

    public CustomChestScreen(CustomChestScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);
        this.backgroundHeight = 114 + 6 * 18;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title.getString(), 8.0F, 6.0F, 4210752);
        this.textRenderer.draw(matrices, this.playerInventoryTitle.getString(), 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
    }


    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }
}
