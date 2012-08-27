package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.interaction.Constants;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupSelectSpeciality extends InteractionPage {
	
	public GroupSelectSpeciality() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError {
		Choices choices = new Choices( this, interaction );
		GroupHelper helper = new GroupHelper( interaction );
		// list all the skills for which this group doesn't have an existing bonus
		List< String > existingSpecial = helper.record.getSpecialityNames();
		for ( String name : existingSpecial ) {
			choices.addInternalChoice(
					name + " (" + WordUtils.capitalize( helper.record.getSpecialitySkill( name ).toString() ) + ")",
					name );
		}
		// add a 'cancel' choice as well
		choices.addInternalChoice( "Cancel", "cancel" );
		return choices;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		return helper.getVariables();
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String input ) throws GeneralInteractionError {
		if ( input.equalsIgnoreCase( "cancel" ) )
			return null;
		interaction.context.put( Constants.SELECTED_GROUP_SPECIAL, input );
		return "Selected " + input.toLowerCase();
	}
}
