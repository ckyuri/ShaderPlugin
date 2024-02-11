package me.kyuri.shaderplugin;

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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import revxrsal.commands.annotation.Command;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandShader implements Listener {
    private final Map<UUID, Creeper> creeperMap = new HashMap<>();
    private final Map<UUID, Location> deathLocations = new HashMap<>();
    private final Map<UUID, Boolean> shaderToggles = new HashMap<>();



    @Command("toggleshader")
    public void onShaderCommand(Player player) {
        UUID playerId = player.getUniqueId();
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        Creeper creeper = creeperMap.get(playerId);
        boolean shaderToggle = shaderToggles.getOrDefault(playerId, false);
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Team team = board.getTeam("noCollision");


        if (creeper == null && !shaderToggle) {
            shaderToggles.put(playerId, true);

            // If creeper is not found for this player, create a new one
            CraftWorld craftWorld = (CraftWorld) player.getWorld();
            ServerLevel serverLevel = craftWorld.getHandle();
            creeper = new Creeper(EntityType.CREEPER, serverLevel);
            CraftCreeper craftCreeper = new CraftCreeper((CraftServer) Bukkit.getServer(), creeper);

            creeper.setPos(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
            creeper.setNoAi(true);
            creeper.setInvulnerable(true);
            creeper.setInvisible(true);
            craftCreeper.setInvisible(true);
            craftCreeper.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));


            serverLevel.addFreshEntity(creeper);

            creeperMap.put(playerId, creeper);

            team.addEntry(player.getName());
            team.addEntry(creeper.getStringUUID());

            // Send camera packet once on creation
            ClientboundSetCameraPacket packet = new ClientboundSetCameraPacket(creeper);
            serverPlayer.connection.send(packet);

            player.setHealth(0);
            player.setRespawnLocation(deathLocations.get(playerId));
            player.spigot().respawn();
            Bukkit.getScheduler().runTaskLater(ShaderPlugin.getInstance(), () -> {
                player.spigot().respawn();
            }, 1);

        } else{
            shaderToggles.put(playerId, false);
            serverPlayer.connection.send(new ClientboundSetCameraPacket(serverPlayer));
            creeper.remove(Entity.RemovalReason.DISCARDED);
            creeper.kill();
            creeperMap.remove(playerId);

            team.removeEntry(player.getName());
            team.removeEntry(creeper.getStringUUID());
        }
        player.sendMessage("Shader toggled " + (shaderToggles.get(playerId) ? "on" : "off") + " for player " + playerId);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        deathLocations.put(player.getUniqueId(), player.getLocation());
        event.deathMessage(null);
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location deathLocation = deathLocations.get(player.getUniqueId());
        if (deathLocation != null) {
            event.setRespawnLocation(deathLocation);
        }
    }



}
