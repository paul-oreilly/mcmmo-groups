package com.oreilly.mmogroup.interaction.admin;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.errors.PluginNotEnabled;


public class CreateGroup extends InteractionPage {
	
	public CreateGroup() {
		autoPage();
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		HashMap< String, Object > variables = new HashMap< String, Object >();
		try {
			variables.put( "currentGroups", StringUtils.join( GroupAPI.getAllGroups(), ", " ) );
		} catch ( PluginNotEnabled e ) {
			e.printStackTrace();
		}
		return variables;
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) throws GeneralInteractionError {
		try {
			GroupAPI.createGroup( data.toString() );
			return "A new group has been created (" + data.toString() + ")";
		} catch ( PluginNotEnabled e ) {
			e.printStackTrace();
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
		
	}
	
}
