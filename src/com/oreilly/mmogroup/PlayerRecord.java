package com.oreilly.mmogroup;



public class PlayerRecord {
	
	String name;
	String groupName;
	String specialisation;
	protected MMOGroup plugin = null;
	
	
	public PlayerRecord( MMOGroup plugin, String name ) {
		this( plugin, name, null, null );
	}
	
	
	public PlayerRecord( MMOGroup plugin, String name, String groupName, String specialisation ) {
		this.plugin = plugin;
		this.name = name;
		this.groupName = groupName;
		this.specialisation = specialisation;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public void setGroup( String groupName ) {
		String oldGroup = this.groupName;
		this.groupName = groupName;
		plugin.players._internal_PlayerRecordGroupUpdate( this, oldGroup );
	}
	
	
	public String getGroupName() {
		return groupName;
	}
	
	
	public GroupRecord getGroup() {
		return plugin.groups.getGroup( groupName );
	}
	
	
	public String getSpecialisation() {
		return specialisation;
	}
	
	
	public void setSpecialisation( String name ) {
		String oldSpecialisation = this.specialisation;
		specialisation = name;
		plugin.players._internal_PlayerRecordSpecialisationUpdate( this, oldSpecialisation );
	}
}
