package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;

import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.validator.DoubleValidator;
import com.oreilly.mmogroup.bukkitTools.io.Numbers;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupModifyBonus extends InteractionPage {
	
	public GroupModifyBonus() {
		autoPage();
		withValidator( new DoubleValidator() );
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		HashMap< String, Object > variables = helper.getVariables();
		if ( helper.selectedSkill != null ) {
			double currentBonusFactor = helper.record.getSkillBonus( helper.selectedSkill );
			variables.put( "current_bonus_factor", Numbers.doubleAsPercentage( currentBonusFactor, 2 ) );
		}
		return variables;
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws GeneralInteractionError {
		// sets the new bonus factor for the selected skill
		GroupHelper helper = new GroupHelper( interaction );
		try {
			GroupAPI.setBonusAmount( helper.record, helper.selectedSkill, (Double)data );
			return "The experience for " + helper.selectedSkill.toString().toLowerCase() + " is now set at " +
					Numbers.doubleAsPercentage( (Double)data, 2 );
		} catch ( PluginNotEnabled e ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
}
