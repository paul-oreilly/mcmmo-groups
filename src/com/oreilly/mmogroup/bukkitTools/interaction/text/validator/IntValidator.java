package com.oreilly.mmogroup.bukkitTools.interaction.text.validator;

import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.InterfaceDependencyError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ValidationFailedError;


public class IntValidator extends Validator {
	
	@Override
	protected Object validate( Object object, InteractionPage page ) throws ValidationFailedError,
			InterfaceDependencyError {
		if ( object instanceof Integer )
			return object;
		if ( Integer.class.isAssignableFrom( object.getClass() ) ) {
			return object;
		}
		String asString = object.toString().toLowerCase();
		try {
			Integer parsed = Integer.parseInt( asString );
			return parsed;
		} catch ( NumberFormatException error ) {
			throw new ValidationFailedError( this, "Unable to convert " + object.toString() + " to an integer" );
		}
	}
	
}
