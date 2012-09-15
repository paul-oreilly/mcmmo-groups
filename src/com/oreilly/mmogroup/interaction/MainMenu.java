package com.oreilly.mmogroup.interaction;

import java.util.HashMap;

import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.PermissionConstants;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.interaction.helpers.PlayerHelper;
import com.oreilly.mmogroup.interaction.players.ChooseSpeciality;
import com.oreilly.mmogroup.interaction.players.JoinGroup;
import com.oreilly.mmogroup.interaction.players.LeaveGroup;


public class MainMenu extends InteractionPage {
	
	public MainMenu() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		PlayerHelper helper = new PlayerHelper( interaction.user );
		return helper.getVariables();
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, PageFailure,
			ContextDataRequired, GeneralInteractionError {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		// depending on user, the choices change...
		PlayerHelper helper = new PlayerHelper( interaction );
		if ( helper.playersGroup == null ) {
			// no group at all? Their only option is to join one.. if they have permission to.
			if ( MMOGroup.permission.has( interaction.user, PermissionConstants.joinGroup ) ) {
				choices.addPageChoice( variable.define( "join_group" ), new JoinGroup() );
			} else
				// abort the conversation
				throw new AbortInteraction( variable.define( "error.permissions.joinGroup" ) );
		} else if ( MMOGroup.permission.has( interaction.user, PermissionConstants.changeGroup ) ) {
			choices.addPageChoice( variable.define( "change_group" ), new LeaveGroup(), new JoinGroup() );
		} else if ( MMOGroup.permission.has( interaction.user, PermissionConstants.leaveGroup ) ) {
			choices.addPageChoice( variable.define( "leave_group" ), new LeaveGroup() );
		}
		// next check, can they specialise?
		if ( MMOGroup.permission.has( interaction.user, PermissionConstants.specialise ) ) {
			if ( helper.playerRecord.getSpecialisation() == null ) {
				if ( helper.getEligableSpecialityOptions().size() > 0 ) {
					choices.addPageChoice( variable.define( "specialise" ), new ChooseSpeciality() );
				}
			}
		}
		// If there are no choices by this point, then throw a PageFailure..
		if ( choices.getChoiceCount() == 0 )
			throw new PageFailure( "There are no actions that you can take right now" );
		// provide an obvious exit choice
		choices.addAbortChoice( variable.define( "cancel" ) );
		return choices;
	}
}
