Sanity Check
============

The Sanity Add-On is intended to demonstrate basic field data access. It allows a user to configure a list of microblock display names that have interesting values along with minimum and maximum expected values. It can then perform a a basic sanity check to look for any current values outside of the specified ranges. This may be useful finding problems like open or shorted sensors.

Try it out
----------

Deploy the Sanity sample add-on by executing the 'deploy' task and starting (if necessary) the server.

Browse to `http://yourserver/sanity`. This should present a login page. Log in with any valid operator and password.

Add some values to check. Fill in some display names (like "Zone Temp"), minimum, and maximum values, then click the "Add" button.

Fill in a location (reference name path below the geographic root - like "building200/floor_1") and press the "Run" button to run the report from the specified location. If you leave the location blank, it will run from the root of the Geographic tree.

Important Lessons
-----------------

The Controller.java file is the entry point for most of the interesting parts of this sample. All requests for server side action are sent to this class. The doRun method runs this "report", using a ReadAction to find PresentValue aspects, retrieve their current value from the field, and compare it to stored limits.

SanityCheckConfig's load and save methods use DataStores to read and write the configuration information.