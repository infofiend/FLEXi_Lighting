/**
 *  FLEXi Virtual Presence
 *
 *  Copyright 2015 Anthony Pastor
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
 metadata {
	// Automatically generated. Make future change here.
	definition (name: "FLEXi Virtual Presence", namespace: "info_fiend", author: "anthony pastor") {
		capability "Actuator"
		capability "Presence Sensor"
		capability "Sensor"
		capability "Switch"        
        
        attribute "vPresence", "string"
		
	}

	simulator {
		status "present": "presence: 1"
		status "not present": "presence: 0"
	
	}

	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, canChangeBackground: true) {
			state "present", label:'PRESENT', action: "arrived", backgroundColor:"#53a7c0", nextState:"leaving"
			state "not present", label:'NOT Present', action: "left", backgroundColor:"#ffffff", nextState:"arriving"
			state "leaving", label:'${name}', backgroundColor:"#79b821"
			state "arriving", label:'${name}', backgroundColor:"#ffffff"            
		}

	
		main "switch"
		details(["switch"])
	}
}


def parse(String description) {
	
}

def arrived() {
	sendEvent(name: "vPresence", value: "present", isStateChange: true)
	sendEvent(name: "presence", value: "present", isStateChange: true)    
}

def left() {
	sendEvent(name: "vPresence", value: "not present", isStateChange: true)
	sendEvent(name: "presence", value: "not present", isStateChange: true)    
}



