package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.WordUtils;

import com.gmail.nossr50.datatypes.SkillType;
import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.AbortInteraction;
import com.oreilly.common.interaction.text.error.ContextDataRequired;
import com.oreilly.common.interaction.text.error.GeneralInteractionError;
import com.oreilly.common.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.api.GroupAPI;
import com.oreilly.mmogroup.errors.PluginNotEnabled;
import com.oreilly.mmogroup.interaction.Constants;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupAddSpeciality extends InteractionPage {
	
	public GroupAddSpeciality() {
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
		// generate a choice for each skill type that doesn't have a specialisation already
		Set< SkillType > existingSpecialities = helper.record.getSpecialitySkills(); //TODO: Add to API
		for ( SkillType skill : SkillType.values() ) {
			if ( skill == SkillType.ALL )
				continue;
			if ( existingSpecialities.contains( skill ) )
				continue;
			choices.addInternalChoice( WordUtils.capitalize( skill.toString() ), skill.toString() );
		}
		// add a 'cancel' choice as well
		choices.addInternalChoice( "Cancel", "cancel" );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String selectedSkillName ) throws GeneralInteractionError {
		if ( selectedSkillName.equalsIgnoreCase( "cancel" ) )
			return null;
		SkillType selected = SkillType.valueOf( selectedSkillName );
		if ( selected == null )
			throw new GeneralInteractionError( "Unable to determine a skill type based on \"" + selectedSkillName +
					"\"" );
		GroupHelper helper = new GroupHelper( interaction );
		try {
			GroupAPI.addSpecialityOption( helper.record, selected, WordUtils.capitalize( selectedSkillName ),
					MMOGroup.instance.config.minPowerLevelForSpecialisation,
					MMOGroup.instance.config.specialisationBonus );
			interaction.context.put( Constants.SELECTED_GROUP_SPECIAL, selectedSkillName );
			return "New speciality created for " + selectedSkillName.toLowerCase();
		} catch ( PluginNotEnabled error ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
}
