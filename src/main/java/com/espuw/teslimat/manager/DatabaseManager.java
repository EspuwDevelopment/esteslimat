package com.espuw.teslimat.manager;

import com.espuw.teslimat.TeslimatPlugin;

import java.io.File;
import java.sql.*;
import java.util.*;

public class DatabaseManager {

    private final TeslimatPlugin plugin;
    private Connection connection;

    public DatabaseManager(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() throws Exception {
        File dir = plugin.getDataFolder();
        if (!dir.exists()) dir.mkdirs();
        File db = new File(dir, "data.db");

        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());

        try (Statement st = connection.createStatement()) {
            st.executeUpdate(
                "PRAGMA journal_mode=WAL;" +
                "CREATE TABLE IF NOT EXISTS player_data (" +
                "  uuid TEXT NOT NULL PRIMARY KEY," +
                "  total INTEGER NOT NULL DEFAULT 0," +
                "  claimed TEXT NOT NULL DEFAULT ''" +
                ");"
            );
        }
    }

    public long getTotal(UUID uuid) {
        String sql = "SELECT total FROM player_data WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong("total");
        } catch (SQLException e) {
            plugin.getLogger().warning("DB getTotal hatası: " + e.getMessage());
        }
        return 0L;
    }

    public void addTotal(UUID uuid, long amount) {
        String sql = "INSERT INTO player_data (uuid, total, claimed) VALUES (?, ?, '') " +
                     "ON CONFLICT(uuid) DO UPDATE SET total = total + ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setLong(2, amount);
            ps.setLong(3, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("DB addTotal hatası: " + e.getMessage());
        }
    }

    public Set<Integer> getClaimed(UUID uuid) {
        String sql = "SELECT claimed FROM player_data WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String raw = rs.getString("claimed");
                Set<Integer> set = new HashSet<>();
                if (raw != null && !raw.isBlank()) {
                    for (String s : raw.split(",")) {
                        try { set.add(Integer.parseInt(s.trim())); } catch (NumberFormatException ignored) {}
                    }
                }
                return set;
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("DB getClaimed hatası: " + e.getMessage());
        }
        return new HashSet<>();
    }

    public void addClaimed(UUID uuid, int stage) {
        Set<Integer> current = getClaimed(uuid);
        current.add(stage);
        String joined = String.join(",", current.stream().map(String::valueOf).toList());
        String sql = "INSERT INTO player_data (uuid, total, claimed) VALUES (?, 0, ?) " +
                     "ON CONFLICT(uuid) DO UPDATE SET claimed = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, joined);
            ps.setString(3, joined);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("DB addClaimed hatası: " + e.getMessage());
        }
    }

    public List<Map.Entry<UUID, Long>> getTopList() {
        String sql = "SELECT uuid, total FROM player_data ORDER BY total DESC LIMIT 10";
        List<Map.Entry<UUID, Long>> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    list.add(Map.entry(uuid, rs.getLong("total")));
                } catch (IllegalArgumentException ignored) {}
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("DB getTopList hatası: " + e.getMessage());
        }
        return list;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            plugin.getLogger().warning("DB kapatma hatası: " + e.getMessage());
        }
    }
}
