package org.gamegen.banper; //Your package

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class banper extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	public String login;
	public String password;
	public String url;
	public String token;
	
	public void onDisable() {
		log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}
	
	public void onEnable() {
        log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
		login = this.getConfig().getString("login");
		password = this.getConfig().getString("password");
		url = this.getConfig().getString("url");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("banper")){
			PermissionUser user = PermissionsEx.getUser(player);
			if(user.has("banper.ban")) {
				if ((args[0] != null)&&(args[1] != null)) {
					token = createMD5(login+":"+password);
					String parameters = "user=" + args[0] + "&per=" + args[1] + "&token=" + token + "&admin=" + player.getName();
			        String result = excutePost(url, parameters);
					if (result != null) {
						player.sendMessage(ChatColor.RED+"[BANPER]"+result);
		            	log.info(result);
					}
					return true;
				}
			} else { player.sendMessage("access denied:has no permissions"); return true;}
		}
		return false; 
	}
	
    public static String createMD5(String raw){
	       String output = null;
	       try
	       {
	           MessageDigest md;
	           md = MessageDigest.getInstance("MD5");
	           md.update(raw.getBytes(), 0, raw.length());
	           output = new BigInteger(1, md.digest()).toString(16);
	       }
	       catch (NoSuchAlgorithmException e)
	       {
	           e.printStackTrace();
	       }
	       return output;
    }
	
	public static String excutePost(String targetURL, String urlParameters){

      HttpURLConnection connection = null;
      try
      {
        URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");

        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        connection.connect();
       
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        StringBuffer response = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null)
        {
          response.append(line);
          response.append('\r');
        }
        rd.close();

        String str1 = response.toString();
        return str1;
      }
      catch (Exception e)
      {
        e.printStackTrace();
        return null;
      }
      finally
      {
        if (connection != null)
          connection.disconnect();
      }
    }
}