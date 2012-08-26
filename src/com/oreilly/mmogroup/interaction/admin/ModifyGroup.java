package com.oreilly.mmogroup.interaction.admin;

import java.util.HashMap;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.AbortInteraction;
import com.oreilly.common.interaction.text.helpers.Choices;
import com.oreilly.common.text.VariablePrefixer;
import com.oreilly.mmogroup.interaction.admin.groups.GroupAddBonus;
import com.oreilly.mmogroup.interaction.admin.groups.GroupAddSpeciality;
import com.oreilly.mmogroup.interaction.admin.groups.GroupChangeName;
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
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this );
		choices.addPageChoice( variable.define( "ChangeName" ),
				new GroupChangeName() );
		choices.addPageChoice( variable.define( "DeleteGroup" ),
				new DeleteGroup() );
		choices.addPageChoice( variable.define( "AddSpecial" ),
				new GroupAddSpeciality(), new GroupModifySpeciality() );
		choices.addPageChoice( variable.define( "RemoveSpecial" ),
				new GroupSelectSpeciality(), new GroupRemoveSpeciality() );
		choices.addPageChoice( variable.define( "ModifySpecial" ),
				new GroupSelectSpeciality(), new GroupModifySpeciality() );
		choices.addPageChoice( variable.define( "AddBonus" ),
				new GroupAddBonus(), new GroupModifyBonus() );
		choices.addPageChoice( variable.define( "ModifyBonus" ),
				new GroupSelectBonus(), new GroupModifyBonus() );
		choices.addPageChoice( variable.define( "RemoveBonus" ),
				new GroupRemoveBonus() );
		choices.addPageChoice( variable.define( "AutoTeleportToggle" ),
				new GroupToggleAutoTeleport() );
		choices.addPageChoice( variable.define( "SetTeleportLocation" ),
				new GroupSetTeleportLocation() );
		return choices;
	}
	
}