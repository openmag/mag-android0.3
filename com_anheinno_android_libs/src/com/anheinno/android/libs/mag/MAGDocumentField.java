package com.anheinno.android.libs.mag;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.anheinno.android.libs.JSONBrowserConfigScreen;
import com.anheinno.android.libs.JSONBrowserField;

public class MAGDocumentField extends JSONBrowserField implements MAGContainerLayoutInterface {
	private MAGLayoutManager _mag_layout;
	private MAGDocument _document;
	private MAGDocumentContainerFieldInterface _container_field;
	
	public MAGDocumentField(Context context, MAGDocumentContainerFieldInterface container) {
		super(context);
		_container_field = container;
		_mag_layout = new MAGLayoutManager(context);
		setContent(_mag_layout);
	}
	
	public MAGDocument getMAGDocument() {
		return _document;
	}
	
	public void showJSON(JSONObject o, Object params) {
		MAGDocumentFieldShowJSONTask show_task = new MAGDocumentFieldShowJSONTask(getContext(), this);
		show_task.execute(new JSONObject[] {o});
	}
	
	static class MAGDocumentFieldShowJSONTask extends AsyncTask<JSONObject, Integer, MAGDocument> {
		private Context _context;
		private MAGDocumentField _field;
		
		MAGDocumentFieldShowJSONTask(Context context, MAGDocumentField field) {
			_context = context;
			_field = field;
		}
		
		@Override
		protected MAGDocument doInBackground(JSONObject... params) {
			JSONObject o = params[0];

			MAGDocument new_doc = new MAGDocument(_context, _field);
			if(new_doc.fromJSON(o)) {
				MAGContainerInterface parent = null;
				
				if(_field._container_field != null) {
					parent = _field._container_field.getMAGContainer();
					if(parent != null) {
						if(_field._document != null) {
							parent.removeChild(_field._document);
						}
						new_doc.setParent(parent);
					}
				}
				
				return new_doc;
			}else {
				return null;
			}
		}
		
	    protected void onPostExecute(MAGDocument new_doc) {
	    	super.onPostExecute(new_doc);
	    	
	    	if(new_doc != null) {
		    	new_doc.initField(_context);
		 		
		 		if(_field._document != null) {
		 			_field._document.releaseResources();
		 			_field._document = null;
		 		}
		 		
		 		_field._document = new_doc;
		 		
		 		_field.setScrollPosition(0, 0);
		 		_field._container_field.reset();
		 		
		 		_field._mag_layout.setContainer(_field._document);
		 		_field._document.initUi();
		 		
	    	}
	    	
	 		_field.endDownloading();
	    	
	    }
	    
	    protected void onCancelled() {
	    	super.onCancelled();
	    	_field.endDownloading();
	    }
		
	}
	
	public void unloadContainer() {
		if(_container_field != null) {
			_container_field.unload();
		}
	}
	
	protected void unload() {
		super.reset();
		MAGContainerInterface parent = _container_field.getMAGContainer();
		if(_document != null && parent != null) {
			_document.releaseResources();
			parent.removeChild(_document);
		}
		_document = null;
		_mag_layout.releaseResources();
	}
	
	public MAGDocumentContainerFieldInterface getMAGDocumentContainer() {
		return _container_field;
	}

	@Override
	protected JSONBrowserConfigScreen getConfigScreen() {
		return new MAGDocumentConfigScreen(getContext());
	}

	public void invalidateMAGComponent(MAGComponentInterface comp) {
		if(_mag_layout != null) {
			_mag_layout.invalidateMAGComponent(comp);
		}
	}
	
	public void releaseResources() {
		//System.out.println("MAGDocumentField::releaseResources is called");
				
		_mag_layout.releaseResources();
		_mag_layout = null;
		if(_document != null) {
			_document.releaseResources();
			_document = null;
		}
		_container_field = null;
		
		super.releaseResources();
	}

}
