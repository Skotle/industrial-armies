package dev.tuningneo.industrialarmies.client;

import dev.tuningneo.industrialarmies.army.CombatStance;
import dev.tuningneo.industrialarmies.army.SoldierOrder;
import dev.tuningneo.industrialarmies.entity.CompanySoldierEntity;
import dev.tuningneo.industrialarmies.network.UpdateSoldierSettingsPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public final class SoldierSettingsScreen extends Screen {
    private final int entityId;
    private final String initialName;
    private SoldierOrder order;
    private CombatStance combatStance;
    private int followDistance;
    private boolean showName;

    private EditBox nameBox;
    private Button orderButton;
    private Button stanceButton;
    private Button distanceButton;
    private Button visibilityButton;

    public SoldierSettingsScreen(CompanySoldierEntity soldier) {
        super(Component.translatable("screen.industrial_armies.soldier_settings"));
        entityId = soldier.getId();
        initialName = soldier.getCustomName() == null ? "" : soldier.getCustomName().getString();
        order = soldier.getOrder();
        combatStance = soldier.getCombatStance();
        followDistance = soldier.getFollowDistance();
        showName = soldier.isCustomNameVisible();
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int top = height / 2 - 92;

        nameBox = new EditBox(font, centerX - 100, top + 26, 200, 20,
                Component.translatable("screen.industrial_armies.name"));
        nameBox.setMaxLength(32);
        nameBox.setValue(initialName);
        addRenderableWidget(nameBox);

        orderButton = addRenderableWidget(Button.builder(orderLabel(), button -> {
            order = SoldierOrder.byId(order.ordinal() + 1);
            button.setMessage(orderLabel());
        }).bounds(centerX - 100, top + 56, 200, 20).build());

        stanceButton = addRenderableWidget(Button.builder(stanceLabel(), button -> {
            combatStance = combatStance.next();
            button.setMessage(stanceLabel());
        }).bounds(centerX - 100, top + 80, 200, 20).build());

        distanceButton = addRenderableWidget(Button.builder(distanceLabel(), button -> {
            followDistance = followDistance == 4 ? 8 : followDistance == 8 ? 12 : 4;
            button.setMessage(distanceLabel());
        }).bounds(centerX - 100, top + 104, 200, 20).build());

        visibilityButton = addRenderableWidget(Button.builder(visibilityLabel(), button -> {
            showName = !showName;
            button.setMessage(visibilityLabel());
        }).bounds(centerX - 100, top + 128, 200, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.industrial_armies.save"), button -> save())
                .bounds(centerX - 100, top + 158, 96, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(centerX + 4, top + 158, 96, 20).build());
        setInitialFocus(nameBox);
    }

    private void save() {
        PacketDistributor.sendToServer(new UpdateSoldierSettingsPayload(
                entityId,
                nameBox.getValue(),
                order.ordinal(),
                combatStance.ordinal(),
                followDistance,
                showName));
        onClose();
    }

    private Component orderLabel() {
        return Component.translatable("screen.industrial_armies.order",
                Component.translatable("screen.industrial_armies.order." + order.name().toLowerCase()));
    }

    private Component stanceLabel() {
        return Component.translatable("screen.industrial_armies.stance",
                Component.translatable("screen.industrial_armies.stance." + combatStance.name().toLowerCase()));
    }

    private Component distanceLabel() {
        return Component.translatable("screen.industrial_armies.follow_distance", followDistance);
    }

    private Component visibilityLabel() {
        return Component.translatable("screen.industrial_armies.show_name",
                Component.translatable(showName ? "options.on" : "options.off"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 102, 0xFFFFFF);
        graphics.drawString(font, Component.translatable("screen.industrial_armies.name"),
                width / 2 - 100, height / 2 - 82, 0xA0A0A0, false);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
