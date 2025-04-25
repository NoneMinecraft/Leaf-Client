package net.nonemc.leaf.injection.forge.mixins.client;

public class ScrollBlocker {
    private static boolean scrollBlocked = false;

    public static void setScrollBlocked(boolean blocked) {
        scrollBlocked = blocked;
    }

    public static boolean isScrollBlocked() {
        return scrollBlocked;
    }
}
