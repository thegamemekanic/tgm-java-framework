package com.thegamemekanic.framework.process;

/**
 * Possible states a process can be in.
 * 
 * @author The Game Mechanic
 */
enum ProcessState {
	// Initial state of a process.
	UNINITIALIZED,
	
	// Possible states during the lifetime of a process.
	RUNNING,
	PAUSED,
	
	// How the process finished.
	SUCCEEDED,
	FAILED,
	ABORTED,
}
