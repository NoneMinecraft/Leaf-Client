package net.nonemc.leaf.utils.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.nonemc.leaf.event.EntityKilledEvent;
import net.nonemc.leaf.event.EventTarget;
import net.nonemc.leaf.event.Listenable;

public class StatisticsUtils implements Listenable {
    private static int kills;
    private static int deaths;

    @EventTarget
    public void onTargetKilled(EntityKilledEvent e) {
        if (!(e.getTargetEntity() instanceof EntityPlayer)) {
            return;
        }

        kills++;
    }

    public static void addDeaths() {
        deaths++;
    }

    public static int getDeaths() {
        return deaths;
    }

    public static int getKills() {
        return kills;
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
