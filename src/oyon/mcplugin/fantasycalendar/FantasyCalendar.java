package oyon.mcplugin.fantasycalendar;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import oyon.mcplugin.fantasycalendar.commands.FCAdd;

public class FantasyCalendar
  extends JavaPlugin
{
  public void onEnable()
  {
    Bukkit.getScheduler().runTaskTimer(this, new Runnable()
    {
      public void run()
      {
        FileConfiguration config = FantasyCalendar.this.getConfig();
        
        int newday = config.getInt("DateStore.current_day");
        
        int newyear = config.getInt("DateStore.current_year");
        
        List<String> serverSeasons = config.getStringList("Season");
        List<String> serverMonths = config.getStringList("Months." + config.getString("DateStore.current_season") + ".Month");
        if (Bukkit.getServer().getWorld(config.getString("world")).getTime() == 0L) {
          if (newday > config.getInt("MaxDaysPerMonth") - 1)
          {
            newday = 1;
            config.set("DateStore.current_day", Integer.valueOf(newday));
            int monthindex = config.getInt("DateStore.current_month") + 1;
            
            config.set("DateStore.current_month", Integer.valueOf(monthindex));
            FantasyCalendar.this.saveConfig();
            if (monthindex > serverMonths.size() - 1)
            {
              config.set("DateStore.current_month", Integer.valueOf(0));
              int seasonindex = config.getInt("DateStore.current_season") + 1;
              
              config.set("DateStore.current_season", Integer.valueOf(seasonindex));
              FantasyCalendar.this.saveConfig();
              if (seasonindex > serverSeasons.size() - 1)
              {
                config.set("DateStore.current_season", Integer.valueOf(0));
                config.set("DateStore.current_month", Integer.valueOf(0));
                config.set("DateStore.current_day", Integer.valueOf(1));
                
                newyear++;
                config.set("DateStore.current_year", Integer.valueOf(newyear));
                FantasyCalendar.this.saveConfig();
              }
            }
          }
          else
          {
            config.set("DateStore.current_day", Integer.valueOf(newday + 1));
            FantasyCalendar.this.saveConfig();
          }
        }
      }
    }, 0L, Long.MAX_VALUE);
    
    registerConfig();
    registerCommands();
  }
  
  public void onDisable() {}
  
  public void registerConfig()
  {
    getConfig().options().copyDefaults(true);
    saveDefaultConfig();
  }
  
  public void registerCommands()
  {
    getCommand("FC").setExecutor(new FCAdd(this));
  }
  
  public String getDate()
  {
    String season = (String)getConfig().getStringList("Season").get(getConfig().getInt("DateStore.current_season"));
    String month = (String)getConfig().getStringList("Months." + season + ".Month").get(getConfig().getInt("DateStore.current_month"));
    
    String date = getConfig().getString("date_format");
    
    date = date.replaceAll("%season", season);
    date = date.replaceAll("%month", month);
    date = date.replaceAll("%day", String.valueOf(getConfig().getInt("DateStore.current_day")));
    date = date.replaceAll("%year", String.valueOf(getConfig().getInt("DateStore.current_year")));
    
    return date;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    FileConfiguration c = getConfig();
    List<String> serverSeasons = c.getStringList("Season");
    Player player = (Player)sender;
    if (command.getName().equalsIgnoreCase("fcdate"))
    {
      if (!(sender instanceof Player))
      {
        player.sendMessage("You cannot execute this from console!");
        return false;
      }
      if ((args[0].equalsIgnoreCase("setdate")) && (player.hasPermission("fantasy.setdate")))
      {
        c.set("DateStore.current_season", Integer.valueOf(Integer.parseInt(args[1])));
        if (!serverSeasons.contains(serverSeasons.get(Integer.valueOf(args[1]).intValue())))
        {
          player.sendMessage("That season does not exist!");
          return false;
        }
        c.set("DateStore.current_month", Integer.valueOf(Integer.parseInt(args[2])));
        
        List<String> serverMonths = c.getStringList("Months." + (String)c.getStringList("Season").get(getConfig().getInt("DateStore.current_season")) + ".Month");
        if (!serverMonths.contains(serverMonths.get(Integer.valueOf(Integer.parseInt(args[2])).intValue())))
        {
          player.sendMessage("That month does not exist in this season!");
          return false;
        }
        c.set("DateStore.current_day", Integer.valueOf(Integer.parseInt(args[3])));
        if (Integer.valueOf(Integer.parseInt(args[3])).intValue() > c.getInt("MaxDaysPerMonth"))
        {
          player.sendMessage("Number exceeds max days per month");
          return false;
        }
        c.set("DateStore.current_year", Integer.valueOf(Integer.parseInt(args[4])));
        saveConfig();
        player.sendMessage("You have set date to: " + getDate());
        return true;
      }
      if ((args[0].equalsIgnoreCase("date")) && (player.hasPermission("fantasy.date")))
      {
        player.sendMessage(getDate());
        return true;
      }
      if ((args[0].equalsIgnoreCase("time")) && (player.hasPermission("fantasy.time")))
      {
        long cmdtime = Bukkit.getServer().getWorld(c.getString("world")).getTime();
        player.sendMessage(Long.toString(cmdtime));
        return true;
      }
      return true;
    }
    return false;
  }
}
