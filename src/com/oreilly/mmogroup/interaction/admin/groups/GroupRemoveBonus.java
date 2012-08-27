package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.WordUtils;

import com.gmail.nossr50.datatypes.SkillType;
import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupRemoveBonus extends InteractionPage {
	
	public GroupRemoveBonus() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		return helper.getVariables();
		
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError {
		Choices choices = new Choices( this, interaction );
		GroupHelper helper = new GroupHelper( interaction );
		// list all the skills for which this group doesn't have an existing bonus
		Set< SkillType > existingBonuses = helper.record.getSkillBonuses();
		for ( SkillType skill : existingBonuses )
			choices.addInternalChoice( WordUtils.capitalize( skill.toString() ), skill.toString() );
		// add a cancel choice
		choices.addInternalChoice( "Cancel", "cancel" );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String bonusName ) throws GeneralInteractionError {
		if ( bonusName.equalsIgnoreCase( "cancel" ) )
			return null;
		SkillType selected = SkillType.valueOf( bonusName );
		if ( selected == null )
			throw new GeneralInteractionError( "Unable to determine a skill type based on \"" + bonusName + "\"" );
		GroupHelper helper = new GroupHelper( interaction );
		try {
			GroupAPI.removeBonus( helper.record, selected );
			return "Bonus for " + bonusName.toLowerCase() + " has been removed";
		} catch ( PluginNotEnabled error ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
	
}
