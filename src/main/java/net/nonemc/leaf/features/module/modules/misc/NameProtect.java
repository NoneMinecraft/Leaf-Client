package net.nonemc.leaf.features.module.modules.misc;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.event.EventTarget;
import net.nonemc.leaf.event.TextEvent;
import net.nonemc.leaf.features.module.Module;
import net.nonemc.leaf.features.module.ModuleCategory;
import net.nonemc.leaf.features.module.ModuleInfo;
import net.nonemc.leaf.file.configs.FriendsConfig;
import net.nonemc.leaf.utils.misc.StringUtils;
import net.nonemc.leaf.utils.render.ColorUtils;
import net.nonemc.leaf.utils.render.GetColorUtils;
import net.nonemc.leaf.value.BoolValue;
import net.nonemc.leaf.value.TextValue;

@ModuleInfo(name = "NameProtect", category = ModuleCategory.MISC)
public class NameProtect extends Module {

    private final TextValue fakeNameValue = new TextValue("FakeName", "&cProtected User");
    private final TextValue allFakeNameValue = new TextValue("AllPlayersFakeName", "Leaf");
    public final BoolValue selfValue = new BoolValue("Yourself", true);
    public final BoolValue tagValue = new BoolValue("Tag", false);
    public final BoolValue allPlayersValue = new BoolValue("AllPlayers", false);

    @EventTarget
    public void onText(final TextEvent event) {
        if (mc.thePlayer == null || event.getText().contains("§8[§9§l" + Leaf.CLIENT_NAME + "§8] §3") || event.getText().startsWith("/") || event.getText().startsWith(Leaf.commandManager.getPrefix() + ""))
            return;

        for (final FriendsConfig.Friend friend : Leaf.fileManager.getFriendsConfig().getFriends())
            event.setText(StringUtils.replace(event.getText(), friend.getPlayerName(), ColorUtils.translateAlternateColorCodes(friend.getAlias()) + "§f"));

        event.setText(StringUtils.replace(
                event.getText(),
                mc.thePlayer.getName(),
                (selfValue.get() ? (tagValue.get() ? StringUtils.injectAirString(mc.thePlayer.getName()) + " §7(§r" + ColorUtils.translateAlternateColorCodes(fakeNameValue.get() + "§r§7)") : ColorUtils.translateAlternateColorCodes(fakeNameValue.get()) + "§r") : mc.thePlayer.getName())
        ));

        if (allPlayersValue.get())
            for (final NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap())
                event.setText(StringUtils.replace(event.getText(), playerInfo.getGameProfile().getName(), ColorUtils.translateAlternateColorCodes(allFakeNameValue.get()) + "§f"));
    }

}