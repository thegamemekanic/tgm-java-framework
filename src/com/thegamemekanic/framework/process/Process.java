package com.thegamemekanic.framework.process;

/**
 * A Process is best used for anything that needs to update as a chain such that once the process is finished, it 
 * pushes the child process to the process manager so that it can be updated.
 * 
 * <p>
 * <b>Example:</b> Timed Bomb: Simply a combination of a Timer process followed by an Explosion process. Once the 
 * Timer process is finished, the Explosion begins. Once the explosion is finished, the process chain is complete!
 * </p>
 * 
 * @author The Game Mechanic
 */
public abstract class Process {
	
	//=================================================================================================================
	// Member Variables
	
	private ProcessState _state = ProcessState.UNINITIALIZED;
	private Process _child;
	
	
	
	//=================================================================================================================
	// Public Interface
	
	/**
	 * Attaches a child process to the end of the process chain. The child process becomes active when the parent
	 * process completes.
	 * 
	 * @param childProcess - Process to add to the end of the process list.
	 */
	public final void attachChild(Process childProcess) {
		if(_child == null) {
			_child = childProcess;
		} else {
			_child.attachChild(childProcess);
		}
	}
	
	/**
	 * @return True if the process is running or paused, false otherwise.
	 */
	public final boolean isAlive() {
		return _state == ProcessState.RUNNING || _state == ProcessState.PAUSED;
	}
	
	/**
	 * @return True if the process is completed: succeeded, failed, or aborted.
	 */
	public final boolean isDead() {
		return _state == ProcessState.SUCCEEDED || _state == ProcessState.FAILED || _state == ProcessState.ABORTED;
	}
	
	/**
	 * Pauses the process. Only works if the process is running to prevent attempting to pause a process that has been
	 * completed, or hasn't even started.
	 */
	public final void pause() {
		if(_state == ProcessState.RUNNING) {
			_state = ProcessState.PAUSED;
		}
	}
	
	/**
	 * Resumes the process. Only works if the process is paused to prevent attempting to resume a process that has been
	 * completed, or hasn't even started.
	 */
	public final void resume() {
		if(_state == ProcessState.PAUSED) {
			_state = ProcessState.RUNNING;
		}
	}
	
	/**
	 * Ends the process as a success.
	 */
	public final void succeed() {
		_state = ProcessState.SUCCEEDED;
	}
	
	/**
	 * Ends the process as a failure.
	 */
	public final void fail() {
		_state = ProcessState.FAILED;
	}
	
	
	
	//=================================================================================================================
	// Customizable Methods

	/**
	 * Called once before the calls to update begin.
	 */
	protected abstract void initialize();
	
	/**
	 * Called until the process is ended, either by succeeding, failing, or aborting.
	 * @param delta
	 */
	protected abstract void update(float delta);
	
	/**
	 * Called when the process succeeds.
	 */
	protected void onSuccess() { } 
	
	/**
	 * Called when the process fails.
	 */
	protected void onFail() { }
	
	/**
	 * Called when the process is aborted.
	 */
	protected void onAbort() { }
	
	
	
	//=================================================================================================================
	// Accessors
	
	/**
	 * @return The current state of the process.
	 */
	public final ProcessState getState() {
		return _state;
	}
	
	
	
	//=================================================================================================================
	// Package Accessors
	
	final Process getChild() {
		return _child;
	}
	
	/**
	 * @param state - State to set the process to.
	 */
	final void setState(ProcessState state) {
		_state = state;
	}
	
}
