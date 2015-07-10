package com.thegamemekanic.framework.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Process Manager handles adding, updating, and removing processes.
 * 
 * @author The Game Mechanic
 */
public final class ProcessManager {
	
	//=================================================================================================================
	// Member Variables
	
	private List<Process> _processes;
	
	
	
	//=================================================================================================================
	// Constructor
	
	/**
	 * Create an instance of a Process Manager
	 */
	public ProcessManager() {
		_processes = new ArrayList<Process>();
	}
	
	
	
	//=================================================================================================================
	// Public Interface
	
	/**
	 * Add a process to the process manager. 
	 * @param process - Process to be added.
	 */
	public final void AddProcess(Process process) {
		if(process != null && (process.getState() == ProcessState.UNINITIALIZED || process.isAlive())) {
			_processes.add(process);
		}
	}
	
	/**
	 * Updates all running processes. Successful process will promote their child process to be handled by the process
	 * manager. If the process fails or is aborted, child processes are dropped along with it.
	 * @param delta
	 */
	public final void update(float delta) {
		
		Iterator<Process> iter = _processes.iterator();
		
		while(iter.hasNext()) {
			Process process = iter.next();
			
			// Initialize processes
			if(process.getState() == ProcessState.UNINITIALIZED) {
				process.initialize();
				process.setState(ProcessState.RUNNING);
			}
			
			// Update running processes
			if(process.getState() == ProcessState.RUNNING) {
				process.update(delta);
			}
			
			// Handle completed processes
			if(process.isDead()) {
				switch(process.getState()) {
				
				// Needs to add the child process to the manager if it exists
				case SUCCEEDED: 
					process.onSuccess();
					
					if(process.getChild() != null) {
						AddProcess(process.getChild());
					}
					
					break;
				
				case FAILED:
					process.onFail();
					break;
				
				case ABORTED:
					process.onAbort();
					break;
					
				default: break;
				}
				
				iter.remove();
			}
		}
	}
	
	
	
	//=============================================================================================
	// Accessors
	
	/**
	 * @return Number of processes being handled by the Process Manager.
	 */
	public final int getProcessCount() {
		return _processes.size();
	}
}
