package me.Ghappy.EstateRanker;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijikokun.bukkit.Permissions.PropertyHandler;

public class Yeditor extends JavaPlugin
{
  private static final Logger log = Logger.getLogger("Minecraft");
  private final Commander commands = new Commander();
  private final Functions functions = new Functions();
  private final Yaml yaml = new Yaml(new SafeConstructor());
  public static PermissionHandler Permissions = null;
  public static Server Server = null;
  private String DefaultWorld = "";
  public static String configer = "plugins/Permissions/world.yml";

  public void onEnable() {
    PropertyHandler server = new PropertyHandler("server.properties");
    String DefaultWorld = server.getString("level-name");
    configer = "plugins/Permissions/" + DefaultWorld + ".yml";
    setupPermissions();
  }

  public void onDisable()
  {
    System.out.println("Yeditor Disabled");
  }

  public void setupPermissions() {
    Plugin test = getServer().getPluginManager()
      .getPlugin("Permissions");
    PluginDescriptionFile pdfFile = getDescription();

    if (Permissions == null)
      if (test != null) {
        getServer().getPluginManager().enablePlugin(test);
        Permissions = ((Permissions)test).getHandler();
      } else {
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() +
          "not enabled. Permissions not detected");
        getServer().getPluginManager().disablePlugin(this);
      }
  }

  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if (((commandLabel.equalsIgnoreCase("yt")) ||
      (commandLabel
      .equalsIgnoreCase("yedit"))) && (args.length > 0)) {
      if (Permissions.has((Player)sender, "yeditor.editor")) {
        this.commands.command(sender, cmd, commandLabel, args);
      }

      return true;
    }
    return false;
  }
}
