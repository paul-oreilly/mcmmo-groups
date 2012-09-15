package com.oreilly.mmogroup;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.mcMMO;
import com.oreilly.mmogroup.api.PlayerAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionFactory;
import com.oreilly.mmogroup.bukkitTools.interaction.text.formatter.Border;
import com.oreilly.mmogroup.bukkitTools.interaction.text.formatter.ClearChat;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.AdminMenu;
import com.oreilly.mmogroup.interaction.MainMenu;
import com.oreilly.mmogroup.interaction.players.JoinGroup;
import com.oreilly.mmogroup.interaction.players.LeaveGroup;


public class MMOGroup extends JavaPlugin {
	
	public static MMOGroup instance = null;
	// vault permission api
	public static Permission permission = null;
	// reference to MCMMO
	public static mcMMO mcMMO = null;
	
	public static final String logPrefix = "[MMOGroup] ";
	
	public Config config = null;
	public Players players = null;
	public Groups groups = null;
	public IO io = null;
	public PluginLogger log = null;
	public Translations translations = null;
	public InteractionFactory playerInteractionFactory = null;
	public InteractionFactory adminInteractionFactory = null;
	
	
	public MMOGroup() {
		super();
		instance = this;
	}
	
	
	@Override
	public void onEnable() {
		
		// get our logger working
		log = new PluginLogger( this );
		
		// load config info
		config = new Config( this );
		
		// load the io handler
		io = new IO( this );
		
		// reset the default translation
		io.resetDefaultTranslation();
		
		// get reference to mcMMO
		// TODO: Null check etc
		mcMMO = (mcMMO)getServer().getPluginManager().getPlugin( "mcMMO" );
		
		// load up the main classes
		groups = new Groups( this );
		players = new Players( this );
		translations = new Translations( this );
		
		// load information from files
		io.loadAll();
		
		// register event listeners
		getServer().getPluginManager().registerEvents( new Events( this ), this );
		
		// link into vault for getting permission information
		loadPermissions();
		
		// set up event hook for interactions if required
		Interaction.registerEventListener( this );
		
		// create a new interface factory
		// no pages - those can be added depending on which command is used.
		playerInteractionFactory = new InteractionFactory()
				.withExitSequence( "exit", "quit" )
				.withReturnSequence( "return" )
				.withFormatter( new Border() )
				.withFormatter( new ClearChat() )
				.withTimeout( 20 )
				.withTranslation( translations.translater, "default" ); // TODO: Use value from config
		
		adminInteractionFactory = new InteractionFactory()
				.withExitSequence( "exit", "quit" )
				.withReturnSequence( "return" )
				.withFormatter( new Border() )
				.withFormatter( new ClearChat() )
				.withTimeout( 20 )
				.withTranslation( translations.translater, "default" )
				.withStyle( Border.COLOR_TITLE_TEXT, ChatColor.GOLD )
				.withStyle( Border.COLOR_TITLE_BORDER, ChatColor.DARK_RED )
				.withStyle( Border.COLOR_TEXT_BORDER, ChatColor.BLUE );
		
		// TODO: Commands
		
		// TODO: Event on player selecting group
		// TODO: Event listener on join success, including teleport if defined in config
		
	}
	
	
	@Override
	public void onDisable() {
		groups.onDisable();
		players.onDisable();
	}
	
	
	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args ) {
		if ( cmd.getName().equalsIgnoreCase( "mmogroup" ) ) {
			switch ( args.length ) {
				case 0:
					playerInteractionFactory.buildInteraction( sender )
							.withPages( new MainMenu() )
							.begin();
					return true;
				case 1: //TODO: Add help
					// TODO: Permission checks!
					/*if ( args[0].equalsIgnoreCase( "?" ) | args[1].equalsIgnoreCase( "help" ) ) {
						interactionFactory.buildInteraction( sender )
								.withPages( new Help() )
								.begin();
						return true;
					}*/
					if ( args[0].toLowerCase().trim().startsWith( "admin" ) ) {
						if ( permission.has( sender, PermissionConstants.admin ) ) {
							adminInteractionFactory.buildInteraction( sender )
									.withPages( new AdminMenu() )
									.begin();
							return true;
						}
						else
							return false;
					}
					if ( args[0].equalsIgnoreCase( "join" ) ) {
						if ( permission.has( sender, PermissionConstants.joinGroup ) ) {
							playerInteractionFactory.buildInteraction( sender )
									.withPages( new JoinGroup() )
									.begin();
							return true;
						} else
							return false;
					}
					if ( args[0].toLowerCase().trim().startsWith( "change" ) ) {
						if ( permission.has( sender, PermissionConstants.leaveGroup ) &
								permission.has( sender, PermissionConstants.joinGroup ) ) {
							playerInteractionFactory.buildInteraction( sender )
									.withPages( new LeaveGroup(), new JoinGroup() )
									.begin();
							return true;
						} else
							return false;
					}
					break;
				case 2:
					if ( args[0].equalsIgnoreCase( "join" ) ) {
						if ( sender instanceof Player )
							if ( permission.has( sender, PermissionConstants.joinGroup ) )
								try {
									PlayerAPI.joinGroup( (Player)sender, args[1] );
									return true;
								} catch ( PluginNotEnabled e ) {
									e.printStackTrace();
								}
					}
					if ( args[0].toLowerCase().trim().startsWith( "change" ) ) {
						if ( sender instanceof Player )
							if ( permission.has( sender, PermissionConstants.leaveGroup ) &
									permission.has( sender, PermissionConstants.joinGroup ) )
								try {
									PlayerAPI.changeGroup( (Player)sender, args[1] );
									return true;
								} catch ( PluginNotEnabled e ) {
									e.printStackTrace();
								}
					}
					break;
			}
			return false;
		} else
			return false;
	}
	
	
	private void loadPermissions() {
		Server server = getServer();
		if ( server.getPluginManager().getPlugin( "Vault" ) == null ) {
			log.warning( "[PermitMe] !! Loading of permissions failed, as vault plugin not found" );
			return;
		}
		RegisteredServiceProvider< Permission > provider =
				server.getServicesManager().getRegistration( Permission.class );
		if ( provider == null ) {
			log.warning( "[PermitMe] !! Loading of permissions failed, as vault service provider not registered" );
			return;
		}
		permission = provider.getProvider();
	}
	
}
