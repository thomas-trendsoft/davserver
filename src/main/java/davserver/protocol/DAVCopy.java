package davserver.protocol;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import davserver.DAVException;
import davserver.DAVUrl;
import davserver.DAVUtil;
import davserver.repository.Collection;
import davserver.repository.IRepository;
import davserver.repository.Property;
import davserver.repository.Resource;
import davserver.repository.error.ConflictException;
import davserver.repository.error.LockedException;
import davserver.repository.error.NotAllowedException;
import davserver.repository.error.NotFoundException;
import davserver.repository.error.ResourceExistsException;

/**
 * HTTP COPY Method implementation
 * 
 * @author tkrieger
 *
 */
public class DAVCopy {
	
	public DAVCopy() {
	}
	
	/**
	 * Copy a complete collection with depth from source to target
	 * 
	 * @param source
	 * @param target
	 */
	private void copyCollection(IRepository repos, Collection source,String target,Resource tres,int depth) 
			throws NotAllowedException, ConflictException, ResourceExistsException, NotFoundException, IOException 
	{
		Iterator<Resource> citer = null;
		Collection         nc;
		if (depth > 0) {
			citer = source.getChildIterator();
		}
		
		if (target == null) {
			nc    = repos.createCollection(target);	
		} else if (depth == 0 && tres instanceof Collection) {
			nc = (Collection)tres;
		} else {
			nc    = repos.createCollection(target);	
		}

		copyProperties(source, nc);
		
		// check if childs be copied
		if (depth == 0) {
			return;
		}
		
		// Iterate child resource
		while (citer.hasNext()) {
			Resource r = citer.next();
			if (r instanceof Collection && depth > 0) {
				copyCollection(repos,(Collection)r,target + "/" + ((Collection)r).getName(),null,depth-1);
			} else {
				copyResource(repos,r,target + "/" + r.getName());
			}
		}
		
	}
	
	/**
	 * Copy a resource from source to target 
	 * 
	 * @param repos
	 * @param source
	 * @param target
	 * @throws NotAllowedException
	 * @throws ConflictException
	 * @throws ResourceExistsException
	 * @throws NotFoundException
	 * @throws IOException
	 */
	private void copyResource(IRepository repos,Resource source,String target) 
			throws NotAllowedException, ConflictException, ResourceExistsException, NotFoundException, IOException 
	{
		Resource r = repos.createResource(target, source.getContent());	
		copyProperties(source,r);
	}
	
	/**
	 * Copy the properties of a resource to an target 
	 * 
	 * @param src
	 * @param target
	 */
	private void copyProperties(Resource src,Resource target) {
		Iterator<Property> iter = src.getPropertyIterator();
		
		while (iter.hasNext()) {
			Property p = iter.next();
			target.setProperty(p);
		}
	}

	
	/**
	 * Handle COPY Request
	 * 
	 * @param req
	 * @param resp
	 * @param repos
	 * @param url
	 * @throws DAVException 
	 * @throws NotFoundException 
	 * @throws NotAllowedException 
	 */
	public void handleCopy(HttpRequest req,HttpResponse resp,IRepository repos,DAVUrl url,boolean move) throws DAVException, NotAllowedException, NotFoundException {
		System.out.println("handle copy/move");
		int stat = 201;

		try {
			

			// check destination header
			Header d  = req.getFirstHeader("Destination");
			Header ow = req.getFirstHeader("Overwrite");
			if (d == null) {
				throw new DAVException(400,"destination header missing");
			}
			
			// check source resource
			if (url.getResref() == null) {
				throw new DAVException(400,"no source reference");
			}
			
			// check target resource TODO check prefix management for resources
			URI    turi = new URI(d.getValue());
			DAVUrl turl = new DAVUrl(turi.getPath(),"");

			// check preconditions
			if (move)
				DAVRequest.checkLock(req, repos, url);
			else 
				DAVRequest.checkLock(req, repos, turl);
			
			// check source resource
			Resource src = repos.locate(url.getResref());
			if (src == null) {
				throw new DAVException(404,"source not found");
			}
			
			if (turl.getResref() == null) {
				throw new DAVException(400,"no readable target reference");
			}	
			
			// check if source and target the same
			if (turl.getResref().compareTo(url.getResref()) == 0) {
				throw new DAVException(403,"source is target");
			}
			
			
			int    dv    = Integer.MAX_VALUE;
			Header depth = req.getFirstHeader("Depth");
			if (depth != null && depth.getValue().compareTo("infinity") != 0) {
				if (depth.getValue().compareTo("0")==0) {
					dv = 0;
				} else {
					throw new DAVException(400,"bad request");
				}
			}

			
			// check target and overwrite options
			Resource target = null;
			try {
				target = repos.locate(turl.getResref());
				if (target != null) {
					// update http response
					stat = 204;
					if (ow == null || ow.getValue().compareTo("F")==0) {
						throw new DAVException(412,"precondition failed");
					} else if (ow.getValue().compareTo("T")==0 && !(dv == 0 && target instanceof Collection)) {
						try {
							repos.remove(turl.getResref());
						} catch (LockedException e) {
							e.printStackTrace();
							throw new DAVException(412,"locked");
						}
					} 
				}
			} catch (NotFoundException nfe) {
				// exception ok 
			}
			
			
			// Check resource type of source element 
			if (src instanceof Collection) {
				copyCollection(repos,(Collection)src,turl.getResref(),target,dv);
			// copy simple resource
			} else {
				copyResource(repos,src,turl.getResref());
			}
			
			// if move delete the source
			if (move) {
				try {
					repos.remove(url.getResref());
				} catch (LockedException le) {
					DAVUtil.handleError(new DAVException(500,"cant delete source"), resp);
				}
			}
			
			resp.setStatusCode(stat);
			
		} catch (IOException ioe) {
			DAVUtil.handleError(new DAVException(500, ioe.getMessage()),resp);
		} catch (ConflictException ce) {
			// TODO Check RFC Handling
		} catch (ResourceExistsException ree) {
			// TODO Check RFC Handling
		} catch (URISyntaxException use) {
			DAVUtil.handleError(new DAVException(400,"bad location"), resp);
		}
	}
	
}
