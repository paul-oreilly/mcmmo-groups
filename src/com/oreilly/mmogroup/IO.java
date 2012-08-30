package com.oreilly.mmogroup;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.WordUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.nossr50.datatypes.SkillType;
import com.oreilly.mmogroup.bukkitTools.io.Locations;
import com.oreilly.mmogroup.bukkitTools.io.Numbers;
import com.oreilly.mmogroup.bukkitTools.io.Yaml;


public class IO {
	
	class SkillBonus {
		
		SkillType type;
		Double amount;
		
		
		public SkillBonus( SkillType type, double amount ) {
			this.type = type;
			this.amount = amount;
		}
		
		
		public String toSaveString() {
			return WordUtils.capitalizeFully( type.toString() ) + " " + Numbers.doubleAsPercentage( amount, 2 );
		}
		
	}
	
	protected MMOGroup plugin = null;
	
	
	public IO( MMOGroup plugin ) {
		this.plugin = plugin;
	}
	
	
	public void loadAll() {
		loadGroupRecords();
		loadPlayerRecords();
	}
	
	
	public void loadGroupRecords() {
		FileConfiguration config = Yaml.loadYamlFile( plugin.config.groupDataFile, plugin.log );
		if ( config != null ) {
			for ( String fileKey : config.getKeys( false ) ) {
				// get the primary data
				ConfigurationSection section = config.getConfigurationSection( fileKey );
				String groupName = section.getString( GroupRecordConstant.name );
				if ( groupName == null )
					continue; // TODO: Throw error
				GroupRecord record = new GroupRecord( groupName, plugin );
				// get other data
				record.teleportLocation = Locations.fromString(
						section.getString( GroupRecordConstant.teleportLocation ),
						fileKey + " in " + plugin.config.groupDataFile.getAbsolutePath(),
						plugin.log );
				record.teleportOnJoin = section.getBoolean( GroupRecordConstant.teleportOnJoin, false );
				List< String > bonusRecords = section.getStringList( GroupRecordConstant.bonuses );
				if ( bonusRecords != null )
					for ( String bonusRecord : bonusRecords ) {
						SkillBonus bonus = skillBonusFromString( bonusRecord,
								plugin.config.groupDataFile.getAbsolutePath(), plugin.log );
						if ( bonus != null )
							record.addSkillBonus( bonus.type, bonus.amount );
					}
				// add the group to the system
				plugin.groups.addGroup( record );
			}
		}
	}
	
	
	public void loadPlayerRecords() {
		FileConfiguration config = Yaml.loadYamlFile( plugin.config.playerDataFile, plugin.log );
		if ( config != null ) {
			for ( String fileKey : config.getKeys( false ) ) {
				// get the primary data
				ConfigurationSection section = config.getConfigurationSection( fileKey );
				String playerName = section.getString( PlayerRecordConstant.name );
				PlayerRecord player = new PlayerRecord( plugin, playerName );
				player.groupName = config.getString( PlayerRecordConstant.groupName );
				player.specialisation = config.getString( PlayerRecordConstant.specialisation );
				// add the player to the system
				plugin.players.addPlayer( player );
			}
		}
	}
	
	
	public void savePlayerRecord( PlayerRecord record ) {
		saveAllPlayers();
	}
	
	
	public void saveGroupRecord( GroupRecord record ) {
		saveAllGroups();
	}
	
	
	public void saveAllGroups() {
		FileConfiguration config = Yaml.loadYamlFile( plugin.config.groupDataFile, plugin.log );
		if ( config != null ) {
			for ( GroupRecord record : plugin.groups.groups.values() ) {
				String fileKey = record.name;
				config.set( fileKey + "." + GroupRecordConstant.name, record.name );
				if ( record.teleportLocation != null )
					config.set( fileKey + "." + GroupRecordConstant.teleportLocation,
							record.teleportLocation.toString() );
				config.set( fileKey + "." + GroupRecordConstant.teleportOnJoin, record.teleportOnJoin );
				if ( record.bonuses != null )
					if ( record.bonuses.size() > 0 ) {
						List< String > bonusRecords = new LinkedList< String >();
						for ( SkillType type : record.bonuses.keySet() )
							bonusRecords.add( new SkillBonus( type, record.bonuses.get( type ) ).toSaveString() );
						config.set( fileKey + "." + GroupRecordConstant.bonuses, bonusRecords );
					}
			}
		}
		try {
			config.save( plugin.config.groupDataFile );
		} catch ( IOException e ) {
			plugin.log.severe( "IO Error while saving group data: " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	
	public void saveAllPlayers() {
		FileConfiguration config = Yaml.loadYamlFile( plugin.config.playerDataFile, plugin.log );
		if ( config != null ) {
			for ( PlayerRecord player : plugin.players.players.values() ) {
				String fileKey = player.name;
				config.set( fileKey + "." + PlayerRecordConstant.name, player.name );
				if ( player.groupName != null )
					config.set( fileKey + "." + PlayerRecordConstant.groupName, player.groupName );
				if ( player.specialisation != null )
					config.set( fileKey + "." + PlayerRecordConstant.specialisation, player.specialisation );
			}
		}
		try {
			config.save( plugin.config.playerDataFile );
		} catch ( IOException e ) {
			plugin.log.severe( "IO Error while saving player data: " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	
	public void saveAll() {
		saveAllGroups();
		saveAllPlayers();
	}
	
	
	// data functions
	SkillBonus skillBonusFromString( String data, String errorLocation, Logger log ) {
		// expected format is "[Name] xx.xx%"
		if ( data == null )
			return null;
		String[] splitString = data.split( " " );
		if ( splitString.length != 2 ) {
			log.warning( "Incorrect format - expected \"[skill name] [bonus amount]\" in " + errorLocation +
					" found \"" + data + "\"" );
			return null;
		}
		String skillName = splitString[0].trim().toUpperCase();
		SkillType type = SkillType.valueOf( SkillType.class, skillName );
		if ( type == null ) {
			log.warning( "Unable to determine skill type \"" + skillName + "\" in " + errorLocation );
			return null;
		}
		Double amount = Numbers.doubleFromString( splitString[1] );
		if ( amount == null ) {
			log.warning( "Unable to determine an amount in " + errorLocation + " from \"" + splitString[1] + "\"" );
			return null;
		}
		return new SkillBonus( type, amount );
	}
	
}


class GroupRecordConstant {
	
	static public final String name = "name";
	static public final String teleportLocation = "teleport.location";
	static public final String teleportOnJoin = "teleport.teleportOnJoin";
	static public final String bonuses = "bonuses";
}


class PlayerRecordConstant {
	
	static public final String name = "name";
	static public final String groupName = "group";
	static public final String specialisation = "specialisation";
}