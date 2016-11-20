package oyon.mcplugin.fantasycalendar.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import oyon.mcplugin.fantasycalendar.FantasyCalendar;

public class FCAdd
  implements CommandExecutor
{
  private FantasyCalendar plugin;
  
  public FCAdd(FantasyCalendar pl)
  {
    this.plugin = pl;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    List<String> serverSeasons = this.plugin.getConfig().getStringList("Season");
    
    Player player = (Player)sender;
    if (label.equalsIgnoreCase("FC"))
    {
      if (!(sender instanceof Player))
      {
        player.sendMessage("You cannot execute this from console!");
        return false;
      }
      if ((args[0].equalsIgnoreCase("add")) && (player.hasPermission("fantasy.add")))
      {
        if (args[1].equalsIgnoreCase("Season"))
        {
          if (serverSeasons.contains(args[2]))
          {
            player.sendMessage("That season already exists!");
            return false;
          }
          player.sendMessage("You added " + args[2] + " as a season.");
          serverSeasons.add(args[2]);
          this.plugin.getConfig().set("Season", serverSeasons);
          this.plugin.getConfig().set("Months." + args[2] + ".Month", "");
          this.plugin.saveConfig();
        }
        if (args[1].equalsIgnoreCase("Month"))
        {
          List<String> serverMonths = this.plugin.getConfig().getStringList("Months." + args[3] + ".Month");
          if (serverMonths.contains(args[2]))
          {
            player.sendMessage("That month already exists!");
            return false;
          }
          player.sendMessage("You added " + args[2] + " as a month in season " + args[3]);
          serverMonths.add(args[2]);
          this.plugin.getConfig().set("Months." + args[3] + ".Month", serverMonths);
          this.plugin.saveConfig();
        }
      }
      if ((args[0].equalsIgnoreCase("list")) && (player.hasPermission("fantasy.list")))
      {
        if (args[1].equalsIgnoreCase("Season")) {
          for (String seasons : serverSeasons) {
            player.sendMessage(seasons);
          }
        }
        if (args[1].equalsIgnoreCase("Months"))
        {
          List<String> serverMonths = this.plugin.getConfig().getStringList("Months." + args[2] + ".Month");
          for (String monthname : serverMonths) {
            player.sendMessage(monthname);
          }
        }
      }
      if ((args[0].equalsIgnoreCase("reload")) && (player.hasPermission("fantasy.reload"))) {
        this.plugin.reloadConfig();
      }
      if (args[0].equalsIgnoreCase("help"))
      {
        player.sendMessage("/fc add season [Season Name]: Adds a season to the config");
        player.sendMessage("/fc add month [Month Name] [Season Name]: Adds a month to that season in the config");
        player.sendMessage("/fc list months [Season Name]: Lists all months within that season");
        player.sendMessage("/fc list season: Lists all seasons");
        player.sendMessage("/fcdate setdate [Season #] [Month #] [day] [year]: sets date (All lists start with 0)");
        player.sendMessage("/fcdate time: Shows time in ticks");
        player.sendMessage("/fcdate date: shows current date");
        
        return true;
      }
      return true;
    }
    return true;
  }
}
