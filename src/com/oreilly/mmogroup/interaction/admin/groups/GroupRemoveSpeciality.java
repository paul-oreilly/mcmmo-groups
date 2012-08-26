package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;
import java.util.List;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.AbortInteraction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralInteractionError;
import com.oreilly.common.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupRemoveSpeciality extends InteractionPage {
	
	public GroupRemoveSpeciality() {
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
		List< String > specialityNames = helper.record.getSpecialityNames();
		for ( String name : specialityNames ) {
			choices.addInternalChoice( name + "(" + helper.record.getSpecialitySkill( name ).toString().toLowerCase() +
					")", name );
		}
		// add a cancel choice
		choices.addInternalChoice( "Cancel", "cancel" );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String input ) throws GeneralInteractionError {
		if ( input.equalsIgnoreCase( "cancel" ) )
			return null;
		try {
			// check if the speciality exists...
			GroupHelper helper = new GroupHelper( interaction );
			List< String > specialityNames = helper.record.getSpecialityNames();
			input = input.trim();
			for ( String name : specialityNames )
				if ( input.equalsIgnoreCase( name ) ) {
					GroupAPI.removeSpeciality( helper.record, name );
					return "Speciality " + name + " has been removed";
				}
			// failure..
			interaction.pageWaitingForInput = true;
			return "Unable to determine a speciality based on \"" + input + "\"";
		} catch ( PluginNotEnabled error ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
	
}
