package com.oreilly.mmogroup.interaction.admin;

import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.Constants;


// confirmation dialog after selecting a group - "Are you sure you want to delete the group x?"
public class DeleteGroup extends InteractionPage {
	
	public DeleteGroup() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, ContextDataRequired,
			GeneralInteractionError, PageFailure {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		choices.addInternalChoice( variable.define( "yes" ), "yes" ).withAlias( "yes" ).withAlias( "y" );
		choices.addInternalChoice( variable.define( "no" ), "no" ).withAlias( "no" ).withAlias( "n" );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String decision ) throws ContextDataRequired,
			GeneralInteractionError {
		if ( decision.contentEquals( "yes" ) ) {
			try {
				GroupAPI.deleteGroup( interaction.getContextData( String.class, interaction,
						Constants.SELECTED_GROUP ) );
			} catch ( PluginNotEnabled e ) {
				e.printStackTrace();
				throw new GeneralInteractionError( "Plugin not enabled" );
			}
			return "Record deleted.";
		} else
			return "Deletion cancelled.";
	}
	
}
