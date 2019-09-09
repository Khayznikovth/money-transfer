package ru.khayz.ms;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Address {
    static private AtomicInteger subscriberIdCounter = new AtomicInteger();
    final private int subsriberId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return subsriberId == address.subsriberId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subsriberId);
    }

    public Address() {
        subsriberId = subscriberIdCounter.incrementAndGet();
    }


}
