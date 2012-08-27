package com.oreilly.mmogroup.api;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;

import com.gmail.nossr50.datatypes.SkillType;
import com.oreilly.mmogroup.GroupRecord;
import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.bukkitTools.io.Numbers;
import com.oreilly.mmogroup.errors.PluginNotEnabled;


// TODO: New error type to add -> "Record does not exist"

public class GroupAPI {
	
	static public boolean DEBUG = true;
	
	
	static public boolean changeName( String currentName, String newName ) throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( currentName );
		if ( record != null )
			return changeName( record, newName );
		else
			return false;
	}
	
	
	static public boolean changeName( GroupRecord record, String newName ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: Change of group name, from " + record.getName() + " to " + newName );
		if ( record != null ) {
			record.changeName( newName );
			return true;
		} else
			return false;
	}
	
	
	static public GroupRecord createGroup( String groupName ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: Create group " + groupName );
		GroupRecord group = new GroupRecord( groupName, MMOGroup.instance );
		MMOGroup.instance.groups.addGroup( group );
		return group;
	}
	
	
	static public GroupRecord getGroup( String groupName ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: getGroup for " + groupName );
		return MMOGroup.instance.groups.getGroup( groupName );
	}
	
	
	static public Set< String > getAllGroupNames() throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: getAllGroupNames" );
		return MMOGroup.instance.groups.getAllGroupNames();
	}
	
	
	static public List< GroupRecord > getAllGroups() throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: getAllGroups" );
		return MMOGroup.instance.groups.getAllGroups();
	}
	
	
	static public void deleteGroup( String groupName ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: deleteGroup for " + groupName );
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		if ( record != null )
			record.remove();
	}
	
	
	static public void deleteGroup( GroupRecord record ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: deleteGroup for " + record.getName() );
		record.remove();
	}
	
	
	static public void addBonus( String groupName, SkillType skill, double multiplier ) throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		addBonus( record, skill, multiplier );
	}
	
	
	static public void addBonus( GroupRecord record, SkillType skill, double multiplier ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: addBonus for " + record.getName() +
					" -> " + skill.toString() + " at " + Numbers.doubleAsPercentage( multiplier, 2 ) );
		record.addSkillBonus( skill, multiplier );
	}
	
	
	static public Set< SkillType > getBonuses( String groupName ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: getBonuses for " + groupName );
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return record.getSkillBonuses();
	}
	
	
	static public boolean removeBonus( String groupName, SkillType skill ) throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return removeBonus( record, skill );
	}
	
	
	static public boolean removeBonus( GroupRecord record, SkillType skill ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: removeBonus for " + record.getName() + " -> " + skill.toString() );
		record.removeSkillBonus( skill );
		return true; //TODO: Add boolean return to group record
	}
	
	
	static public boolean setBonusAmount( String groupName, String skillName, double amount ) throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		SkillType skill = SkillType.valueOf( skillName );
		if ( ( record == null ) | ( skill == null ) )
			return false;
		else
			return setBonusAmount( record, skill, amount );
	}
	
	
	static public boolean setBonusAmount( GroupRecord record, SkillType skill, double amount ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: setBonusAmount for " + record.getName() +
					", to set " + skill.toString() + " to " + Numbers.doubleAsPercentage( amount, 2 ) );
		if ( ( record != null ) & ( skill != null ) ) {
			record.setSkillBonusAmount( skill, amount );
			return true;
		} else
			return false;
	}
	
	
	static public void addSpecialityOption( String groupName, SkillType skill,
			String specialityName, int requiredPowerLevel, double experienceFactor ) throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		addSpecialityOption( record, skill, specialityName, requiredPowerLevel, experienceFactor );
	}
	
	
	static public void addSpecialityOption( GroupRecord record, SkillType skill,
			String specialityName, int requiredPowerLevel, double experienceFactor ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: addSpecialityOption for " + record.getName() + " -> " +
					skill.toString() );
		
		// TODO: Throw error on these functions if group is null
		record.addSpecialityOption( specialityName, skill, requiredPowerLevel, experienceFactor );
	}
	
	
	static public boolean removeSpeciality( String groupName, String specialityName ) throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return removeSpeciality( record, specialityName );
	}
	
	
	static public boolean removeSpeciality( GroupRecord record, String specialityName ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: removeSpeciality for " + record.getName() + ":" + specialityName );
		return record.removeSpecialityOption( specialityName );
	}
	
	
	static public List< String > getSpecialityNames( String groupName ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: getSpecialityNames for " + groupName );
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return record.getSpecialityNames();
	}
	
	
	static public Set< SkillType > getSpecialitySkills( String groupName ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: getSpecialitySkills for " + groupName );
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return record.getSpecialitySkills();
	}
	
	
	static public Double getSkillBonus( String groupName, SkillType skill ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: getSkillBonus for " + groupName + " -> " + skill.toString() );
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return record.getSkillBonus( skill );
	}
	
	
	static public boolean changeSpecialitySkill( String groupName, String specialityName, SkillType skill )
			throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return changeSpecialitySkill( record, specialityName, skill );
	}
	
	
	static public boolean changeSpecialitySkill( GroupRecord record, String specialityName, SkillType skill )
			throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: changeSpecialitySkill for " + record.getName() + ":" +
					specialityName + " -> " + skill.toString() );
		return record.setSpecialitySkill( specialityName, skill );
	}
	
	
	static public boolean changeSpecialityName( String groupName, String specialityName, String newName )
			throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return changeSpecialityName( record, specialityName, newName );
	}
	
	
	static public boolean changeSpecialityName( GroupRecord record, String specialityName, String newName )
			throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: changeSpecialityName for " + record.getName() + ":" +
					specialityName + " -> " + newName );
		return record.setSpecialityName( specialityName, newName );
	}
	
	
	static public boolean changeSpecialityFactor( String groupName, String specialityName, double factor )
			throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return changeSpecialityFactor( record, specialityName, factor );
	}
	
	
	static public boolean changeSpecialityFactor( GroupRecord record, String specialityName, double factor )
			throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: changeSpecialityFactor for " + record.getName() + ":" +
					specialityName + " -> " + Numbers.doubleAsPercentage( factor, 2 ) );
		return record.setSpecialitySkillFactor( specialityName, factor );
	}
	
	
	static public Location getTeleportLocation( String groupName ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: getTeleportLocation for " + groupName );
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return record.getTeleportLocation();
	}
	
	
	static public void removeTeleportLocation( String groupName ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: removeTeleportLocation for " + groupName );
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		record.removeTeleportLocation();
	}
	
	
	static public void setTeleportLocation( String groupName, Location location ) throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		setTeleportLocation( record, location );
	}
	
	
	static public void setTeleportLocation( GroupRecord record, Location location ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: setTeleportLocation for " + record.getName() + " -> " +
					location.toString() );
		record.addTeleportationLocation( location );
	}
	
	
	static public boolean getAutoTeleport( String groupName ) throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return getAutoTeleport( record );
	}
	
	
	static public boolean getAutoTeleport( GroupRecord record ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: getAutoTeleport for " + record.getName() );
		return record.getAutoTeleport();
	}
	
	
	static public boolean setAutoTeleport( String groupName, boolean value ) throws PluginNotEnabled {
		checkEnabled();
		GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
		return setAutoTeleport( record, value );
	}
	
	
	static public boolean setAutoTeleport( GroupRecord record, boolean value ) throws PluginNotEnabled {
		checkEnabled();
		if ( DEBUG )
			MMOGroup.instance.log.finer( "API Call: setAutoTeleport (" + value + ") for " + record.getName() );
		record.setAutoTeleport( value );
		return true;
	}
	
	
	// Internal functions
	
	static private void checkEnabled() throws PluginNotEnabled {
		if ( MMOGroup.instance == null )
			throw new PluginNotEnabled();
	}
}
