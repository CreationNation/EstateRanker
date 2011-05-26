package me.Ghappy.EstateRanker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Commander
{
  public static Yeditor plugin;
  private final Functions functions = new Functions();

  public void command(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if ((args[0].equalsIgnoreCase("add")) && (args.length == 3)) {
      boolean added = this.functions.addPlayer(args[1], args[2]);
      if (added)
        sender.sendMessage(ChatColor.AQUA + "[YEDIT]" + ChatColor.GREEN +
          " Player:" + args[1] + " sucessfully added.");
      else
        sender.sendMessage(ChatColor.AQUA + "[YEDIT]" + ChatColor.RED +
          " An error has occued. Check log.");
    }
    else if ((args[0].equalsIgnoreCase("modify")) && (args.length == 3)) {
      boolean added = this.functions.addPlayer(args[1], args[2]);
      if (added)
        sender.sendMessage(ChatColor.AQUA + "[YEDIT]" + ChatColor.GREEN +
          " Player:" + args[1] + " sucessfully modified.");
      else
        sender.sendMessage(ChatColor.AQUA + "[YEDIT]" + ChatColor.RED +
          " An error has occued. Check log.");
    }
    else if ((args[0].equalsIgnoreCase("del")) && (args.length == 2)) {
      boolean delete = false;
      boolean exsists = this.functions.checkPlayer(args[1]);
      if (exsists) {
        delete = this.functions.delPlayer(args[1]);
      }
      if (delete)
        sender.sendMessage(ChatColor.AQUA + "[YEDIT]" + ChatColor.GREEN +
          " Player:" + args[1] + " sucessfully deleted.");
      else if (!exsists)
        sender.sendMessage(ChatColor.AQUA + "[YEDIT]" + ChatColor.RED +
          " Player does not exsist.");
      else {
        sender.sendMessage(ChatColor.AQUA + "[YEDIT]" + ChatColor.RED +
          " An error has occued. Check log.");
      }
    }
    else if ((args[0].equalsIgnoreCase("help")) ||
      (args[0].equalsIgnoreCase("?"))) {
      sender.sendMessage(ChatColor.AQUA + "[YEDIT] -Help- Plugin by Samkio:");
      sender.sendMessage(ChatColor.AQUA + "[YEDIT]" + ChatColor.WHITE +
        " /yt add <player> <group>");
      sender.sendMessage(ChatColor.AQUA + "[YEDIT]" + ChatColor.WHITE +
        " /yt modify <player> <group>");
    } else {
      sender.sendMessage(ChatColor.AQUA + "[YEDIT]" + ChatColor.RED +
        " Bad Syntax. /yt help.");
    }
  }

}
