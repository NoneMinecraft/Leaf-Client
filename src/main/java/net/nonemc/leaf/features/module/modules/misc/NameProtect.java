package net.nonemc.leaf.features.module.modules.misc;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.event.EventTarget;
import net.nonemc.leaf.event.TextEvent;
import net.nonemc.leaf.features.module.Module;
import net.nonemc.leaf.features.module.ModuleCategory;
import net.nonemc.leaf.features.module.ModuleInfo;
import net.nonemc.leaf.file.configs.FriendsConfig;
import net.nonemc.leaf.libs.render.ColorUtils;
import net.nonemc.leaf.value.BoolValue;
import net.nonemc.leaf.value.TextValue;

import java.util.HashMap;

import static net.nonemc.leaf.file.FileConfigManagerKt.getFriendsConfig;

@ModuleInfo(name = "NameProtect", category = ModuleCategory.MISC)
public class NameProtect extends Module {
    private static final HashMap<String, String> airCache = new HashMap<>();
    private final TextValue fakeNameValue = new TextValue("FakeName", "&cProtected User");
    private final TextValue allFakeNameValue = new TextValue("AllPlayersFakeName", "Leaf");
    public final BoolValue selfValue = new BoolValue("Yourself", true);
    public final BoolValue tagValue = new BoolValue("Tag", false);
    public final BoolValue allPlayersValue = new BoolValue("AllPlayers", false);

    @EventTarget
    public void onText(final TextEvent event) {
        if (mc.thePlayer == null || event.getText().contains("§8[§9§l" + Leaf.CLIENT_NAME + "§8] §3") || event.getText().startsWith("/") || event.getText().startsWith(Leaf.commandManager.getPrefix() + ""))
            return;

        for (final FriendsConfig.Friend friend : getFriendsConfig().getFriends())
            event.setText(replace(event.getText(), friend.getPlayerName(), ColorUtils.translateAlternateColorCodes(friend.getAlias()) + "§f"));

        event.setText(replace(
                event.getText(),
                mc.thePlayer.getName(),
                (selfValue.get() ? (tagValue.get() ? injectAirString(mc.thePlayer.getName()) + " §7(§r" + ColorUtils.translateAlternateColorCodes(fakeNameValue.get() + "§r§7)") : ColorUtils.translateAlternateColorCodes(fakeNameValue.get()) + "§r") : mc.thePlayer.getName())
        ));

        if (allPlayersValue.get())
            for (final NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap())
                event.setText(replace(event.getText(), playerInfo.getGameProfile().getName(), ColorUtils.translateAlternateColorCodes(allFakeNameValue.get()) + "§f"));
    }
    public static String injectAirString(String str) {
        if (airCache.containsKey(str)) return airCache.get(str);

        StringBuilder stringBuilder = new StringBuilder();

        boolean hasAdded = false;
        for (char c : str.toCharArray()) {
            stringBuilder.append(c);
            if (!hasAdded) stringBuilder.append('\uF8FF');
            hasAdded = true;
        }

        String result = stringBuilder.toString();
        airCache.put(str, result);

        return result;
    }
    public static String replace(final String string, final String searchChars, String replaceChars) {
        if (string.isEmpty() || searchChars.isEmpty() || searchChars.equals(replaceChars))
            return string;

        if (replaceChars == null)
            replaceChars = "";

        final int stringLength = string.length();
        final int searchCharsLength = searchChars.length();
        final StringBuilder stringBuilder = new StringBuilder(string);

        for (int i = 0; i < stringLength; i++) {
            final int start = stringBuilder.indexOf(searchChars, i);

            if (start == -1) {
                if (i == 0)
                    return string;

                return stringBuilder.toString();
            }

            stringBuilder.replace(start, start + searchCharsLength, replaceChars);
        }

        return stringBuilder.toString();
    }

}