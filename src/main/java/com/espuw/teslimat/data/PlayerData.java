package com.espuw.teslimat.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Bir oyuncunun teslimat verilerini tutar.
 */
public class PlayerData {

    private final UUID uuid;
    private long toplamTeslimat;
    // Hangi aşama ödüllerini aldığı (1–5)
    private final Set<Integer> alinanOduller = new HashSet<>();

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.toplamTeslimat = 0;
    }

    public PlayerData(UUID uuid, long toplamTeslimat, Set<Integer> alinanOduller) {
        this.uuid = uuid;
        this.toplamTeslimat = toplamTeslimat;
        this.alinanOduller.addAll(alinanOduller);
    }

    public UUID getUuid() { return uuid; }

    public long getToplamTeslimat() { return toplamTeslimat; }

    public void addTeslimat(long miktar) { this.toplamTeslimat += miktar; }

    public boolean odulAlindi(int asama) { return alinanOduller.contains(asama); }

    public void odulAl(int asama) { alinanOduller.add(asama); }

    public Set<Integer> getAlinanOduller() { return alinanOduller; }
}
