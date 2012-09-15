package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;

import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.validator.IntValidator;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupModifySpecialityRequirement extends InteractionPage {
	
	public GroupModifySpecialityRequirement() {
		autoPage();
		withValidator( new IntValidator() );
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		return helper.getVariables();
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws GeneralInteractionError {
		Integer requirement = (Integer)data;
		GroupHelper helper = new GroupHelper( interaction );
		try {
			GroupAPI.changeSpecialityRequirement( helper.record, helper.selectedSpecialityName, requirement );
			return "A power level of " + requirement + " is now required for " + helper.selectedSpecialityName;
		} catch ( PluginNotEnabled e ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
}
