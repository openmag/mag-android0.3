package com.anheinno.android.libs.mag;

public interface MAGContainerInterface extends MAGComponentInterface {
	
	void addChild(MAGComponentInterface child);
	
	void removeChild(MAGComponentInterface child);
	
	int childrenNum();
	
	MAGComponentInterface getChild(int idx);
	
	MAGComponentInterface getChild(String id);
	
	MAGComponentInterface[] getNamedChildren();
	
	// void setLayoutManager(MAGLayoutManager manager);
	
    // void removeLayoutManager();
		
	void invalidateChild(MAGComponentInterface comp);
	
}
