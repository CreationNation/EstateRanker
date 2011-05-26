/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package me.Ghappy.EstateRanker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.iConomy.iConomy;
import com.iConomy.system.Holdings;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 *
 * @author Mathias
 */
public class EstateRanker extends JavaPlugin{
private static final Logger log = Logger.getLogger("Minecraft");
public static PermissionHandler Permissions;
public String[] zoneIDs = {"officerestate" , "minerestate" , "builderestate" , "traderestate", "default"};
public String[] strEstates = {"default", "trader", "banker", "entrepreneur", "treasurer", "miner", "driller", "explorer", "excavator", "officer", "sergeant", "captain", "enforcer", "builder", "constructor", "architect", "creator"};
public String[] strLvl4 = {"treasurer", "enforcer", "creator", "excavator"};
public String[] strLvl3 = {"entrepreneur", "captain", "explorer", "architect"};
public String[] strLvl2 = {"banker", "sergeant", "driller", "constructor"};
public String[] strLvl1 = {"trader", "officer", "miner", "builder"};
public String[] strOfficer = {"officer", "sergeant", "captain", "enforcer"};
public String[] strMiner = {"miner", "driller", "explorer", "excavator"};
public String[] strTrader = {"trader", "banker", "entrepreneur", "treasurer"};
public String[] strBuilder = {"builder", "constructor", "architect", "creator"};
public final HashMap<Player, ArrayList<Block>> CMUsers = new HashMap<Player, ArrayList<Block>>();
public WorldGuardPlugin worldguard = null;
public String thisEstate, nextEstate;
public int intEstate = 0;
public double currentCN, costCN;
public iConomy iCon = null;
public List<String> zIDs = Arrays.asList(zoneIDs);
public List<String> lEst = Arrays.asList(strEstates);
public List<String> lvl4 = Arrays.asList(strLvl4);
public List<String> lvl3 = Arrays.asList(strLvl3);
public List<String> lvl2 = Arrays.asList(strLvl2);
public List<String> lvl1 = Arrays.asList(strLvl1);
public List<String> officer = Arrays.asList(strOfficer);
public List<String> miner = Arrays.asList(strMiner);
public List<String> trader = Arrays.asList(strTrader);
public List<String> builder = Arrays.asList(strBuilder);
public boolean canAfford = false, canLevel = false;
public Holdings acc;
public Functions func = new Functions();
private Configuration config;

    private void setupPermissions() {
        Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

        if (Permissions == null) {
            if (test != null) {
                Permissions = ((Permissions)test).getHandler();
            } else {
                log.info("[EstateRanker] Permission system not detected, defaulting to OP");
            }
        }
    }

    private void setupWorldGuard(){
        Plugin test = this.getServer().getPluginManager().getPlugin("WorldGuard");
        
        if(test != null){
            worldguard = (WorldGuardPlugin)getServer().getPluginManager().getPlugin("WorldGuard");
            log.info("[EstateRanker] WorldGuard detected :)!");
        } else {
            log.info("[EstateRanker] WorldGuard not found :(, Disabling plugin!");
            this.setEnabled(false);
        }
    }
    private void setupiConomy(){
        Plugin test = this.getServer().getPluginManager().getPlugin("iConomy");
        if(test != null){
            iCon = (iConomy)getServer().getPluginManager().getPlugin("iConomy");
            log.info("[EstateRanker] iConomy detected :)!");
        } else {
            log.info("[EstateRanker] iConomy not found :(");
        }
    }
    private void getPlayerInfo(Player p){
        String[] groups;
        thisEstate = "";
        nextEstate = "";
        intEstate = 0;

        groups = Permissions.getGroups(p.getWorld().getName(), p.getName());
        acc = iCon.getAccount(p.getName()).getHoldings();
        currentCN = acc.balance();
        for(int i = 0; i < groups.length; i++){
            if(lEst.contains(groups[i])){
                if(thisEstate != null && thisEstate != ""){
                    if(trader.contains(thisEstate)){
                        if(trader.indexOf(groups[i]) > trader.indexOf(thisEstate)){
                            thisEstate = groups[i];
                        }
                    } else if(miner.contains(thisEstate)){
                        if(miner.indexOf(groups[i]) > miner.indexOf(thisEstate)){
                            thisEstate = groups[i];
                        }
                    } else if(builder.contains(thisEstate)){
                        if(builder.indexOf(groups[i]) > builder.indexOf(thisEstate)){
                            thisEstate = groups[i];
                        }
                    } else if(officer.contains(thisEstate)){
                        if(officer.indexOf(groups[i]) > officer.indexOf(thisEstate)){
                            thisEstate = groups[i];
                        }
                    }
                } else {
                    thisEstate = groups[i];
                }
            }
        }
        if(thisEstate == null) thisEstate = "";
        if(lvl3.contains(thisEstate)){
            costCN = 500000;
            canLevel = true;
        } else if(lvl2.contains(thisEstate)){
            costCN = 150000;
            canLevel = true;
        } else if(lvl1.contains(thisEstate)){
            costCN = 30000;
            canLevel = true;
        } else if(lvl4.contains(thisEstate)){
            costCN = 1000000;
            canLevel = false;
        } else {
            if(thisEstate.equalsIgnoreCase("default")){
                costCN = 2000;
                canLevel = false;
            } else {
                costCN = 0;
                canLevel = false;
            }
        }
        log.info(thisEstate);
        if(!thisEstate.equals("default") && !lvl4.contains(thisEstate) && thisEstate != ""){
            for(int i = 0; i < strEstates.length; i++){
                if(thisEstate.equals(strEstates[i])){
                    nextEstate = strEstates[i + 1];
                }
            }
        } else {
            if(lvl4.contains(thisEstate)){
                nextEstate = "";
            }
        }
    }

    public void onDisable() {
        log.info("[EstateRanker] EstateRanker Disabled");
    }

    public void onEnable() {
        setupPermissions();
        setupWorldGuard();
        setupiConomy();
        log.info("[EstateRanker] EstateRanker is now Enabled! :)");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        List<String> strIDs;
        Player player = (Player)sender;
        strIDs = worldguard.getGlobalRegionManager().get(player.getWorld()).getApplicableRegionsIDs(new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
        getPlayerInfo(player);

        if(cmd.getName().equalsIgnoreCase("rankup")){
            
            if(strIDs.contains(zoneIDs[0])){
                intEstate = 0;
                if(!officer.contains(thisEstate) && !thisEstate.equals("") && !thisEstate.equalsIgnoreCase("default")){
                    player.sendMessage(ChatColor.DARK_AQUA + "~ Creation Nation RankUp ~");
                    player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                    player.sendMessage("You are in the " + thisEstate + " estate, this is the Officer rankUp area");
                    player.sendMessage("You must use the " + thisEstate + " RankUp area");
                    player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                    return true;
                }
            } else if (strIDs.contains(zoneIDs[1])) {
                intEstate = 1;
                if(!miner.contains(thisEstate) && !thisEstate.equals("") && !thisEstate.equalsIgnoreCase("default")){
                    player.sendMessage(ChatColor.DARK_AQUA + "~ Creation Nation RankUp ~");
                    player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                    player.sendMessage("Your are in the " + thisEstate + " estate, this is the Miners rankUp area");
                    player.sendMessage("You must use the " + thisEstate + " RankUp area");
                    player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                    return true;
                }
            } else if(strIDs.contains(zoneIDs[2])){
                intEstate = 2;
                if(!builder.contains(thisEstate) && !thisEstate.equals("") && !thisEstate.equalsIgnoreCase("default")){
                    player.sendMessage(ChatColor.DARK_AQUA + "~ Creation Nation RankUp ~");
                    player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                    player.sendMessage("You are in the " + thisEstate + " estate, this is the Builder rankUp area");
                    player.sendMessage("You must use the " + thisEstate + " RankUp area");
                    player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                    return true;
                }
            } else if(strIDs.contains(zoneIDs[3])){
                intEstate = 3;
                if(!trader.contains(thisEstate) && !thisEstate.equals("") && !thisEstate.equalsIgnoreCase("default")){
                    player.sendMessage(ChatColor.DARK_AQUA + "~ Creation Nation RankUp ~");
                    player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                    player.sendMessage("You are in the " + thisEstate + " estate, this is the Trader rankUp area");
                    player.sendMessage("You must use the " + thisEstate + " RankUp area");
                    player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + "You need to be in the presence of an estate god to do this!");
                return true;
            }
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("accept")){
                    levelUp(player);
                } else if(args[0].equalsIgnoreCase("decline")){
                    return true;
                } else if(args[0].equalsIgnoreCase("info")){
                    if(canLevel){
                        player.sendMessage(ChatColor.DARK_AQUA + "~ Creation Nation RankUp ~");
                        player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                        player.sendMessage("Your current estate is: " + thisEstate);
                        player.sendMessage("The next level in your estate is: " + nextEstate);
                        player.sendMessage("  ");
                        player.sendMessage("LevelUp cost: " + ChatColor.YELLOW + costCN + " CN");
                        player.sendMessage("Current CN: " + ChatColor.YELLOW + currentCN + " CN");
                        player.sendMessage("To accept the levelup, type " + ChatColor.GREEN + "/RankUp Accept" + ChatColor.WHITE + " and the cost will be automaticly charged");
                        player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                        return true;
                    } else {
                        if(lvl4.contains(thisEstate)){
                            player.sendMessage(ChatColor.DARK_AQUA + "~ Creation Nation RankUp ~");
                            player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                            player.sendMessage("You are currently level 4 in your estate");
                            player.sendMessage("Please speak to an Admin for information on leveling up further");
                            player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                            return true;
                        } else if(thisEstate.equalsIgnoreCase("default")){
                            player.sendMessage(ChatColor.DARK_AQUA + "~ Creation Nation RankUp ~");
                            player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                            player.sendMessage("You currently have no estate");
                            player.sendMessage("You can choose from one of these 4 starter estates:");
                            player.sendMessage(ChatColor.GREEN + "Miner" + ChatColor.WHITE + ", " + ChatColor.GOLD + "Trader" + ChatColor.WHITE + ", " + ChatColor.BLUE + "Officer" + ChatColor.WHITE + ", " + ChatColor.DARK_GRAY + "Builder");
                            player.sendMessage("  ");
                            player.sendMessage("Cost: " + ChatColor.YELLOW + costCN + " CN & 1 Reed Block");
                            player.sendMessage("Current CN: " + ChatColor.YELLOW + currentCN + " CN");
                            player.sendMessage("To get your estate, type " + ChatColor.GREEN + "/RankUp Start <Estate>" + ChatColor.WHITE + " and the cost will be automaticly charged");
                            player.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                            return true;
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + "Could not determine your current estate");
                            return true;
                        }
                    }
                } else if(args[0].equalsIgnoreCase("start")){
                    if(!thisEstate.equalsIgnoreCase("default")){
                        player.sendMessage(ChatColor.DARK_RED + "This command is for people who dont yet have an Estate");
                        return true;
                    }
                    if(args.length < 2){
                        player.sendMessage("The syntax for this command is:");
                        player.sendMessage(ChatColor.GREEN + "/RankUp Start <Builder; Trader; Miner; Officer>");
                        return true;
                    } else {
                        if(lvl1.contains(args[1].toLowerCase())){
                            firstEstate(player, args[1].toLowerCase());
                            return true;
                        } else {
                            player.sendMessage("The syntax for this command is:");
                            player.sendMessage(ChatColor.GREEN + "/RankUp Start <Builder; Trader; Miner; Officer>");
                            return true;
                        }
                    }
                }
            } else {
                player.sendMessage("Use " + ChatColor.GREEN + "/RankUp Info " + ChatColor.WHITE + "for information on leveling up");
                return true;
            }
        }
        return true;
    }
    public void levelUp(Player p){
        if(acc.hasEnough(costCN) || acc.hasOver(costCN)){
            acc.subtract(costCN);
            func.addPlayer(p.getName(), nextEstate);
            Permissions.reload(p.getWorld().getName());
            p.sendMessage("Congratulations, your rankUp was succesful");
            p.sendMessage("Your new rank: " + ChatColor.GREEN + nextEstate);
            thisEstate = null;
            nextEstate = null;
        } else {
            p.sendMessage(ChatColor.DARK_RED + "Insufficient CN to level up");
            return;
        }
        return;
    }
    public void firstEstate(Player player, String estate){
        // officer, miner, builder, trader
        switch(intEstate){
            case 0:
                if(!estate.equalsIgnoreCase(strOfficer[0])){
                    player.sendMessage(ChatColor.DARK_RED + "You need to be in the " + estate + " RankUp area to do this");
                    return;
                }
                break;
            case 1:
                if(!estate.equalsIgnoreCase(strMiner[0])){
                    player.sendMessage(ChatColor.DARK_RED + "You need to be in the " + estate + " RankUp area to do this");
                    return;
                }
                break;
            case 2:
                if(!estate.equalsIgnoreCase(strBuilder[0])){
                    player.sendMessage(ChatColor.DARK_RED + "You need to be in the " + estate + " RankUp area to do this");
                    return;
                }
                break;
            case 3:
                if(!estate.equalsIgnoreCase(strTrader[0])){
                    player.sendMessage(ChatColor.DARK_RED + "You need to be in the " + estate + " RankUp area to do this");
                    return;
                }
                break;
        }
        if(!acc.hasEnough(costCN)){
            player.sendMessage(ChatColor.DARK_RED + "Insufficient CN found");
            return;
        }
        if(player.getInventory().getItemInHand().getTypeId() != 83){
            player.sendMessage(ChatColor.DARK_RED + "Reed block not found. Please hold it in your hand");
            return;
        }
        if(func.addPlayer(player.getName(), estate)){
            acc.subtract(costCN);
            player.getInventory().setItemInHand(null);
            player.sendMessage(ChatColor.YELLOW + "Congratulations, your new estate is " + estate);
        } else {
            player.sendMessage(ChatColor.RED + "Failed to apply estate, please contact an admin");
        }
        Permissions.reload();
        thisEstate = null;
    }
}