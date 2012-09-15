package com.oreilly.mmogroup;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

import com.oreilly.mmogroup.bukkitTools.io.Numbers;


public class Config {
	
	File groupDataFile = null;
	File playerDataFile = null;
	File translationDirectory = null;
	File defaultTranslationFile = null;
	
	public Double specialisationBonus = (double)3;
	public boolean teleportOnJoin = false;
	public int minPowerLevelForSpecialisation = 0; // TODO: Add to settings dialog
	public String defaultTranslation = null; // TODO: ""
	public double defaultSkillBonusMultiplier = 2; // TODO: ""
	
	protected MMOGroup plugin = null;
	
	
	public Config( MMOGroup plugin ) {
		this.plugin = plugin;
		
		// make sure the config file is created, and up to date with current defaults
		plugin.saveDefaultConfig();
		FileConfiguration configFile = plugin.getConfig();
		configFile.options().copyDefaults( true );
		
		// create file paths
		groupDataFile = new File( plugin.getDataFolder() + File.separator + "groups.yml" );
		playerDataFile = new File( plugin.getDataFolder() + File.separator + "players.yml" );
		translationDirectory = new File( plugin.getDataFolder() + File.separator + "translations" );
		defaultTranslationFile = new File( translationDirectory + File.separator + "default.yml" );
		if ( !translationDirectory.exists() )
			translationDirectory.mkdirs();
		
		// load config variables
		String specialisationString = configFile.getString( ConfigConstants.specialisationBonus, "3" );
		Double specialisation = Numbers.doubleFromString( specialisationString );
		if ( specialisation != null )
			specialisationBonus = specialisation;
		teleportOnJoin = configFile.getBoolean( ConfigConstants.teleportOnJoiningGroup, true );
		minPowerLevelForSpecialisation = configFile.getInt( ConfigConstants.minPowerLevel, 0 );
		defaultTranslation = configFile.getString( ConfigConstants.defaultTranslation, "default" );
		defaultSkillBonusMultiplier = configFile.getDouble( ConfigConstants.defaultSkillBonusMultiplier, 2 );
		
		// TODO: Support command alias'
		
	}
	
	
	public void saveConfig() {
		FileConfiguration configFile = plugin.getConfig();
		configFile.set( ConfigConstants.specialisationBonus, Numbers.doubleAsPercentage( specialisationBonus, 2 ) );
		configFile.set( ConfigConstants.teleportOnJoiningGroup, teleportOnJoin );
		configFile.set( ConfigConstants.defaultTranslation, defaultTranslation );
		configFile.set( ConfigConstants.defaultSkillBonusMultiplier, defaultSkillBonusMultiplier );
	}
}


class ConfigConstants {
	
	static public final String specialisationBonus = "defaults.specialisationBonus";
	static public final String teleportOnJoiningGroup = "defaults.teleportOnJoiningGroup";
	static public final String minPowerLevel = "defaults.minimumPowerLevelForSpecialisation";
	static public final String defaultTranslation = "defaults.translationName";
	static public final String defaultSkillBonusMultiplier = "defaults.bonuses.multiplierForGroups";
	
}