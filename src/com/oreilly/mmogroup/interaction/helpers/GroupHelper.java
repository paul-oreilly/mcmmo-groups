package com.oreilly.mmogroup.interaction.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;

import com.gmail.nossr50.datatypes.SkillType;
import com.oreilly.mmogroup.GroupRecord;
import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.io.Numbers;
import com.oreilly.mmogroup.interaction.Constants;


public class GroupHelper {
	
	public GroupRecord record = null;
	public SkillType selectedSkill = null;
	public SkillType selectedSpecialitySkill = null;
	public String selectedSpecialityName = null;
	
	
	// DBEUG of entire class!
	
	public GroupHelper( Interaction fromContext ) {
		try {
			String groupName = fromContext.getContextData( String.class, fromContext, Constants.SELECTED_GROUP, false );
			if ( groupName != null )
				init( groupName, fromContext );
		} catch ( ContextDataRequired error ) {
		}
	}
	
	
	public GroupHelper( String groupName ) {
		init( groupName, null );
	}
	
	
	public GroupHelper( GroupRecord record ) {
		init( record, null );
	}
	
	
	private void init( String groupName, Interaction fromContext ) {
		if ( groupName != null ) {
			GroupRecord record = MMOGroup.instance.groups.getGroup( groupName );
			if ( record != null )
				init( record, fromContext );
		}
	}
	
	
	private void init( GroupRecord record, Interaction fromContext ) {
		this.record = record;
		if ( fromContext != null ) {
			try {
				String skillName = fromContext.getContextData(
						String.class, fromContext, Constants.SELECTED_GROUP_SKILL, false );
				if ( skillName != null )
					selectedSkill = SkillType.valueOf( skillName );
				selectedSpecialityName = fromContext.getContextData(
						String.class, fromContext, Constants.SELECTED_GROUP_SPECIAL, false );
				if ( selectedSpecialityName != null )
					selectedSpecialitySkill = record.getSpecialitySkill( selectedSpecialityName );
			} catch ( ContextDataRequired error )
			{
			}
		}
	}
	
	
	public HashMap< String, Object > getVariables() {
		HashMap< String, Object > variables = new HashMap< String, Object >();
		if ( record == null ) {
			System.out.println( "WARNING! Record is null!" );
			return variables;
		}
		variables.put( "group_name", record.getName() );
		Set< SkillType > bonuses = record.getSkillBonuses();
		ArrayList< String > formattedBonuses = new ArrayList< String >();
		for ( SkillType skill : bonuses )
			formattedBonuses.add( WordUtils.capitalizeFully( skill.toString() ) );
		variables.put( "group_bonus_line", StringUtils.join( formattedBonuses, ", " ) );
		variables.put( "group_bonus_list", StringUtils.join( formattedBonuses, "\n" ) );
		ArrayList< String > detailedBonuses = new ArrayList< String >();
		for ( SkillType skill : bonuses )
			detailedBonuses.add( WordUtils.capitalizeFully( skill.toString() ) +
					" (" + Numbers.doubleAsPercentage( record.getSkillBonus( skill ) ) + ")" );
		variables.put( "group_detailed_bonuses_line", StringUtils.join( detailedBonuses, ", " ) );
		variables.put( "group_detailed_bonsues_list", StringUtils.join( detailedBonuses, "\n" ) );
		List< String > specialityNames = record.getSpecialityNames();
		variables.put( "group_special_line", StringUtils.join( specialityNames, ", " ) );
		variables.put( "group_special_list", StringUtils.join( specialityNames, "\n" ) );
		Location teleportLocation = record.getTeleportLocation();
		if ( teleportLocation != null )
			variables.put( "group_teleport_location",
					teleportLocation.getWorld().getName() + " x" +
							teleportLocation.getBlockX() + ", y" + teleportLocation.getBlockY() + " z" +
							teleportLocation.getBlockZ() );
		else
			variables.put( "group_teleport_location", "none" );
		// TODO: Variables for the currently selected speciality (name, skill, bonus amount..)
		if ( selectedSpecialityName != null ) {
			variables.put( "group_selected_speciality", selectedSpecialityName );
			variables.put( "group_selected_speciality_skill", selectedSpecialitySkill );
		}
		// TODO: "AutoTeleport" status (enabled/disabled)
		return variables;
	}
}
