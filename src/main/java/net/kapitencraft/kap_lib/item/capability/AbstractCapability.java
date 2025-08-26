package net.kapitencraft.kap_lib.item.capability;


public interface AbstractCapability<D> {

    void copyFrom(D data);

    D getData();
}
