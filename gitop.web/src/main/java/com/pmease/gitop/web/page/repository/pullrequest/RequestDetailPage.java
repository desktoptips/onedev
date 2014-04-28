package com.pmease.gitop.web.page.repository.pullrequest;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pmease.gitop.core.Gitop;
import com.pmease.gitop.core.manager.PullRequestManager;
import com.pmease.gitop.model.CommitComment;
import com.pmease.gitop.model.PullRequest;
import com.pmease.gitop.web.page.repository.RepositoryTabPage;
import com.pmease.gitop.web.page.repository.source.commit.diff.CommitCommentsAware;

@SuppressWarnings("serial")
public class RequestDetailPage extends RepositoryTabPage implements CommitCommentsAware {

	private IModel<PullRequest> model;
	
	public RequestDetailPage(final PageParameters params) {
		super(params);
		
		model = new LoadableDetachableModel<PullRequest>() {

			@Override
			protected PullRequest load() {
				return Gitop.getInstance(PullRequestManager.class).load(params.get(0).toLong());
			}
			
		};
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
	
		add(new RequestDetailPanel("content", model));
	}
	
	public PullRequest getPullRequest() {
		return model.getObject();
	}

	@Override
	public void onDetach() {
		model.detach();
		super.onDetach();
	}

	@Override
	public List<CommitComment> getCommitComments() {
		return new ArrayList<>();
	}

	@Override
	public boolean isShowInlineComments() {
		return false;
	}

	@Override
	public boolean canAddComments() {
		return false;
	}
	
}