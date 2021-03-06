package com.oreilly.mmogroup.bukkitTools.interaction.text.error;

public class InterfaceDependencyError extends Exception {
	
	private static final long serialVersionUID = 6503770121688302349L;
	public String interfaceRequired = null;
	
	
	public InterfaceDependencyError( String interfaceRequired ) {
		this.interfaceRequired = interfaceRequired;
		System.out.println( this.getClass().getName() + " ERROR: Interface dependency error for " + interfaceRequired );
	}
}
