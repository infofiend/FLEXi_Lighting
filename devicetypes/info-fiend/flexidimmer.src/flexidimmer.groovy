/* FLEXiDimmer DEVICE TYPE (EXAMPLE using Aeon Micro G2 Dimmer)
 *
 * This is a variation of Mike Maxwell's specific micro driver 1.3 for Aeon Micro G2
 
 *	--ADDED: custom attributes (lines 40-45) and commands (lines 53-63 and lines 387-517) related to storage of scene setting information (see remarks below)
 *
 *		
 *

	AEON G2 
	0x20 Basic
	0x25 Switch Binary
	0x26 Switch Multilevel
	0x2C Scene Actuator Conf
	0x2B Scene Activation
	0x70 Configuration 
	0x72 Manufacturer Specific
	0x73 Powerlevel
	0x77 Node Naming
	0x85 Association
	0x86 Version
	0xEF MarkMark
	0x82 Hail

*/

metadata {
	definition (name: "flexidimmer", namespace: "info_fiend", author: "anthony pastor") {
		capability "Actuator"
		capability "Switch"
		capability "Refresh"
		capability "Sensor"
        capability "Alarm" 
        capability "Switch Level"
        capability "Energy Meter"
        capability "Power Meter"
        capability "Test Capability"
	// "FLEXI"-Related Attributes Added
        attribute "sceneLevel", "number"
		attribute "sceneSwitch", "string"
        attribute "sceneName", "string"
        attribute "offTime", "number"
	//  -------------------------------
        command "levelUp"
        command "levelDown"
        command "flash", ["string"]  //blink,flasher,strobe
        command "reset"
	// "FLEXI"-Related Commands Added
		command "setOffTime"
		command "setScName"       
        command "saveScene"
        command "setScSwitch", ["string"]
        command "sceneManual"
        command "sceneFree"
        command "sceneSlave"
        command "sceneMaster"
        command "setLevelFromThing"
	// ------------------------------
	

        //aeon S2 switch (DSC26103-ZWUS)
        fingerprint deviceId: "0x1001", inClusters: "0x25,0x27,0x2C,0x2B,0x70,0x85,0x72,0x86,0xEF,0x82"

	}
    preferences {
       	input name: "param80", type: "enum", title: "State change notice:", description: "Type", required: true, options:["Off","Hail","Report"]
        input name: "param120", type: "enum", title: "Set trigger mode:", description: "Switch type", required: true, options:["Momentary","Toggle","Three Way"]
        input name: "blinker", type: "enum", title: "Set blinker mode:", description: "Blinker type", required: false, options:["Blink","Flasher","Strobe"]
        input name: "dInterval", type: "enum", title: "Set dimmer button offset:", description: "Value per click", required: false, options:["1","5","10"]
    }
  

	// simulator metadata
	simulator {
		status "on":  "command: 2003, payload: FF"
		status "off": "command: 2003, payload: 00"
        for (int i = 0; i <= 10000; i += 1000) {
			status "power  ${i} W": new physicalgraph.zwave.Zwave().meterV1.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 4, scale: 2, size: 4).incomingMessage()
		}
		for (int i = 0; i <= 100; i += 10) {
			status "energy  ${i} kWh": new physicalgraph.zwave.Zwave().meterV1.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 0, scale: 0, size: 4).incomingMessage()
		}

		// reply messages
		reply "2001FF,delay 100,2502": "command: 2503, payload: FF"
		reply "200100,delay 100,2502": "command: 2503, payload: 00"
	}

	// tile definitions
tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, canChangeBackground: true) {
			state "on", label:'${name}', action:"switch.off", backgroundColor:"#79b821", nextState:"turningOff"
			state "off", label:'${name}', action:"switch.on", backgroundColor:"#ffffff", nextState:"turningOn"
			state "turningOn", label:'${name}', backgroundColor:"#79b821"
			state "turningOff", label:'${name}', backgroundColor:"#ffffff"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		standardTile("blink", "device.alarm", inactiveLabel: false, decoration: "flat") {
            state "default", label:"", action:"alarm.strobe", backgroundColor: "#53a7c0", icon:"st.secondary.strobe" 
		}
		valueTile("lValue", "device.level", inactiveLabel: true, height:1, width:1, decoration: "flat") {
            state "levelValue", label:'${currentValue}%', unit:"", backgroundColor: "#53a7c0"
        }
        standardTile("lUp", "device.switchLevel", inactiveLabel: false,decoration: "flat", canChangeIcon: false) {
            state "default", action:"levelUp", icon:"st.illuminance.illuminance.bright"
        }
        standardTile("lDown", "device.switchLevel", inactiveLabel: false,decoration: "flat", canChangeIcon: false) {
            state "default", action:"levelDown", icon:"st.illuminance.illuminance.light"
        }
        valueTile("power", "device.power", decoration: "flat") {
			state "default", label:'${currentValue} W'
		}
		valueTile("energy", "device.energy", decoration: "flat") {
			state "default", label:'${currentValue} kWh1'
		}
		standardTile("reset", "device.energy", inactiveLabel: false, decoration: "flat") {
			state "default", label:'reset kWh', action:"reset"
		}
		controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 2) {
			state "level", action:"setLevelFromThing"
		}
		valueTile("level", "device.level", decoration: "flat") {
			state "level", label: 'Level ${currentValue}%'
		}



// NL ADDED TILES
	    valueTile("sceneName", "device.sceneName", decoration: "flat") {
			state "sceneName", label: 'Scene: ${currentValue}'
		}

    	valueTile("offTime", "device.offTime") {
			state "offTime", label: 'OffTime: ${currentValue}'
		}

		standardTile("sceneSwitch", "device.sceneSwitch", width: 1, height: 1, canChangeIcon: true, decoration: "flat", defaultState: "Manual") {
        	state "Manual", label: 'Manual', icon:"https://dl.dropboxusercontent.com/u/2403292/STIcons/manualSettings-large.png"
	        state "Master", label: '${name}', icon:"https://dl.dropboxusercontent.com/u/2403292/STIcons/MCP-large.png" 
    	    state "Slave", label: 'Slave', action:"sceneFree", icon:"https://dl.dropboxusercontent.com/u/2403292/STIcons/slave-large.png", nextState: "Freebie"
			state "Freebie", action:"sceneSlave", icon:"https://dl.dropboxusercontent.com/u/2403292/STIcons/free-large.png", nextState: "Slave"

	    }



		main(["switch", "lUp"])
        details(["switch",  "offTime", "sceneSwitch", "level", "levelSliderControl", "lUp", "lDown","blink","lValue","refresh", "energy", "power", "reset"])
	}
 }

def parse(String description) {
	def result = null
	def cmd = zwave.parse(description, [0x20: 1, 0x70: 1])
    //log.debug "cmd:${cmd.inspect()}"
	def item1 = [
		canBeCurrentState: false,
		linkText: displyName,
		isStateChange: false,
		displayed: false,
		descriptionText: description,
		value:  description
	]
    if (cmd.hasProperty("value")) {
		result = createEvent(cmd, item1)
	}
    //log.debug "res:${result.inspect()}"
	return result
}


def createEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd, Map item1) {
	//aeons return this when in mode 2
	//log.debug "basicReport:${cmd.inspect()}"
    
    def result = [item1]

	item1.name = "switch"
	item1.value = cmd.value ? "on" : "off"
	item1.handlerName = item1.value
	item1.descriptionText = "${item1.linkText} was turned ${item1.value}"
	item1.canBeCurrentState = true
	item1.isStateChange = isStateChange(device, item1.name, item1.value)
	item1.displayed = item1.isStateChange

	if (cmd.value > 15) {
		def item2 = new LinkedHashMap(item1)
		item2.name = "level"
		item2.value = cmd.value as String
		item2.unit = "%"
		item2.descriptionText = "${item1.linkText} dimmed ${item2.value} %"
		item2.canBeCurrentState = true
		item2.isStateChange = isStateChange(device, item2.name, item2.value)
		item2.displayed = false
		result << item2
	}
    //i still don't know what this is...
    for (int i = 0; i < result.size(); i++) {
		result[i].type = "physical"
	}
    return result 
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	// Handles all Z-Wave commands we aren't interested in or don't know about
    //log.debug "udf:${cmd.inspect()}"
	return [:]
}

//
//
// "FLEXI"-Related Commands (either added or modified)

def setScSwitch(inState) {
	log.debug "FlexiAeonDimmer: Executing 'setScSwitch(${inState})'"
	sendEvent (name: "sceneSwitch", value: inState, isStateChange: true)
        	          
}

def sceneManual() {
//	log.debug "FlexiAeonDimmer: sceneManual setting sceneSwitch to: MANUAL."  

    def curLevel = device.currentValue("level")
    setScLevel(curLevel)
    def curSat = device.currentValue("saturation")
    setScSat(curSat)
    def curHue = device.currentValue("hue")
    setScHue(curHue)

	def newValue = "Manual" as String
    setScSwitch(newValue)

}

def sceneFree() {
//	log.debug "FlexiAeonDimmer: sceneFree setting sceneSwitch to: FREEEEEE."
	def newValue = "Freebie" as String
   	setScSwitch(newValue)
	refresh()    
}

def sceneSlave() {
//	log.debug "FlexiAeonDimmer: sceneSlave setting sceneSwitch to: SLAVE."  
	def newValue = "Slave" as String
 	setScSwitch(newValue)
	refresh()
}

def saveScene(Number inValue, String inMode, Number inOffTime) {

	log.debug "FlexiAeonDimmer: saveScene:"

    if ( inMode ) {
		log.debug "sceneName to: ${inMode}"
	    sendEvent(name: "sceneName", value: inMode, isStateChange: true)
    }	

    if ( inOffTime ) {
  		if ( inOffTime == 0 ) {
			log.debug "Setting offTime to: default 30 b/c inOffTime is 0."
		    sendEvent(name: "offTime", value: 30, isStateChange: true)
    	} else {
			log.debug "Setting offTime to: ${inOffTime}"
		    sendEvent(name: "offTime", value: inOffTime, isStateChange: true)
    	}	
	}

	if ( inValue ) {
		sendEvent(name: "sceneLevel", value: inValue.level)
        log.debug "Setting sceneLevel to: ${inValue}."        
	}
	
}

def setLevel(Number value) {

	setLevel(value, 3)

//Don't request a config report when advanced reporting is enabled
//	if (settings.param80 in ["Hail","Report"]) zwave.basicV1.basicSet(value: value).format()
//    else delayBetween ([zwave.basicV1.basicSet(value: value).format(), zwave.basicV1.basicGet().format()], 5000)
}

def setLevelFromThing(Number inValue) {

//	log.debug "FlexiAeonDimmer: setLevelfromThing setting sceneSwitch to: Manual."  
	def newValue = "Manual" as String
    setScSwitch(newValue)

	setLevel(inValue, 3)
}


def setLevel(Number inValue, duration) {
	if (device.latestValue("switch") == "off" ) {
		sendEvent(name: "switch", value: "on", isStateChange: true)
    }
    
	sendEvent(name: "level", value: inValue, isStateChange: true)
    
    def dimmingDuration = duration < 128 ? duration : 128 + Math.round(duration / 60)

	//Don't request a config report when advanced reporting is enabled
	if (settings.param80 in ["Hail","Report"]) zwave.switchMultilevelV2.switchMultilevelSet(value: inValue, dimmingDuration: 0).format()
    else delayBetween ([zwave.switchMultilevelV2.switchMultilevelSet(value: inValue, dimmingDuration: duration).format(), zwave.basicV1.basicGet().format()], 0)
}

def on() {
    //reset alarm trigger
    state.alarmTriggered = 0
	    
    def level = device.currentValue("level")
    if(level == null)
    {
    	level = 99
    }
    if (device.latestValue("switch") == "off" ) {
		sendEvent(name: "switch", value: "on", isStateChange: true)
    }
    
    //Don't request a config report when advanced reporting is enabled
	if (settings.param80 in ["Hail","Report"]) zwave.basicV1.basicSet(value: level).format() 					//value: 0xFF
    else delayBetween([zwave.basicV1.basicSet(value: level).format(), zwave.basicV1.basicGet().format()], 5000)	//value: 0xFF
}

def off() {
    //log.debug "at:${state.alarmTriggered} swf:${state.stateWhenFlashed}"
    
    //override alarm off command from smartApps
    if (state.alarmTriggered == 1 && state.stateWhenFlashed == 1) {
    	state.alarmTriggered = 0
    } else {
    	//Don't request a config report when advanced reporting is enabled
    	if (settings.param80 in ["Hail","Report"]) zwave.basicV1.basicSet(value: 0x00).format()
		else delayBetween ([zwave.basicV1.basicSet(value: 0x00).format(),  zwave.basicV1.basicGet().format()], 5000)
    }
}

// ----------------------------------

def levelUp(){
	def int step = (settings.dInterval ?:10).toInteger() //set 10 as default
    def int crntLevel = device.currentValue("level")
    def int nextLevel = crntLevel - (crntLevel % step) + step  
    state.alarmTriggered = 0
    if( nextLevel > 99)	nextLevel = 99
    //log.debug "crnt:${crntLevel} next:${nextLevel}"
    //Don't request a config report when advanced reporting is enabled
    if (settings.param80 in ["Hail","Report"]) zwave.basicV1.basicSet(value: nextLevel).format()
    else delayBetween ([zwave.basicV1.basicSet(value: nextLevel).format(), zwave.basicV1.basicGet().format()], 5000)
}

def levelDown(){
	def int step = (settings.dInterval ?:10).toInteger() //set 10 as default
	def int crntLevel = device.currentValue("level")
    def int nextLevel //= crntLevel - (crntLevel % step) - step  
    state.alarmTriggered = 0
    if (crntLevel == 99) {
    	nextLevel = 100 - step
    } else {
    	nextLevel = crntLevel - (crntLevel % step) - step
    }
	//log.debug "crnt:${crntLevel} next:${nextLevel}"
	if (nextLevel == 0){
    	off()
    }
    else
    {
    	//Don't request a config report when advanced reporting is enabled
    	if (settings.param80 in ["Hail","Report"]) zwave.basicV1.basicSet(value: nextLevel).format()
    	else delayBetween ([zwave.basicV1.basicSet(value: nextLevel).format(), zwave.basicV1.basicGet().format()], 5000)
    }
}

def refresh() {
     return zwave.basicV1.basicGet().format()
}


def createEvent(physicalgraph.zwave.commands.meterv2.MeterReport cmd, Map item1)
{
	if (cmd.scale == 0) {
		createEvent([name: "energy", value: cmd.scaledMeterValue, unit: "kWh"])
	} else if (cmd.scale == 1) {
		createEvent([name: "energy", value: cmd.scaledMeterValue, unit: "kVAh"])
	}
	else {
		createEvent([name: "power", value: Math.round(cmd.scaledMeterValue), unit: "W"])
	}
}

def reset() {
	return [
		zwave.meterV2.meterReset().format(),
		zwave.meterV2.meterGet().format()
	]
}




//built in flasher mode
def flash(type) {
	if (!type) type = settings.blinker
	//AEON blink parameters
	//1: blink duration in seconds 1-255
    //2: cycle time in .1 seconds (50% duty cycle) 1-255
    def pBlink = []
    
    if (device.currentValue("switch") == "on") state.stateWhenFlashed = 1
    else state.stateWhenFlashed = 0
    
    switch (settings.blinker) {
		case "Flasher":
        	pBlink.add(10)
            pBlink.add(10)
            break
        case "Strobe":
            pBlink.add(3)
            pBlink.add(2)
            break
		default: //Blink
           	pBlink.add(1)
           	pBlink.add(20)
            break
	}
    //sendEvent (name: "alarm", value: "done",descriptionText: "Flasher activated.")
	zwave.configurationV1.configurationSet(configurationValue: pBlink, parameterNumber: 2, size: 2).format()
}

//alarm methods
def strobe() {
	state.alarmTriggered = 1
	flash(settings.blinker)
}

def siren() {
	state.alarmTriggered = 1
	flash(settings.blinker)
}

def both()	{
	state.alarmTriggered = 1
	flash(settings.blinker)
}

//capture preference changes
def updated() {
    //log.debug "before settings: ${settings.inspect()}, state: ${state.inspect()}" 
    
    //get requested reporting preferences
    Short p80
    switch (settings.param80) {
		case "Off":
			p80 = 0
            break
		case "Hail":
			p80 = 1
            break
		default:
			p80 = 2	//Report
            break
	}    
    
	//get requested switch function preferences
    Short p120
    switch (settings.param120) {
		case "Momentary":
			p120 = 0
            break
		case "Three Way":
			p120 = 2
            break
		default:
			p120 = 1	//Toggle
            break
	}    
  
	//update if the settings were changed
    if (p80 != state.param80)	{
    	//log.debug "update 80:${p80}"
        state.param80 = p80 
        return response(zwave.configurationV1.configurationSet(parameterNumber: 80, size: 1, configurationValue: [p80]).format())
    }
	if (p120 != state.param120)	{
    	//log.debug "update 120:${p120}"
        state.param120 = p120
        return response(zwave.configurationV1.configurationSet(parameterNumber: 120, size: 1, configurationValue: [p120]).format())
    }

	//log.debug "after settings: ${settings.inspect()}, state: ${state.inspect()}"
}

def configure() {
	settings.param80 = "Report"
    settings.param120 = "Toggle"
    settings.blinker = "Blink"
	delayBetween([
		zwave.configurationV1.configurationSet(parameterNumber: 80, size: 1, configurationValue: 2).format(),
		zwave.configurationV1.configurationSet(parameterNumber: 120, size: 1, configurationValue: 1).format()
	])
}




