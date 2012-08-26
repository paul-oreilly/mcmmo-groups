package com.oreilly.mmogroup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;

import com.gmail.nossr50.datatypes.SkillType;


public class GroupRecord {
	
	String name;
	HashMap< SkillType, Double > bonuses = new HashMap< SkillType, Double >();
	// TODO: Add "Min power level" to match each speciality skill later
	HashMap< String, Speciality > specialitiesByName = new HashMap< String, Speciality >();
	HashMap< SkillType, Speciality > specialitiesBySkill = new HashMap< SkillType, Speciality >();
	//LinkedList< SkillType > specialityOptions = new LinkedList< SkillType >();
	Location teleportLocation = null;
	boolean teleportOnJoin = false;
	
	protected MMOGroup plugin;
	
	
	public GroupRecord( String name, MMOGroup plugin ) {
		this.name = name;
		this.plugin = plugin;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public void changeName( String newName ) {
		name = newName;
		plugin.groups._internal_GroupRecordUpdate( this );
	}
	
	
	public void addSpecialityOption( String name, SkillType type, int requiredPowerLevel, double skillFactor ) {
		if ( specialitiesByName.containsKey( name ) )
			return;
		if ( specialitiesBySkill.containsKey( type ) )
			return;
		else {
			Speciality special = new Speciality( name, type, requiredPowerLevel, skillFactor );
			specialitiesByName.put( name, special );
			specialitiesBySkill.put( type, special );
			plugin.groups._internal_GroupRecordUpdate( this );
		}
	}
	
	
	public boolean removeSpecialityOption( String name ) {
		Speciality special = specialitiesByName.remove( name );
		if ( special != null ) {
			specialitiesBySkill.remove( special.skill );
			plugin.groups._internal_GroupRecordUpdate( this );
			return true;
		} else
			return false;
	}
	
	
	// TODO: Add to API
	public boolean removeSpecialityOption( SkillType skill ) {
		Speciality special = specialitiesBySkill.remove( skill );
		if ( special != null ) {
			specialitiesByName.remove( special.name );
			plugin.groups._internal_GroupRecordUpdate( this );
			return true;
		} else
			return false;
	}
	
	
	public List< String > getSpecialityNames() {
		LinkedList< String > result = new LinkedList< String >();
		result.addAll( specialitiesByName.keySet() );
		return result;
	}
	
	
	//TODO: Add to API
	public String getSpecialityName( SkillType skill ) {
		Speciality special = specialitiesBySkill.get( skill );
		if ( special != null )
			return special.name;
		else
			return null;
	}
	
	
	public boolean setSpecialityName( String currentName, String newName ) {
		Speciality special = specialitiesByName.remove( currentName );
		if ( special == null )
			return false;
		special.name = newName;
		specialitiesByName.put( newName, special );
		plugin.groups._internal_GroupRecordUpdate( this );
		return true;
	}
	
	
	public Set< SkillType > getSpecialitySkills() {
		HashSet< SkillType > result = new HashSet< SkillType >();
		result.addAll( specialitiesBySkill.keySet() );
		return result;
	}
	
	
	public int getSpecialityRequiredPowerLevel( String name ) {
		Speciality special = specialitiesByName.get( name );
		if ( special == null )
			return -1;
		else
			return special.reqPowerLevel;
	}
	
	
	public boolean setSpecialityRequiredPowerLevel( String name, int requiredPowerLevel ) {
		Speciality special = specialitiesByName.get( name );
		if ( special == null )
			return false;
		else {
			special.reqPowerLevel = requiredPowerLevel;
			plugin.groups._internal_GroupRecordUpdate( this );
			return true;
		}
	}
	
	
	public SkillType getSpecialitySkill( String name ) {
		Speciality special = specialitiesByName.get( name );
		if ( special == null )
			return null;
		else
			return special.skill;
	}
	
	
	public boolean setSpecialitySkill( String name, SkillType skill ) {
		Speciality special = specialitiesByName.get( name );
		if ( special == null )
			return false;
		else {
			special.skill = skill;
			plugin.groups._internal_GroupRecordUpdate( this );
			return true;
		}
	}
	
	
	public double getSpecialitySkillFactor( String name ) {
		Speciality special = specialitiesByName.get( name );
		if ( special == null )
			return -1;
		else
			return special.skillFactor;
	}
	
	
	public boolean setSpecialitySkillFactor( String name, double skillFactor ) {
		Speciality special = specialitiesByName.get( name );
		if ( special == null )
			return false;
		else {
			special.skillFactor = skillFactor;
			plugin.groups._internal_GroupRecordUpdate( this );
			return true;
		}
	}
	
	
	public void addSkillBonus( SkillType type, double amount ) {
		bonuses.put( type, amount );
		plugin.groups._internal_GroupRecordUpdate( this );
	}
	
	
	public void setSkillBonusAmount( SkillType skill, double amount ) {
		addSkillBonus( skill, amount );
	}
	
	
	public Set< SkillType > getSkillBonuses() {
		HashSet< SkillType > result = new HashSet< SkillType >();
		result.addAll( bonuses.keySet() );
		return result;
	}
	
	
	public double getSkillBonus( SkillType type ) {
		return bonuses.get( type );
	}
	
	
	public void removeSkillBonus( SkillType type ) {
		bonuses.remove( type );
		plugin.groups._internal_GroupRecordUpdate( this );
	}
	
	
	public Location getTeleportLocation() {
		return teleportLocation;
	}
	
	
	public void addTeleportationLocation( Location location ) {
		teleportLocation = location;
		plugin.groups._internal_GroupRecordUpdate( this );
	}
	
	
	public void removeTeleportLocation() {
		teleportLocation = null;
		plugin.groups._internal_GroupRecordUpdate( this );
	}
	
	
	public void remove() {
		plugin.groups.removeGroup( this );
	}
	
	
	public boolean getAutoTeleport() {
		return teleportOnJoin;
	}
	
	
	public void setAutoTeleport( boolean value ) {
		teleportOnJoin = value;
		plugin.groups._internal_GroupRecordUpdate( this );
	}
}


class Speciality {
	
	public SkillType skill = null;
	public int reqPowerLevel = 0;
	public String name;
	public double skillFactor = 0;
	
	
	public Speciality( String name, SkillType skill, int requriedPowerLevel, double skillFactor ) {
		this.name = name;
		this.skill = skill;
		this.reqPowerLevel = requriedPowerLevel;
		this.skillFactor = skillFactor;
	}
}
