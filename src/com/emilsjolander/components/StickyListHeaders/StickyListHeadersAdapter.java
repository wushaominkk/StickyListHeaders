package com.emilsjolander.components.StickyListHeaders;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * @author Emil Sj�lander
 * 
 * 
Copyright 2012 Emil Sj�lander

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 *
 */
public abstract class StickyListHeadersAdapter extends BaseAdapter {
	
	private ArrayList<View> headerCache;
	private ArrayList<WrapperView> wrapperCache;
	private Context context;
	private HashMap<Integer, View> currentlyVissibleHeaderViews;
	
	public StickyListHeadersAdapter(Context context) {
		headerCache = new ArrayList<View>();
		wrapperCache = new ArrayList<WrapperView>();
		currentlyVissibleHeaderViews = new HashMap<Integer, View>();
		this.context = context;
	}
	
	public abstract View getHeaderView(int position, View convertView);
	
	/**
	 * 
	 * @param position
	 * the list position
	 * @return
	 * an identifier for this header, a header for a position must always have a constant ID
	 */
	public abstract long getHeaderId(int position);
	protected abstract View getView(int position, View convertView);
	
	private View getHeaderWithForPosition(int position){
		View header = null;
		if(headerCache.size()>0){
			header = headerCache.remove(0);
		}
		header = getHeaderView(position,header);
		header.setId(R.id.header_view);
		return header;
	}
	
	private View attachHeaderToListItem(View header, View listItem){
		listItem.setId(R.id.list_item_view);
		WrapperView wrapper = null;
		if(wrapperCache.size()>0){
			wrapper = wrapperCache.remove(0);
		}
		if(wrapper == null){
			wrapper = new WrapperView(context);
		}
		//this does so touches on header are not counted as listitem clicks
		header.setClickable(true);
		header.setFocusable(false);
		return wrapper.wrapViews(header,listItem);
	}

	private View wrapListItem(View listItem) {
		listItem.setId(R.id.list_item_view);
		WrapperView wrapper = null;
		if(wrapperCache.size()>0){
			wrapper = wrapperCache.remove(0);
		}
		if(wrapper == null){
			wrapper = new WrapperView(context);
		}
		return wrapper.wrapViews(listItem);
	}
	
	/**
	 * puts header into headerCache, wrapper into wrapperCache and returns listItem
	 * if convertView is null, returns null
	 */
	private View axtractHeaderAndListItemFromConvertView(View convertView){
		if(convertView == null) return null;
		if(currentlyVissibleHeaderViews.containsValue(convertView)){
			currentlyVissibleHeaderViews.remove(convertView.getTag());
		}
		ViewGroup vg = (ViewGroup) convertView;
		
		View header = vg.findViewById(R.id.header_view);
		if(header!=null){
			headerCache.add(header);
		}
		
		View listItem = vg.findViewById(R.id.list_item_view);
		vg.removeAllViews();
		wrapperCache.add(new WrapperView(convertView));
		
		return listItem;
	}
	
	/**
	 * 
	 * !!!DO NOT OVERRIDE THIS METHOD!!!
	 * !!!DO NOT OVERRIDE THIS METHOD!!!
	 * !!!DO NOT OVERRIDE THIS METHOD!!!
	 * 
	 * Override getView(int position,View convertView) instead!
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = getView(position,axtractHeaderAndListItemFromConvertView(convertView));
		if(position == 0 || getHeaderId(position)!=getHeaderId(position-1)){
			v = attachHeaderToListItem(getHeaderWithForPosition(position),v);
			currentlyVissibleHeaderViews.put(position, v);
		}else{
			v = wrapListItem(v);
		}
		v.setTag(position);
		return v;
	}

	public Context getContext() {
		return context;
	}
	
	public HashMap<Integer, View> getCurrentlyVissibleHeaderViews() {
		return currentlyVissibleHeaderViews;
	}

}
