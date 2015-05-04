# FLEXI_Lighting 
FLEXi Lighting Apps, Devices, and examples and extras 

--HOW TO INSTALL--

1) copy the code for the FLEXiHue (Hue Connect) Service Manager and create it as a new SmartApp in the IDE.

2) copy the code for the FLEXiHue device and create it as a new Device Type in the IDE.  Then do the same for the FLEXiHue Bridge device.

3) If you want to be able to use dimmer lights other than Hue lights (or the Aeon Micro G2 that I use as an example), copy the FLEXi-related attributes and commands into whatever device type code and save as a new device type in IDE.  Everything that is needed can be found in the "Add_this_code_to_create_a_new_FLEXi_dimmer" file.  Be sure to change the name in metadata section to "flexidimmer".  In the FlexiDimmer example code, the FLEXi-related code I added is: custom attributes (lines 40-45) and commands (lines 53-63 and lines 221-348).

4) (Optional) If you want to have the FLEXiTriggers look for a virtual presence device, copy the code for the FlexiState device and create it as a new Device Type in the IDE.  
  
5) From within SmartThings app, run the FLEXiHue (Hue Connect) Service Manager.  Follow the intructions to locate / connect your Hue Bridge and  Hue Bulbs.  The Service Manager will use the FLEXiHue Bridge and FLEXiHue device types (see #2 above).

6) The FLEXi system runs "scenes" from SmartThings' "Modes".  So you will need to create a different mode in the SmartThings App (or IDE) for For every Lighting Scene that you want to set up.  For instance, "Morning", "Daytime", "Afternoon", "Dinner", "Late", "Movies", "Vacation", "Blue Steel", etc.  

7) Copy the code for the FLEXi Lighting Scenes App and create it as a new SmartApp in the IDE.  Install and set up scenes.  If you have lights in different rooms and you want each room to run different scenes, this can be easily done by setting up multiple instances of the FLEXi Scene App.  For instance, I have a first FLEXi Scene App for my Dining Room and a second FLEXi Scene App for my Bedroom.  The Dining Room FLEXi Scene App handles the "Dinner" scene, but the Bedroom FLEXi Scene App does not.

8) Copy the code for the FLEXi Triggers App and create it as a new SmartApp in the IDE.  Install and set up triggers.  If you have multiple rooms and each room has its own motion sensor(s), then you should set up multiple instances of the FLEXi Triggers App.  For instance, I have a first FLEXi Trigger App for my Dining Room and a second FLEXi Trigger App for my Bedroom.  The Dining Room FLEXi Scene App handles the "motion" & "no motion" events from the Dining Room motion sensor, and the Bedroom FLEXi Trigger App handles the "motion" & "no motion" events from the Bedroom motion sensor.

