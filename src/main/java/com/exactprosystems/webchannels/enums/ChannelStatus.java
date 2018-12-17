/*
 * *****************************************************************************
 *  Copyright 2009-2018 Exactpro (Exactpro Systems Limited)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

package com.exactprosystems.webchannels.enums;

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
