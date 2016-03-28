package com.liferay.docs.guestbook.portlet;
import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.docs.guestbook.model.Entry;
import com.liferay.docs.guestbook.model.Guestbook;
import com.liferay.docs.guestbook.service.EntryLocalServiceUtil;
import com.liferay.docs.guestbook.service.GuestbookLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
/*
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.ServiceContextFactory;

import com.liferay.docs.guestbook.model.Entry;
*/
import com.liferay.util.bridges.mvc.MVCPortlet;
/**
 * Portlet implementation class GuestPortlet
 */
public class GuestPortlet extends MVCPortlet {

/*
	public void addEntry(ActionRequest request,ActionResponse response){
		try{
			PortletPreferences prefs = request.getPreferences();
			
			String[] guestbookEntries = prefs.getValues("guestbook-entries",new String[1]);
			ArrayList<String> entries = new ArrayList<String>();
			
			if (guestbookEntries != null) {
	
		         entries = new ArrayList<String>(Arrays.asList(prefs.getValues(
		              "guestbook-entries", new String[1])));
	
	       }
			
			String name = ParamUtil.getString(request, "name");
			String message = ParamUtil.getString(request, "message");
			String entry = name + "^" + message;
			
			entries.add(entry);
			
			String[] array = entries.toArray(new String[(entries.size())]);
			prefs.setValues("guestbook-entries", array);
			
			try {
				prefs.store();
			} catch (IOException ex) {
				
				Logger.getLogger(GuestPortlet.class.getName()).log(Level.SEVERE,null,ex);
			
			} catch(ValidatorException ex){
				
				Logger.getLogger(GuestPortlet.class.getName()).log(Level.SEVERE,null,ex);
			}
		}catch(ReadOnlyException ex){
			Logger.getLogger(GuestPortlet.class.getName()).log(Level.SEVERE,null,ex);
		}
		
	}

	public void render(RenderRequest request,RenderResponse response) throws PortletException, IOException{
		
		PortletPreferences prefs = request.getPreferences();
		String[] guestbookEntries = prefs.getValues("guestbook-entries", new String[1]);
		
		if(guestbookEntries!=null){
			List<Entry> entries = parseEntries(guestbookEntries);
			request.setAttribute("entries", entries);
		}
		
		super.render(request, response);
	}
	
	private List<Entry> parseEntries(String[] guestbookEntries){
		ArrayList<Entry> entries = new ArrayList();
		
		for(String entry:guestbookEntries){
			String[] parts = entry.split("\\^",2);
			Entry sEntry = new Entry(parts[0],parts[1]);
			entries.add(sEntry);
			
		}
		return entries;
		
	}
*/
	public void addGuestbook(ActionRequest request,ActionResponse response) 
			throws PortalException, SystemException{
		
		ServiceContext serviceContext = ServiceContextFactory.getInstance(Guestbook.class.getName(),request);
		
		String name = ParamUtil.getString(request, "name");
		
		try{
			GuestbookLocalServiceUtil.addGuestbook(serviceContext.getUserId(),
					name,serviceContext);
			SessionMessages.add(request, "guestbookAdded");
		}catch(Exception e){
			SessionErrors.add(request, e.getClass().getName());
			
			response.setRenderParameter("mvcPath", "/html/guest/edit_guestbook.jsp");
		}
		
	}
	
	public void addEntry(ActionRequest request,ActionResponse response) 
			throws PortalException, SystemException{
		
		ServiceContext serviceContext = ServiceContextFactory.getInstance(Entry.class.getName(),request);
		
		String name = ParamUtil.getString(request, "name");
		String email = ParamUtil.getString(request, "email");
		String message = ParamUtil.getString(request, "message");
		long guestbookId = ParamUtil.getLong(request,"guestbookId");
		
		try{
			EntryLocalServiceUtil.addEntry(serviceContext.getUserId(),guestbookId,
					name,email,message,serviceContext);
			SessionMessages.add(request,"entryAdded");
			
			response.setRenderParameter("guestbookId", Long.toString(guestbookId));
					
		}catch(Exception e)
		{
			SessionErrors.add(request,e.getClass().getName());
			response.setRenderParameter("mvcPath", "/html/guest/edit_entry.jsp");
		}
	}
	
	public void render(RenderRequest renderRequest,RenderResponse renderResponse) throws PortletException, IOException{
		try{
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
					Guestbook.class.getName(),renderRequest);
			
			long groupId = serviceContext.getScopeGroupId();
			long guestbookId = ParamUtil.getLong(renderRequest,"guestbookId");
			
			List<Guestbook> guestbooks = GuestbookLocalServiceUtil.getGuestbooks(groupId);
			
			if(guestbooks.size()==0){
				Guestbook guestbook = GuestbookLocalServiceUtil.addGuestbook(
						serviceContext.getUserId(), "Main", serviceContext);
				guestbookId = guestbook.getGuestbookId();
			}
			
			if(!(guestbookId>0)){
				
				guestbookId = guestbooks.get(0).getGuestbookId();
			}
			
			renderRequest.setAttribute("guestbookId", guestbookId);
			
		}catch(Exception e){
			
			throw new PortletException(e);
		}
		
		super.render(renderRequest, renderResponse);
	}
}
