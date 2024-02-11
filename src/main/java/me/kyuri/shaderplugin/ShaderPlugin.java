package me.kyuri.shaderplugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftCreeper;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class ShaderPlugin extends JavaPlugin implements Listener {
    private static ShaderPlugin instance;
    ProtocolManager protocolManager;
    private static BukkitCommandHandler handler;
    ScoreboardManager manager;
    Scoreboard board;

    @Override
    public void onEnable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        getLogger().log(Level.INFO, "ShaderSystem was enabled successfully.");
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CommandShader(), this);
        handler = BukkitCommandHandler.create(this);
        handler.register(new CommandShader());
        setupTeam();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ShaderPlugin getInstance() {
        return instance;
    }


    /*
    * This method is required to prevent players from colliding with the creeper entity.
    * If this is removed or not called, players will be able to collide with the creeper.
    * craftCreeper.setCollidable(); is not enough to prevent players from colliding with the creeper.
    *  */

    public void setupTeam() {
        manager = Bukkit.getScoreboardManager();
        board = manager.getMainScoreboard();
        Team team = board.getTeam("noCollision");
        if (team == null) {
            team = board.registerNewTeam("noCollision");
        }
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }

}
