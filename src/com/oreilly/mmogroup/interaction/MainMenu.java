package com.oreilly.mmogroup.interaction;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.AbortInteraction;
import com.oreilly.common.interaction.text.helpers.Choices;
import com.oreilly.common.text.VariablePrefixer;
import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.PermissionConstants;
import com.oreilly.mmogroup.interaction.helpers.PlayerHelper;
import com.oreilly.mmogroup.interaction.players.ChooseSpeciality;
import com.oreilly.mmogroup.interaction.players.JoinGroup;
import com.oreilly.mmogroup.interaction.players.LeaveGroup;


public class MainMenu extends InteractionPage {
	
	public MainMenu() {
		// DEBUG
		System.out.println( "Init of main menu" );
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this );
		// depending on user, the choices change...
		PlayerHelper helper = new PlayerHelper( interaction );
		if ( helper.playersGroup == null ) {
			// no group at all? Their only option is to join one.. if they have permission to.
			if ( MMOGroup.permission.has( interaction.user, PermissionConstants.joinGroup ) ) {
				choices.addPageChoice( variable.define( "joinGroup" ), new JoinGroup() );
			} else
				// abort the conversation
				throw new AbortInteraction( variable.define( "error.permissions.joinGroup" ) );
		} else if ( MMOGroup.permission.has( interaction.user, PermissionConstants.changeGroup ) ) {
			choices.addPageChoice( variable.define( "changeGroup" ), new LeaveGroup(), new JoinGroup() );
		} else if ( MMOGroup.permission.has( interaction.user, PermissionConstants.leaveGroup ) ) {
			choices.addPageChoice( variable.define( "leaveGroup" ), new LeaveGroup() );
		}
		// next check, can they specialise?
		if ( MMOGroup.permission.has( interaction.user, PermissionConstants.specialise ) ) {
			if ( helper.playerRecord.getSpecialisation() == null ) {
				PlayerProfile profile = MMOGroup.mcMMO.getPlayerProfile( interaction.user.getName() );
				if ( profile != null ) {
					if ( profile.getPowerLevel() > MMOGroup.instance.config.minPowerLevelForSpecialisation ) {
						choices.addPageChoice( variable.define( "specialise" ), new ChooseSpeciality() );
					}
				}
			}
		}
		return choices;
	}
}
