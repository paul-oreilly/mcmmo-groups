package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.gmail.nossr50.datatypes.SkillType;
import com.oreilly.mmogroup.MMOGroup;
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
			GeneralInteractionError, PageFailure {
		Choices choices = new Choices( this, interaction );
		GroupHelper helper = new GroupHelper( interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		List< String > existingNames = helper.record.getSpecialityNames();
		HashMap< String, SkillType > skillMap = new HashMap< String, SkillType >();
		// generate a choice for each skill type 
		for ( SkillType skill : SkillType.values() ) {
			if ( skill == SkillType.ALL )
				continue;
			String defaultName = WordUtils.capitalizeFully( skill.toString() );
			String currentName = defaultName;
			int count = 2;
			while ( existingNames.contains( currentName ) ) {
				currentName = defaultName + count;
				count++;
			}
			choices.addInternalChoice( WordUtils.capitalizeFully( skill.toString() ), currentName );
			skillMap.put( currentName, skill );
		}
		// add a 'cancel' choice as well
		choices.addCancel( variable.define( "cancel" ) );
		// add the map from names to skills
		interaction.context.put( this.getClass().getSimpleName() + "_skillMap", skillMap );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String selectedSkillName ) throws GeneralInteractionError,
			ContextDataRequired {
		@SuppressWarnings("unchecked")
		HashMap< String, SkillType > skillMap = interaction.getContextData( HashMap.class, interaction, this.getClass()
				.getSimpleName() + "_skillMap" );
		SkillType selected = skillMap.get( selectedSkillName );
		if ( selected == null )
			throw new GeneralInteractionError( "Unable to determine a skill type based on \"" + selectedSkillName +
					"\"" );
		GroupHelper helper = new GroupHelper( interaction );
		try {
			GroupAPI.addSpecialityOption( helper.record, selected, selectedSkillName,
					MMOGroup.instance.config.minPowerLevelForSpecialisation,
					MMOGroup.instance.config.specialisationBonus );
			interaction.context.put( Constants.SELECTED_GROUP_SPECIAL, WordUtils.capitalizeFully( selectedSkillName ) );
			return "New speciality created for " + selectedSkillName.toLowerCase();
		} catch ( PluginNotEnabled error ) {
			throw new GeneralInteractionError( "Plugin not enabled" );
		}
	}
}
