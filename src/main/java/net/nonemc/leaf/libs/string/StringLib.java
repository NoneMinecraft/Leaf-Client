package net.nonemc.leaf.libs.string;

import java.util.Arrays;

public final class StringLib {
    public static String toCompleteString(final String[] args, final int start) {
        return toCompleteString(args, start, " ");
    }
    public static String toCompleteString(final String[] args, final int start, final String join) {
        if (args.length <= start) return "";
        return String.join(join, Arrays.copyOfRange(args, start, args.length));
    }
}