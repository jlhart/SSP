/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.dao.reference;

import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jasig.ssp.dao.AuditableCrudDao;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.model.reference.JournalStepDetail;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.jasig.ssp.util.sort.SortDirection;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Data access class for the JournalStepDetail reference entity.
 */
@Repository
public class JournalStepDetailDao extends
		AbstractReferenceAuditableCrudDao<JournalStepDetail>
		implements AuditableCrudDao<JournalStepDetail> {

	@Autowired
	private JournalStepDao journalStepDao;
	
	private Logger logger = Logger.getLogger(JournalStepDetailDao.class);
	
	public JournalStepDetailDao() {
		super(JournalStepDetail.class);
	}
	
	public void softDeleteReferencingAssociations(UUID id) throws ObjectNotFoundException {
		JournalStepDetail obj = get(id);
		String softDeleteAssociations = "update JournalStepJournalStepDetail set objectStatus = :objectStatus where journalStepDetail = :journalStepDetail";
		createHqlQuery(softDeleteAssociations).setInteger("objectStatus", ObjectStatus.INACTIVE.ordinal()).setEntity("journalStepDetail", obj).executeUpdate();		
	}
	
	@Override
	public PagingWrapper<JournalStepDetail> getAll(final SortingAndPaging sAndP) {
		SortingAndPaging sp = sAndP;
		if (sp == null) {
			sp = new SortingAndPaging(ObjectStatus.ACTIVE);
		}

		if (!sp.isSorted()) {
			sp.appendSortField("objectStatus", SortDirection.ASC);
			sp.appendSortField("name", SortDirection.ASC);
		}

		return super.getAll(sp);
	}


	public JournalStepDao getJournalStepDao() {
		return journalStepDao;
	}


	public void setJournalStepDao(JournalStepDao journalStepDao) {
		this.journalStepDao = journalStepDao;
	}
}