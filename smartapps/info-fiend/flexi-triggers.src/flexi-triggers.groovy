/** FLEXi Triggers
	
    
	Version 1.3 (2015-10-28)
 
   The latest version of this file can be found at:
   https://github.com/infofiend/FLEXi_Lighting/FLEXi_Triggers
 
 
   --------------------------------------------------------------------------
 
   Copyright (c) 2015 Anthony Pastor
 
   This program is free software: you can redistribute it and/or modify it
   under the terms of the GNU General Public License as published by the Free
   Software Foundation, either version 3 of the License, or (at your option)
   any later version.
 
   This program is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
   for more details.
 
   You should have received a copy of the GNU General Public License along
   with this program.  If not, see <http://www.gnu.org/licenses/>.
   
 **/

definition(
    name: "FLEXi Triggers",
    namespace: "info_fiend",
    author: "Anthony Pastor",
   description: "Use this with the FLEXi Lighting Scenes smartapp to control FLEXi_Hue Device Type, " +
   				"Flexi_Dimmer Device Type, and / or any simple on/off switches.  A presence device " +
                "and/or a FlexiState Virtual Presence devices is needed." +
                 "         ---------------------------------------------------------------       " +
                 "Once a triggering event (motion or open contact) occurs, the correct color and levels " +
                 "will be applied. " ,
    category: "Convenience",
    parent: "info_fiend:FLEXi Lighting Scenes",
	iconUrl: "https://dl.dropboxusercontent.com/u/2403292/Lightbulb.png",
    iconX2Url: "https://dl.dropboxusercontent.com/u/2403292/Lightbulb%20large.png")

preferences {
    page name:"pageSettings"
    page name:"pageOptions"    
}


private def	pageSettings() {

    TRACE("pageSettings()")

	def parentModes = []
    parentModes = parent.sendChildTheModes()
    log.debug "Parent Modes = ${parentModes}"
    
	def textSettings =	
        "Select the Trigger Settings." +
        "Next Page will provide additional options."


	def pageProperties = [
        name        : "pageSettings",
        title       : "Choose Settings",
        nextPage    : "pageOptions",
        install     : false,
        uninstall   : true
	]

	def inputModes = [
        name        : "theModes",
        type        : "mode",
        title       : "Select the MODES (set up manually) that you want to use to control Lighting Scenes.  " +
        				"IMPORTANT: Selecting 'All Modes' will cause this app to not work properly.  " +
                        "Also - be sure to use only the Modes used in the parent Lighting App (or a subset thereof).",
        multiple:   true,
        required:   true
    ]
    
	def inputPresence = [
    	name        : "presence",
        type        : "capability.presenceSensor",
        title       : "Presence Sensors:",
        multiple:   true,
        required:   false
    ]    
       
    def inputVirtual = [
        name        : "virtual",
        type        : "device.flexistate",
        title       : "Virtual Presence Switches:",
        multiple:   true,
        required:   false
    ]
    
    def inputMotions = [
        name        : "motions",
        type        : "capability.motionSensor",
        title       : "Select motion detector(s):",
        multiple:   true,
        required:   false
    ]
    
    def inputContacts = [
        name        : "contacts",
        type        : "capability.contactSensor",
        title       : "Select contact detector(s):",
        multiple:   true,
        required:   false
    ]

	def inputHues = [
        name        : "hues",
        type        : "device.flexihueBulb",
        title       : "Select FLEXiHue Bulbs(s):",
        multiple:   true,
        required:   false
    ]
    
    def inputDimmers = [
        name        : "dimmers",
        type        : "device.flexidimmer",
        title       : "Select FLEXiDimmer Lights(s):",
        multiple:   true,
        required:   false
    ]
    
    def inputSwitches = [
        name        : "switches",
        type        : "capability.switch",
        title       : "Select any other On/Off Switches:",
        multiple:   true,
        required:   false
    ]
        
     return dynamicPage(pageProperties) {
		section("Selection of Presence, Contact, and Motion Sensors", hideable:true, hidden: state.installed) {
    		 paragraph textSettings 
        }     
    
		section("1. Select Presence Sensors: ALL of these people need to be home:") {
            input inputPresence
            input inputVirtual
        }
        section("2. Select Motion Sensors: Motion on ANY will trigger lights:") {
            input inputMotions
        }
        section("3. Select Contact Sensors: Open Contact on ANY will trigger lights:") {
            input inputContacts
        }
        section("4. Select Lights Triggered by the Above:") {
            input inputHues
            input inputDimmers
            input inputSwitches
        }
        section("5. Select Contact Sensors: Open Contact on ANY will trigger lights:") {
     
     		// input inputModes
            
//            input trigModes, type: "enum", title: "Select the modes (that are use in the Parent Lighting SmartApp) that THIS Trigger SmartApp will use:",
//            options: parentModes, multiple: true, required: true //, refreshAfterSelection: true
        	def m = location.mode
			def myModes = []
			parentModes.each {myModes << "$it"}
            input "xModes", "enum", multiple: true, title: "Select mode(s)", submitOnChange: true, options: myModes.sort(), defaultValue: [m]
    
        }
    }

}

private def	pageOptions() {

    TRACE("pageOptions()")

	def pageProperties = [
        name        : "pageOptions",
        title       : "Config Page 2: User Abort, Notification and Default Options",
        nextPage    : null,
        install     : true,
        uninstall   : state.installed
	]
    
    def inputAbortToggle = [
        name        : "abortToggle",
        type        : "capability.momentary",
        title       : "Identify the No-Motion Off Abort Toggle:",
        multiple:   false,
        required:   false
    ]
    
    def inputAbortTime = [
        name        : "abortTime",
        type        : "number",
        title       : "How long should app check for abort (minutes)?",
        multiple:   false,
        required:   true,
        defaultValue: 	2
    ]
    
    def inputAbortLength = [
        name        : "abortLength",
        type        : "number",
        title       : "How long should No-Motion events be ignored upon Abort? (minutes):",
        multiple:   false,
        required:   true,
        defaultValue: 	30        
    ]
   
	def inputDefLevel = [
        name        : "defLevel",
        type        : "number",
        title       : "Default Hue / Dimmer Level:",
        required:   true,
        defaultValue: 	99        
    ]
    
    def inputDefColor = [
        name        : "defColor",
        type        : "enum",
        title       : "Default Hue Color (in case Trigger cannot find any other):",
        multiple:   false,
        required:   true,
        metadata: [values:
					["Warm", "Soft", "Normal", "Daylight", "Red", "Green", "Blue", "Yellow", "Orange", "Purple", "Pink"]], 
        defaultValue: "Warm"
    ]
    
    def inputDefSwitch = [
        name        : "defSwitch",
        type        : "enum",
    title       : "Default Switch State:",
        required:true, 
        metadata: [values: ["yes", "no"]], 
        defaultValue: "no",
        multiple:   false
    ]
    
   	def inputDefOffTime = [
        name        : "defOffTime",
        type        : "number",
        title       : "Default amount of time to turn off lights after motion/contact event (minutes):",
        required:   true,
        defaultValue: 	30        
    ]

    return dynamicPage(pageProperties) {   
    	
        section([title:"1. Child App Label", mobileOnly:true]) {
           	label title:"Assign a name", required:false, submitOnChange: true
        }               
        
		section("2. User Abort Options", hideable:true, hidden: true) {
            input inputAbortToggle
            input inputAbortTime
            input inputAbortLength            
        }
        
        section("3. Default Light Settings", hideable:true, hidden: true) {
            input inputDefLevel
            input inputDefColor
            input inputDefSwitch
            input inputDefOffTime
        }
        

    }

}

    

def installed()
{
	
	initialize()

}


def updated()
{
	unschedule()
	unsubscribe()
	initialize()
    

}


def initialize()
{
	state.abortTime = null
    state.abortWindow = null
	state.inactiveAt = null
	state.allGroup = []   
	state.offTime = defOffTime
    
    state.theParentModes = parent.sendChildTheModes()   
	log.debug "the Parent Modes are ${state.theParentModes}."
	state.theChildModes = xModes
	log.debug "the Child Modes are ${state.theChildModes}."
    
    
	subscribe(virtual, "switch", checkHome)
    subscribe(people, "presence", checkHome)
    
    if (hues) {
    	subscribe(hues, "switch.on", levelCheck) 
    }
    
    if (motions) {
    	subscribe(motions, "motion", motionHandler)
    }
    if (contacts) {
    	subscribe(contacts, "contact", contactHandler)
    }

	if (abortToggle) {
    	subscribe(abortToggle, "momentary.pushed", abortHandler)       
    }
	subscribe(location, onLocation)

	colorCheck()
	setActiveAndSchedule()     // schedule ("0 * * * * ?", "scheduleCheck")

}

def checkOff() {   	// Wictor Wictor Niner
	def theCurMode = location.mode
	def modeOffTime = parent.sendChildOffInfo(theCurMode) as Number
    log.debug "checkOFF:  Parent Lighting App returned offTime of ${modeOffTime} for mode ${theCurMode}."

/**	def offTime = defOffTime 

	def foundOff = hues.find{it.currentValue("sceneSwitch") == "Master"} 

    if (foundOff) {
    	offTime = foundOff.currentValue("offTime") 
		log.debug "The Master light is ${foundOff}.  Using the Offtime for that light."        
    } else {
		log.debug "No Master light found.  Using the default Offtime."    
    }
**/

    return modeOffTime  

}

def checkHome() {

	def result = false
    
   	if (allPeopleHome() && allVirtualHome()) {
    
        result = true
        log.debug "allHome is true"
	}
	return result
}      


def allPeopleHome() {

	def result = true
	for (person in people) {
		if (person.currentPresence == "not present") {
			result = false
			break
		}
	}
	log.debug "allPeopleHome: $result"
	return result
}

def allVirtualHome() {

	def result = true
	for (person in virtual) {
		if (person.currentValue("switch") == "off") {
			result = false
			break
		}
	}
	log.debug "allVirtualHome: $result"
	return result
}



def colorCheck() {

	def valueColor = defColor as String
    def newHue = 23
    def newSat = 56
    
	switch(valueColor) {
				
		case "Normal":
			newHue = 52
			newSat = 19
			break;
						
		case "Daylight":
			newHue = 53
			newSat = 91
			break;
                            
		case "Soft":
			newHue = 23
			newSat = 56
			break;
        	                
		case "Warm":
			newHue = 20
			newSat = 80 //83
			break;
    	                    
		case "Blue":
			newHue =  70
			newSat = 100
           	break;
                        
		case "Green":
			newHue = 39
    	    newSat = 100
			break;
                        
		case "Yellow":
        	newHue = 25
			newSat = 100			
    	   	break;
        	                
		case "Orange":
			newHue  = 10
			newSat = 100
			break;
                        
		case "Purple":
			newHue = 75
			newSat = 100
	        break;
                        
		case "Pink":
			newHue = 83
			newSat = 100
		    break;
                        
		case "Red":
			newHue = 100
			newSat = 100                       
			break;
                        
	}

	state.defHue = newHue
    state.defSat = newSat
    
}

def levelCheck(evt) {
	
//   	log.debug "Reached levelCheck.  "
    
    def phyTest = evt.isPhysical()
	if ( phyTest ) {
		hues?.each { 
//    		it.poll()
//	    	def curLevel = it.currentValue("level")
//    		log.debug "Light ${it.label} is level ${curLevel}."
//        	if (curLevel == "100") {	      	
    		log.debug "Detected manual switch used - adjusting ${it.label} to current Scene settings."
			
	        def theLight = hues.find{it.id == evt.deviceId} 
    	    log.trace "LevelCheck: The switch for ${theLight} was physically turned on."
        
			pause(1000)
	        turnON()
    	    state.lastCheck = now()
        }
    }    
}        
        

def turnON() {							// YEAH, baby!
 
	def theMode = location.mode as String // state.currentMode as String 
    
 	def myScene = null
	def masterLight = null    

    masterLight = hues.find{it.currentValue("sceneSwitch") == "Master"}
      	log.debug "masterLight is ${masterLight}."
        
	def masterName = masterLight.displayName
      	log.debug "masterName is ${masterName}."        
        
	if (hues) {
	
        hues?.each {
           	def myHueLevel = null                         
            def scnHue = null
			def scnSat = null
       	    def scnType = null
                      
            myScene = it.currentValue("sceneSwitch")
           	log.debug "${it.label} sceneSwitch is ${myScene}."
                
			if ( myScene == "Master" || myScene == "Slave" ) {

				def masterData = parent.sendChildMasterInfo(masterName, theMode) 
                    	log.debug "${app.label}: turnON : masterData is ${masterData}."                        
    
                myHueLevel = masterData.level 
                    	log.debug "${app.label}: turnON : masterData.level is ${masterData.level}."                        
                        
	            log.debug "${it.label} sceneSwitch is ${myScene}."
                    

	   			scnHue = masterData.hue 
                    	log.debug "${app.label}: turnON : masterData.hue is ${masterData.hue}."                                        
				scnSat = masterData.saturation 
                    	log.debug "${app.label}: turnON : masterData.saturation is ${masterData.saturation}."                                        

				scnType = "Master"

            } else if ( myScene == "Freebie") {
            
				def freeData = parent.sendChildFreeInfo(it.displayName, theMode)
                
            myHueLevel = freeData.level as Number             

	   			scnHue = freeData.hue as Number
				scnSat = freeData.saturation as Number

				scnType = "Free"

			} else if ( myScene == "Manual" ) {

				myHueLevel = it.currentValue("level")   		   			

           	    scnHue = it.currentValue("hue")    
				scnSat = it.currentValue("saturation")

				scnType = "Manual"

            } else { 
				myHueLevel = defLevel as Number 

  	            scnHue = state.defHue as Number    
				scnSat = state.defSat as Number    

   	        	if (myHueLevel > 99) {
       	       		myHueLevel = 99
           	    }    

				scnType = "Default"		            
            }    
            
          	def newValueColor = [hue: scnHue, saturation: scnSat, level: myHueLevel, transitiontime: 2, switch: "on"]
                log.debug "${it.label} is using ${scnType} settings of ${newValueColor}."                        
  	        it.setColor(newValueColor)                    

		}
    }
    
    if (dimmers) {
            
       	dimmers?.each {
			def myDimLevel = null        
            def myDimType = null
                                        
	        myScene = it.currentValue("sceneSwitch")
            log.debug "${it.label} sceneSwitch is ${myScene}."
                
			if ( myScene == "Master" || myScene == "Slave" ) {
	
 	            myDimLevel = parent.sendChildDimLevel(it.displayName, theMode) 
				myDimType = "Master"              

			} else if ( myScene == "Freebie") {
            					
                myDimLevel = it.currentValue("sceneLevel") 
				myDimType = "Free"

			} else if ( myScene == "Manual" ) {
            
				myDimLevel = it.currentValue("level")   		   			
				myDimType = "Manual"

            } else { 
            	
				myDimLevel = defLevel as Number 
				myDimType = "Default"
                
	            if (myDimLevel > 99) {
    	          	myDimLevel = 99
        	    }    
                
			}
			
			log.debug "${it.label} is using ${myDimType} settings - myDimLevel is ${myDimLevel}."                        
	        it.setLevel(myDimLevel)		            
            
		}
    }

	if (switches) {
    	
  		switches?.each {
    		
            if (it.currentValue("switch") == "off") {
	            def theSwitchName = it.displayName as String
        	    theSwitchName = theSwitchName.tr(' !+', '___')
            	log.debug "${app.label}: the switch ID is ${it.id} and its name is ${theSwitchName}."
            
				def theSwitchState = parent.sendChildSwitchState(theSwitchName, theMode)
    	        log.debug "${app.label} retrieved ${theSwitchState} for ${theSwitchName}."
            
        	    if (theSwitchState == "yes") {
      				it.on()
		      	}    
			}
        }
	}   
}

// Handle motion event.
def motionHandler(evt) {

	def currentMode = location.mode
        
    
   	log.trace "${app.label}'s selected modes are ${xModes}.  Current mode is ${currentMode}." 

    if ( xModes.contains(currentMode) ) {
    	log.trace "onMotion:  Current mode of ${currentMode} is within ${app.label}'s selected modes}." 
        
		def theSensor = motions.find{it.id == evt.deviceId} // evt.deviceId
	
		if (checkHome) {

	    	if (evt.value == "active") {
				log.trace "${theSensor.label} detected motion - resetting state.inactiveAt to null & calling turnON()."
				state.inactiveAt = null
        	    turnON()   
		  		state.abortWindow = null
            
	        } else if (evt.value == "inactive") {
				log.trace "${theSensor.label} detected NO motion." 
				state.inactiveAt = now()
            	log.trace "- setting state.inactiveAt to ${now}."    
            
	       	  	if (state.timeOfAbort) {
   		            log.trace "...but abort active."    
        	    } else {
   	        	    log.trace "....and running setActiveAndSchedule."    
            		setActiveAndSchedule()
	            }
    	    }       			   
        
		} else {
    
			log.trace "Motion event, but checkHome is not true."
	    }   
        
	} else {        
    
    	log.trace "onMotion:  Current mode of ${currentMode} is NOT within ${app.label}'s selected modes}." 
	
    }        
}

// Handle contact event.
def contactHandler(evt) {
	def currentMode = location.mode
   	log.trace "${app.label}'s selected modes are ${xModes}.  Current mode is ${currentMode}." 

    if ( xModes.contains(currentMode) ) {
    	log.trace "onContact:  Current mode of ${currentMode} is within ${app.label}'s selected modes}." 

		def theSensor = motions.find{it.id == evt.deviceId}
	
		if (checkHome) {
	    	if (evt.value == "open") {
				log.trace "${theSensor.label} opened -- resetting state.inactiveAt to null & calling turnON()."
				state.inactiveAt = null
				turnON()   
			  	state.abortWindow = null
            
	    	} else {
				log.trace "${theSensor.label} closed -- setting state.inactiveAt to ${now()}."        
					// When contact closes, reset the timer if not already set
 
 				state.inactiveAt = now()
        		setActiveAndSchedule()
	        }  

		} else {
    
			log.debug "Contact event, but checkHome is not true."

		}    
    } else {        
    
    	log.trace "onContact:  Current mode of ${currentMode} is NOT within ${app.label}'s selected modes}." 
	
    }   
}


// Handle location event.
def onLocation(evt) {
    
    def currentMode = evt.value
   	log.trace "${app.label}'s selected modes are ${xModes}.  Current mode is ${currentMode}." 

    if ( xModes.contains(currentMode) ) {
    
    	log.trace "onLocation:  Current mode of ${currentMode} is within ${app.label}'s selected modes}." 
    	pause(500)
    	
    	state.lastMode = state.currentMode

		state.currentMode = currentMode
        
	    state.theOffTime = checkOff()
		
        state.inactiveAt = now()      
        
		log.debug "Mode offTime is ${state.theOffTime}."
	    log.trace "NEW MODE: state.inactiveAt = ${state.inactiveAt} & calling setActiveAndSchedule()."    
    	setActiveAndSchedule() 

	} else {
    
       	log.trace "onLocation:  Current mode of ${currentMode} is NOT within ${app.label}'s selected modes}." 
    
    }
}


def setActiveAndSchedule() {
    unschedule("scheduleCheck")
    
    def myOffTime = checkOff() as Number
    state.myOffTime = myOffTime
    log.debug "setActiveAndSchedule:  checkOff() sent ${myOffTime} as the offTime mins."
    def mySchTime = myOffTime * 15		// check monitored lights every 1/4 of offTime limit (in seconds)
    log.debug "setActiveAndSchedule:  mySchTime (myOffTime * 15) is ${mySchTime}."
    if (mySchTime < 120) {				// BUT check no more than every 2 minutes	    
            mySchTime = 120
    }    
    
    log.debug "setActiveAndSchedule: running scheduleCheck in ${mySchTime} seconds."    
	runIn (mySchTime, "scheduleCheck")   
}

def scheduleCheck() {
    log.debug "scheduleCheck:  "
    if(state.inactiveAt != null) {

        def minutesOff = state.myOffTime as Number 
        log.debug "scheduleCheck:  Mode offTime is ${minutesOff}."
	    def elapsed = now() - state.inactiveAt
        def threshold = 60000 * minutesOff 
        log.debug "scheduleCheck: elapsed = ${elapsed} / threshold = ${threshold}."		

        if (elapsed >= threshold) {                     
            log.debug "scheduleCheck: elapsed > threshold.  Running turningOff."
            
    //    	if (abortToggle && checkHome) {
//            if (checkHome) {
            							// check for previous abort within abortLength minutes
//				if (noPrevAbort) {

//       				sendPush("ST Trig No Motion from ${motions.label} -- abort?") 
//					state.abortWindow = "Valid"        
        	        
//                	unschedule("turningOff")
//            	    def abortWindow = abortTime as Number
//	                abortWindow = abortWindow * 60            
			    
//    	           	runIn (abortWindow, "turningOff")                            
                    
//                } else {
//                 	log.debug "scheduleCheck:  Previous abort still active - do nothing."
	      	    	                 
//                }
                
//   		    } else if (!checkHome) {
            	
//	           	log.debug "scheduleCheck:  No one home - turn off now."
      	    	turningOff()
        
//           	} else if (!abortToggle) {
                            
//                log.debug "scheduleCheck:  no abortToggle found - turn off now."
//	            turningOff()
                    
//        	}    
            
                } else {
        
                setActiveAndSchedule()
        
        }
    } else {
    	log.debug "scheduleCheck:  state.inactiveAt is null, so setting it = now()."
    	state.inactiveAt = now()
        setActiveAndSchedule()
    
    }    
}

/**
def noPrevAbort() {

	def result = false
    
    if (state.timeOfAbort) {    
	
    	def abElapsed = now() - state.timeOfAbort
		def abThreshold = 60000 * abortLength
//	    log.debug "elapsed = ${abElapsed} / abort threshold = ${abThreshold}."
		
    	if (abElapsed >= abThreshold) {                     
			result = true 
		
		}
        
    } else {    
		result = true 
        
	}
    log.debug "noPrevAbort is ${result}."
	return result

}

def abortHandler(evt) {

	log.trace "abortHandler:  abortToggle pushed. "
    log.debug "Receieved evt is ${evt} & evt.value is ${evt.value}."

    if (state.abortWindow == "Valid") {
    
    	unschedule("turningOff")
		state.abortWindow = null
		sendNotificationEvent("Abort received - will ignore No Motion events for ${abortLength} mins.")         
        log.trace "abortHandler:  Received abort command within the abortWindow.  Unscheduling turningOff()."
		state.timeOfAbort = now()
        
        runIn (abortLength, "clearAbort") 
        
    } else {
    
        log.trace "abortHandler:  Received abort command, but not within the abortWindow."
    }    
    
}

def clearAbort() {

	state.timeOfAbort = null
    scheduleCheck()

}

**/

def turningOff () {

		state.abortWindow = null
        state.inactiveAt = null        
        
		log.trace "Executing turningOff() . "
 

			if (hues) {
	            hues?.each {
    	          	it.off()
	    	    }
            }
            if (dimmers) {
	            dimmers?.each {
    	        	it.off()
        	    }    
	        }
            if (switches) {
	            switches?.each {
    	        	it.off()
        	    }    
	        }


}


private def TRACE(message) {
    log.debug message
}

private def STATE() {
    log.trace "settings: ${settings}"
    log.trace "state: ${state}"
}





