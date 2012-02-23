package banper.mc;
import java.io.BufferedWriter;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

import net.minecraft.server.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.logging.Logger;
import org.bukkit.util.config.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;

import org.bukkit.util.config.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.*;
@SuppressWarnings("unused")
public class banper extends JavaPlugin {
	public static PermissionHandler permissionHandler;
    private String pluginPrefix;                                                            
    private String pluginName;            
	public String login;
	public String password;
	public String token;
	public String url;
    public final static Logger log = Logger.getLogger("Minecraft");        
    private final static String pluginFolder = "plugins/banper";  
    @SuppressWarnings("deprecation")
	private Configuration conf;    
    public static String itemName;
    //private GroupWorld permissions;                                                    
    private static Method method;
    private Plugin we;
	@Override
	public void onDisable() {
		
		log.info( pluginPrefix + " is disabled!" );
	}

	   
	   
	@Override
	public void onEnable() {
        pluginName = this.getDescription().getName();
        pluginPrefix = "[" + pluginName + "] ";
        log.info( pluginPrefix + "version " + this.getDescription().getVersion() +
        " by " + this.getDescription().getAuthors().toString() + " is enabled!" );
      Init();
      setupPermissions();
	}
    public void CreateDefaultConfig()
    {
            File f = new File( pluginFolder + "/config.yml" );
            BufferedWriter out;
            try {
                    out = new BufferedWriter(new FileWriter(f, true));
                    f.createNewFile();
                    out.write("logininfo:\r\n" +
                                            "       login: login\r\n" +
                                            "       password: password\r\n"+
                                            "       url: http://\r\n");
                    out.close();
            } catch (IOException e) {
                    log.warning( "Can't create default config!" );
                    this.setEnabled(false);
            }
    }
	@SuppressWarnings("deprecation")
    public void ReadConfig()
     {
             if( new File( pluginFolder ).exists() )
             {
                     File f = new File( pluginFolder + "/config.yml" );
                     if( f.exists() )
                     {
                             conf = new Configuration( f );
                             try
                             {
                                     conf.load();
                             } catch (Exception e) {
                                     log.warning( "Can't load " + pluginFolder +  "/config.yml. File wrong or have error, please verify or delete file!"  );
                                     this.setEnabled( false );
                             }
                             try
                             {
                                     ConfigurationNode logininfo = conf.getNode( "logininfo" );
                                     login = logininfo.getString("login", "");
                                     password = logininfo.getString("password", "");
                                     url = logininfo.getString("url", "");
                                     log.info( pluginPrefix + "Login: " + login );
                                     log.info( pluginPrefix + "Password: " + password );
                                     log.info( pluginPrefix + "Url: " + url );
                                     
                             } catch (Exception e) {
                                     log.warning( "System alert, while read values in config!" );
                             }
                     }
                     else
                     {
                             CreateDefaultConfig();
                     }
             }
             else
             {
                     log.info( pluginPrefix + "create plugin folder " + pluginFolder );
                     if( new File( pluginFolder ).mkdir() )
                     {
                             CreateDefaultConfig();
                     }
                     else
                     {
                             log.info( pluginPrefix + "Can't create plugin folder, please create yourself. Or verify you system rights." );
                             this.setEnabled( false );
                     }
             }
     }
    public void Init()
    {
            ReadConfig();
    }
    public class QueryString {
        private StringBuffer query;
     
        public QueryString() {
            query = new StringBuffer();
        }
     
        public synchronized QueryString add(Object name, Object value)
                    throws UnsupportedEncodingException {
            if (!query.toString().trim().equals("")) query.append("&");
            query.append(URLEncoder.encode(name.toString(), "UTF-8"));
            query.append("=");
            query.append(URLEncoder.encode(value.toString(), "UTF-8"));
            return this;
        }
     
        public String toString() {
            return query.toString();
        }
    }
    public static String excutePost(String targetURL, String urlParameters)
    {

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
    public static String createMD5(String raw)
    	   {
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
                                  
        
       
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
    String[] arguments = args;     
                                    
    String commandName = command.getName().toLowerCase();  
                                                            
    Player player = (Player) sender;        
 
    if ( commandName.equals("banper") && permissionHandler.has(player, "banper.banplayer") )
    {
                              
        
            World w = player.getWorld();

            if ((arguments[0] != null)&&(arguments[1] != null)) {
            token = createMD5(login+":"+password);
              }
    try {

        String parameters = "user=" + arguments[0] + "&per=" + arguments[1] + "&token=" + token + "&admin=" + player.getName();
        String result = excutePost(url, parameters);

            if (result != null) {
   player.sendMessage(result);
                         }
          } catch (Exception e) {
            e.printStackTrace();
        }
    }else
    {
  	 player.sendMessage( "error");
    }
	return false;
    }
	public static String getPluginfolder() {
		return pluginFolder;
	}

	@SuppressWarnings("deprecation")
	public Configuration getConf() {
		return conf;
	}

	public void setConf(@SuppressWarnings("deprecation") Configuration conf) {
		this.conf = conf;
	}
	private void setupPermissions() {
	    if (permissionHandler != null) {
	        return;
	    }
	    
	    Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
	    
	    if (permissionsPlugin == null) {
	        log.info("Permission system not detected, defaulting to OP");
	        return;
	    }
	    
	    permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	    log.info("Found and will use plugin "+((Permissions)permissionsPlugin).getDescription().getFullName());
	}

}
