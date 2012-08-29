package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;

import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.io.Numbers;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupModifySpecialName extends InteractionPage {
	
	public GroupModifySpecialName() {
		autoPage();
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		HashMap< String, Object > variables = helper.getVariables();
		if ( helper.selectedSkill != null ) {
			double currentBonusFactor = helper.record.getSkillBonus( helper.selectedSkill );
			variables.put( "currentBonusFactor", Numbers.doubleAsPercentage( currentBonusFactor, 2 ) );
		}
		return variables;
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws GeneralInteractionError {
		// sets the new bonus factor for the selected skill
		GroupHelper helper = new GroupHelper( interaction );
		try {
			GroupAPI.changeSpecialityName( helper.record, helper.selectedSpecialityName, data.toString().trim() );
			return helper.selectedSpecialityName + " is now known as " +
					data.toString();
		} catch ( PluginNotEnabled e ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
}
