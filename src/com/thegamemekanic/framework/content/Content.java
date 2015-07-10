package com.thegamemekanic.framework.content;

import java.util.HashMap;

public final class Content {
	
	//=================================================================================================================
	// Member Variables
	
	private HashMap<String, ContentLoader> _loaders;
	
	
	
	//=================================================================================================================
	// Constructor
	
	/**
	 * Create a new instance of a Content Manager.
	 */
	public Content() {
		_loaders = new HashMap<String, ContentLoader>();
	}
	
	
	
	//=================================================================================================================
	// Public Interface
	
	/**
	 * Register a content loader to handle loading files of the type specified by the loader.
	 * @param loader - Content Loader to register.
	 */
	public final void registerLoader(ContentLoader loader) {
		if (loader != null) {
			_loaders.put(loader.getExtension(), loader);
		}
		
	}
	
	public final <T> T load(String filePathName) {
		T content = null;
		
		if(filePathName != null && !filePathName.isEmpty()) {
			int extIndex = filePathName.lastIndexOf('.');
			int verifyPath = Math.max(filePathName.lastIndexOf('\\'), filePathName.lastIndexOf('/'));
			
			if(extIndex > verifyPath) {
				String ext = filePathName.substring(extIndex + 1);
				ContentLoader loader = _loaders.get(ext);
				
				if(loader != null) {
					// TODO: TMG - Call load method of some description.
				}
			}
		}
		
		return content;
	}

}
