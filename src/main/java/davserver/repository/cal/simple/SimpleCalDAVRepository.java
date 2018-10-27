package davserver.repository.cal.simple;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import davserver.DAVException;
import davserver.DAVServer;
import davserver.DAVUtil;
import davserver.protocol.auth.IAuthenticationProvider;
import davserver.repository.Collection;
import davserver.repository.ILockManager;
import davserver.repository.Resource;
import davserver.repository.cal.BaseCalDAVRepository;
import davserver.repository.cal.CalendarCollection;
import davserver.repository.cal.VCalendar;
import davserver.repository.cal.resource.VEvent;
import davserver.repository.error.ConflictException;
import davserver.repository.error.LockedException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.repository.error.ResourceExistsException;
import davserver.utils.SimpleLockManager;
import ical4dav.caldav.iCalDAVParser;
import ical4dav.caldav.resources.CalDAVResource;
import ical4dav.caldav.resources.Calendar;
import ical4dav.caldav.resources.Event;
import ical4dav.properties.iCalComponent;

/**
 * CalDAV repository sample implementation 
 * 
 * @author tkrieger
 *
 */
public class SimpleCalDAVRepository extends BaseCalDAVRepository {

	/**
	 * Basic auth provider
	 */
	private IAuthenticationProvider authProvider;
	
	/**
	 * Lock manager
	 */
	private ILockManager lmanager;
	
	/**
	 * root collection 
	 */
	private CalendarCollection root;
	
	/**
	 * Defaultconstructor 
	 */
	public SimpleCalDAVRepository() {
		super();
		
		root     = new SimpleCalendarCollection("root");
		lmanager = new SimpleLockManager();
	}
	
	public void setAuthProvider(IAuthenticationProvider authProvider) {
		this.authProvider = authProvider;
	}
	
	@Override
	public int getProtocol() {
		return DAVServer.PROT_CARDDAV;
	}
	
	@Override
	public IAuthenticationProvider getAuthProvider() {
		return authProvider;
	}
	
	@Override
	public boolean needsAuth() {
		return false;
	}

	@Override
	public Resource locate(String uri) throws NotFoundException, NotAllowedException {
		System.out.println("locate resource: " + uri);
		
		// check root collection
		if (uri.compareTo("/")==0) {
			System.out.println("located root");
			return root;
		}
		
		List<String> comps = DAVUtil.getPathComps(uri);
		Collection cur = root;
		for (int i=0;i<comps.size()-1;i++) {
			System.out.println("comp step: " + comps.get(i));
			if (cur == null) {
				throw new NotFoundException(uri + " not found");
			}
			Resource r = cur.getChild(comps.get(i));
			if (r instanceof Collection) {
				cur = (Collection)r;
			} else {
				cur = null;
			}
		}
		
		if (cur == null) {
			throw new NotFoundException("resource not found");
		} else {
			System.out.println("check child: " + comps.get(comps.size()-1));
			Resource r = cur.getChild(comps.get(comps.size()-1));
			if (r == null) {
				throw new NotFoundException("not found");
			} else {
				return r;
			}
		}
	}

	@Override
	public void remove(String uri) throws IOException, NotFoundException, NotAllowedException, LockedException {
		System.out.println("locate resource: " + uri);
		
		// check root collection
		if (uri.compareTo("/")==0) {
			throw new NotAllowedException("root cannot be removed");
		}
		
		List<String> comps = DAVUtil.getPathComps(uri);
		System.out.println("path: " + comps.size() + ":" + root);
		Collection cur = root;
		for (int i=0;i<comps.size()-1;i++) {
			if (cur == null) {
				throw new NotFoundException(uri + " not found");
			}
			System.out.println("check " + comps.get(i));
			Resource r = cur.getChild(comps.get(i));
			if (r instanceof Collection) {
				cur = (Collection)r;
			} else {
				cur = null;
			}
		}
		
		if (cur == null) {
			throw new NotFoundException("resource not found");
		} else {
			String k = comps.get(comps.size()-1);
			if (cur.getChild(k) == null) {
				throw new NotFoundException("not found");
			} else {
				((SimpleCalendarCollection)cur).removeChild(k);
			}
		}
	}

	@Override
	public Collection createCollection(String ref)
			throws IOException, NotAllowedException, ResourceExistsException, ConflictException {
		List<String> comps = DAVUtil.getPathComps(ref);
		
		if (comps.size() == 0) {
			return root;
		} 
		
		CalendarCollection cur = root;
		for (int i=0;i<comps.size()-1;i++) {
			Resource r = cur.getChild(comps.get(i));
			if (r == null) {
				throw new ConflictException("no parent found");
			}
			if (!(r instanceof CalendarCollection)) {
				throw new NotAllowedException("parent is no collection");
			}
			cur = (CalendarCollection)r;
		}
		
		if (cur == null) {
			throw new ConflictException("no parent found");
		}
		
		String   last   = comps.get(comps.size()-1);
		Resource active = cur.getChild(last);
		
		if (active != null) {
			if (active instanceof CalendarCollection)
				return (CalendarCollection)active;
			else
				throw new ConflictException("exists as calendar");
		}
		
		// first level calendar collections, second level calendars
		if (comps.size() == 2) {
			SimpleCalendar cal = new SimpleCalendar(last);
			System.out.println("created calendar: " + last);
			((SimpleCalendarCollection)cur).addChild(last, cal);
			return cal;
		} else {
			SimpleCalendarCollection coll =  new SimpleCalendarCollection(last);			
			((SimpleCalendarCollection)cur).addChild(last, coll);
			return coll;
		}
		
	}

	@Override
	public Resource createResource(String ref, InputStream data)
			throws NotAllowedException, ConflictException, ResourceExistsException, NotFoundException, IOException {
		List<String> comps = DAVUtil.getPathComps(ref);
		
		if (comps.size() == 0) {
			throw new ConflictException("cannot write to root resource");
		} 
				
		Collection cur = root;
		for (int i=0;i<comps.size()-1;i++) {
			Resource r = cur.getChild(comps.get(i));
			if (!(r instanceof Collection)) {
				throw new NotAllowedException("parent is no collection");
			}
			cur = (Collection)r;
		}
		
		if (cur == null || !(cur instanceof VCalendar)) {
			System.out.println("null or no calendar: " + cur);
			throw new ConflictException("no parent found");
		}
		
		String      last   = comps.get(comps.size()-1);
		Resource    active = cur.getChild(last);
		VCalendar    vcal  = (VCalendar)cur;
		CalDAVResource res = null;
		
		// parse resource component
		try {
			res = iCalDAVParser.parse(data);			
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IOException("parse exception: " + e.getMessage());
		}
		
		// TODO create a calendar resource form stream
		if (res instanceof Event) {
			VEvent ve = new VEvent(last,(Event)res);
			((SimpleCalendar)cur).addChild(last, ve);
			System.out.println("added: " + last + ":" + res );
		} else if (res instanceof Calendar){
			Calendar cal = (Calendar)res;
			for (iCalComponent c : cal.getComponents().values()) {
				if (c instanceof Event) {
					vcal.addChild(last, new VEvent(last, (Event)c));
					System.out.println("add event");
				}
			}
		} else {
			throw new ConflictException("wrong resource type");
		}
		
		return active;
	}

	@Override
	public boolean supportLocks() {
		return true;
	}

	@Override
	public ILockManager getLockManager() {
		return lmanager;
	}
	
}
