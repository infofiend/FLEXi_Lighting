/**
 *  FlexiHue Lux Bulb
 *	Version 1.0
 *  Author: anthony pastor
 */
// for the UI

metadata {
	// Automatically generated. Make future change here.
	definition (name: "FLEXiHue Lux Bulb", namespace: "info_fiend", author: "Anthony Pastor") {
		capability "Switch Level"
		capability "Actuator"
		capability "Switch"
		capability "Refresh"
		capability "Sensor"
       
        command "refresh"       
	}

	simulator {
		// TODO: define status and reply messages here
	}
    
    tiles(scale: 2) {
        multiAttributeTile(name:"rich-control", type: "lighting", canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
              attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
              attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
              attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
              attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
              attributeState "level", action:"switch level.setLevel", range:"(0..100)"
            }
            tileAttribute ("device.level", key: "SECONDARY_CONTROL") {
	            attributeState "level", label: 'Level ${currentValue}%'
			}
        }
    
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
			state "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
			state "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
			state "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
        }
        
        controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 2, inactiveLabel: false, range:"(0..100)") {
            state "level", action:"switch level.setLevel"
        }
           
        standardTile("refresh", "device.switch", inactiveLabel: false, height: 2, width: 2, decoration: "flat") {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }        

        main(["switch"])
        details(["rich-control", "refresh"])
    }    
}

// parse events into attributes
def parse(description) {
	log.debug "parse() - $description"
	def results = []

	def map = description
	if (description instanceof String)  {
		log.debug "FlexiHue Lux Bulb stringToMap - ${map}"
		map = stringToMap(description)
	}

	if (map?.name && map?.value) {
		results << createEvent(name: "${map?.name}", value: "${map?.value}")
	}
	results
}

// handle commands
def on() {
	on(2)
}

def on(Number inTransitionTime) {

	log.debug "FLEXiHue Lux (${device.label}): 'On(transitionTime)'"

	def theLevel = 99
	def transTime = inTransitionTime ?: 2
    
	log.trace "...sending to 'setLevel(${theLevel})' with TT."
	setLevel(theLevel, transTime) 
}

def off() {
	off(2)
}

def off(Number inTransitionTime) {

	log.debug "FLEXiHue Lux (${device.label}): Executing 'Off(transitionTime)'"
    if (device.currentValue("switch") == "on" ) {
		sendEvent(name: "switch", value: "off", isStateChange: true)
    }
	parent.off(this, inTransitionTime)
        
}

def setLevel(Number percent) {
	log.debug "Executing 'setLevel'"
	def transTime = 2
	log.trace "...sending to 'setLevel' with TT."
    setLevel(percent, transTime)
    
//	parent.setLevel(this, percent)
//	sendEvent(name: "level", value: percent)

}

def setLevel(Number percent, inTransitionTime) {

	log.debug "FLEXiHue Lux (${device.label}): Executing 'setLevel(${percent}, ${transitiontime})'."
 
	sendEvent(name: "switch", value: "on", isStateChange: true)    
	sendEvent(name: "level", value: percent, isStateChange: true)

	log.trace "Calling 'parent.setLevel(${this}, ${percent}, ${inTransitionTime})'."
	parent.setLevel(this, percent, inTransitionTime)
}

def refresh() {
	log.debug "Executing 'refresh'"
	parent.manualRefresh()
}
