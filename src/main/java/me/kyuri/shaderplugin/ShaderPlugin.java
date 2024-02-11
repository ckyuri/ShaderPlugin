package me.kyuri.shaderplugin;


import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import revxrsal.commands.bukkit.BukkitCommandHandler;


import java.util.logging.Level;

public final class ShaderPlugin extends JavaPlugin implements Listener {
    private static ShaderPlugin instance;
    ScoreboardManager manager;
    Scoreboard board;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().log(Level.INFO, "ShaderSystem was enabled successfully.");
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CommandShader(), this);
        BukkitCommandHandler handler = BukkitCommandHandler.create(this);
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
