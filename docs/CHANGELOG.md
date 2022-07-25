Gloop-GLFW (C) Black Rook Software 
==================================
by Matt Tropiano et al. (see AUTHORS.txt)


Changed in 0.4.0
----------------

- `Added` MouseScrollType for scrolling actions.
- `Removed` Some redundant enums for keys/buttons - they were affecting lookups.


Changed in 0.3.0
----------------

- `Added` GLFWWindow now has a queryable state that can be fetched outside of the main thread.


Changed in 0.2.0
----------------

- `Added` Joystick events for discrete directions.
- `Changed` GLFWContext's main loop method was turned into a separate object.


Changed in 0.1.1
----------------

- `Added` Some event polling methods, changed main loop to wait for events with timeout.
- `Changed` Removed some calls to init() that would make no sense.
- `Changed` Renamed the context setting.


Changed in 0.1.0
----------------

- Base release.
