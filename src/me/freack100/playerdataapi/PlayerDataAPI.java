/*
 *
 *  * Copyright © 2015 Paul Waslowski <freack1208@gmail.com>
 *  * This work is free. You can redistribute it and/or modify it under the
 *  * terms of the Do What The Fuck You Want To Public License, Version 2,
 *  * as published by Sam Hocevar. See the LICENSE file for more details.
 *
 */

package me.freack100.playerdataapi;

import me.freack100.playerdataapi.collector.Collector;
import me.freack100.playerdataapi.collector.collectors.BlocksBrokenCollector;
import me.freack100.playerdataapi.collector.collectors.BlocksPlacedCollector;
import me.freack100.playerdataapi.commands.VisualizeDataCommand;
import me.freack100.playerdataapi.file.PlayerDataFile;
import me.freack100.playerdataapi.inventory.InventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class PlayerDataAPI extends JavaPlugin implements Listener {

    private final UUID CONSOLE_UUID = UUID.fromString("8f255927-399c-4acf-a90e-a01c22cccb17");
    public InventoryBuilder inventoryBuilder;
    private List<Collector> collectors;
    private HashMap<UUID, PlayerData> data;

    @Override
    public void onEnable() {
        collectors = new ArrayList();
        data = new HashMap();

        inventoryBuilder = new InventoryBuilder(this);

        Bukkit.getPluginManager().registerEvents(this, this);

        //Register Commands
        getCommand("visualizeData").setExecutor(new VisualizeDataCommand(this));

        //Loading the built in collectors [DEBUGGING ONLY RIGHT NOW]
        addCollector(new BlocksPlacedCollector(this));
        addCollector(new BlocksBrokenCollector(this));

        if (!(new File(this.getDataFolder() + File.separator + "data").exists()))
            new File(this.getDataFolder() + File.separator + "data").mkdirs();

        //Add data for the console
        data.put(CONSOLE_UUID, new PlayerData());

    }

    @Override
    public void onDisable() {

    }

    public PlayerData getData(UUID uuid) {
        return (data.containsKey(uuid) ? data.get(uuid) : null);
    }

    public void addCollector(Collector collector) {
        if (!collectors.contains(collector)) {
            Bukkit.getPluginManager().registerEvents(collector, this);
            collectors.add(collector);
        }
    }

    public UUID getConsoleUUID() {
        return CONSOLE_UUID;
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        if(new File(getDataFolder() + File.separator + "data" + File.separator + e.getPlayer().getUniqueId().toString()).exists()){
            data.put(e.getPlayer().getUniqueId(), new PlayerDataFile(e.getPlayer().getUniqueId(),this).readData());
        }else {
            data.put(e.getPlayer().getUniqueId(), new PlayerData());
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        PlayerDataFile file = new PlayerDataFile(e.getPlayer().getUniqueId(), this);
        file.saveData();
    }

}
