package com.exactprosystems.ticker.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ChannelStatus")
@XmlEnum
public enum ChannelStatus {

	CREATED("CREATED"),
	OPENED("OPEN"),
	WAITING("WAITING"),
	CLOSED("CLOSED");
	
	private final String displayName; 
	
	ChannelStatus(final String display)  
    {  
        this.displayName = display;  
    }  

    @Override 
    public String toString()  
    {  
        return this.displayName;  
    } 
	
}
