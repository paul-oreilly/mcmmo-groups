package com.oreilly.mmogroup.interaction.admin;

import java.util.HashMap;

import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.interaction.admin.groups.GroupAddBonus;
import com.oreilly.mmogroup.interaction.admin.groups.GroupAddSpeciality;
import com.oreilly.mmogroup.interaction.admin.groups.GroupModifyBonus;
import com.oreilly.mmogroup.interaction.admin.groups.GroupModifySpeciality;
import com.oreilly.mmogroup.interaction.admin.groups.GroupRemoveBonus;
import com.oreilly.mmogroup.interaction.admin.groups.GroupRemoveSpeciality;
import com.oreilly.mmogroup.interaction.admin.groups.GroupSelectBonus;
import com.oreilly.mmogroup.interaction.admin.groups.GroupSelectSpeciality;
import com.oreilly.mmogroup.interaction.admin.groups.GroupSetTeleportLocation;
import com.oreilly.mmogroup.interaction.admin.groups.GroupToggleAutoTeleport;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class ModifyGroup extends InteractionPage {
	
	public ModifyGroup() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		return helper.getVariables();
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, PageFailure,
			ContextDataRequired, GeneralInteractionError {
		interaction.addBookmark( this );
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		/*choices.addPageChoice( variable.define( "ChangeName" ),
				new GroupChangeName() );*/
		choices.addPageChoice( variable.define( "delete_group" ),
				new DeleteGroup() );
		choices.addPageChoice( variable.define( "add_special" ),
				new GroupAddSpeciality(), new GroupModifySpeciality() );
		choices.addPageChoice( variable.define( "remove_special" ),
				new GroupSelectSpeciality(), new GroupRemoveSpeciality() );
		choices.addPageChoice( variable.define( "modify_special" ),
				new GroupSelectSpeciality(), new GroupModifySpeciality() );
		choices.addPageChoice( variable.define( "add_bonus" ),
				new GroupAddBonus(), new GroupModifyBonus() );
		choices.addPageChoice( variable.define( "modify_bonus" ),
				new GroupSelectBonus(), new GroupModifyBonus() );
		choices.addPageChoice( variable.define( "remove_bonus" ),
				new GroupSelectBonus(), new GroupRemoveBonus() );
		choices.addPageChoice( variable.define( "auto_teleport_toggle" ),
				new GroupToggleAutoTeleport() );
		choices.addPageChoice( variable.define( "set_teleport_location" ),
				new GroupSetTeleportLocation() );
		choices.addFail( variable.define( "cancel" ) );
		return choices;
	}
	
}
