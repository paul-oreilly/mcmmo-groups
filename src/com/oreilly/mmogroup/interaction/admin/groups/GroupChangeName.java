package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;

import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupChangeName extends InteractionPage {
	
	public GroupChangeName() {
		autoPage();
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		return helper.getVariables();
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws GeneralInteractionError {
		GroupHelper helper = new GroupHelper( interaction );
		String oldName = helper.record.getName();
		try {
			if ( GroupAPI.changeName( helper.record, data.toString() ) )
				return oldName + " is now known as " + data.toString();
			else
				throw new GeneralInteractionError( "Failed to change name" );
		} catch ( PluginNotEnabled e ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
}
