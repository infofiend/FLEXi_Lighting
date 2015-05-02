# FLEXI_Lighting 
FLEXi Lighting Apps, Devices, and examples and extras 

--HOW TO INSTALL--

1) copy the FLEXiHue (Hue Connect) Service Manager code and create it as a new SmartApp in the IDE.

2) copy the FLEXiHue device code and create it as a new Device Type in the IDE.

3) If you want to be able to use dimmer lights other than Hue lights (or the Aeon Micro G2 that I use as an example), copy the FLEXi-related attributes and commands into whatever device type code and save as a new device type in IDE.  Everything that is needed can be found in the "Add_this_code_to_create_a_new_FLEXi_dimmer" file.  Be sure to change the name in metadata section to "flexidimmer".  In the FlexiDimmer example code, the FLEXi-related code I added is: custom attributes (lines 40-45) and commands (lines 53-63 and lines 221-348).

4) copy the FlexiState device code and create it as a new Device Type in the IDE.  
  ------OR change the subscription in the FLEXi Trigger app to subscribe to presence directly. -----
  
5) FLEXiHue (Hue Connect) Service Manager and connect the Hue Bulbs.  The Service Manager will use the FLEXiHue device type.

6) Create a Mode (in the SmartThings DashBoard App or IDE) for every Lighting Scene that you want to set up.  For instance, "Morning", "Daytime", "Afternoon", "Dinner", "Late", "Movies", "Vacation", "Blue Steel", etc.  You can set up a different version of the Scene and Trigger App for each room, so its ok if you create a mode that will only be used by a single room (such as "Dinner").

7) copy the FLEXi Lighting Scenes code and create it as a new SmartApp in the IDE.  Install and set up scenes.  

8) copy the FLEXi Triggers code and create it as a new SmartApp in the IDE.  Install and set up triggers.

9) Repeat 7 & 8 for as many rooms as you want.
