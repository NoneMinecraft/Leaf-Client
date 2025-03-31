package net.nonemc.leaf.utils.particles;

import java.util.LinkedList;

public final class EvictingList<T> extends LinkedList<T> {

    private final int maxSize;

    public EvictingList(final int maxSize) {
        this.maxSize = maxSize;
    }


    @Override
    public boolean add(final T t) {
        if (size() >= maxSize) removeFirst();
        return super.add(t);
    }
}