package net.nonemc.leaf.launch.data.modernui.clickgui.fonts.util;

public final class SneakyThrowing {

    public static RuntimeException sneakyThrow(Throwable throwable) {
        return sneakyThrow0(throwable);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T sneakyThrow0(Throwable throwable) throws T {
        throw (T) throwable;
    }

    private SneakyThrowing() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
