package com.pmease.gitplex.rest.resource;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.hibernate.criterion.Restrictions;

import com.pmease.commons.hibernate.dao.Dao;
import com.pmease.commons.hibernate.dao.EntityCriteria;
import com.pmease.commons.jersey.ValidQueryParams;
import com.pmease.gitplex.core.model.Branch;
import com.pmease.gitplex.core.permission.ObjectPermission;

@Path("/branches")
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class BranchResource {

	private final Dao dao;
	
	@Inject
	public BranchResource(Dao dao) {
		this.dao = dao;
	}
	
    @GET
    @Path("/{id}")
    public Branch get(@PathParam("id") Long id) {
    	Branch branch = dao.load(Branch.class, id);
    	if (!SecurityUtils.getSubject().isPermitted(ObjectPermission.ofRepositoryRead(branch.getRepository())))
    		throw new UnauthorizedException();
    	return branch;
    }
    
    @ValidQueryParams
    @GET
    public Collection<Branch> query(
    		@QueryParam("repository") Long repositoryId,
    		@QueryParam("name") String name) {
		EntityCriteria<Branch> criteria = EntityCriteria.of(Branch.class);
		if (repositoryId != null)
			criteria.add(Restrictions.eq("repository.id", repositoryId));
		if (name != null)
			criteria.add(Restrictions.eq("name", name));
		List<Branch> branches = dao.query(criteria);
		
    	for (Branch branch: branches) {
    		if (!SecurityUtils.getSubject().isPermitted(ObjectPermission.ofRepositoryRead(branch.getRepository()))) {
    			throw new UnauthorizedException("Unauthorized access to branch " + branch);
    		}
    	}
    	
    	return branches;
    }

	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") Long id) {
		Branch branch = dao.load(Branch.class, id);
		
    	if (!SecurityUtils.getSubject().isPermitted(ObjectPermission.ofRepositoryAdmin(branch.getRepository())))
    		throw new UnauthorizedException();
    	dao.remove(branch);
	}
	
    @POST
    public Long save(@NotNull @Valid Branch branch) {
    	if (!SecurityUtils.getSubject().isPermitted(ObjectPermission.ofRepositoryAdmin(branch.getRepository())))
    		throw new UnauthorizedException();
    	
    	dao.persist(branch);
    	
    	return branch.getId();
    }
    
}