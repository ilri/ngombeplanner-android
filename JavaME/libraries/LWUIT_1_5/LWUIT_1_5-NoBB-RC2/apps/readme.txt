The Demos in this directory are library project hence they can't be executed directly. These projects are parent projects that contains the shared (portable) sources and resources of the demo.
Under each project directory you will find the actual platform specific projects for RIM, MIDP, JavaSE&  sometimes CDC.
For example the LWUITDemo directory contains the projects LWUITDemoMIDP, LWUITDemoRIM, LWUITDemoDesktop, etc..
Make sure to compile the base and the project beneath it to run the demo. 