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
package org.jasig.ssp.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.jasig.ssp.model.MapStatusReport;
import org.jasig.ssp.model.MapStatusReportCourseDetails;
import org.jasig.ssp.model.MapStatusReportSubstitutionDetails;
import org.jasig.ssp.model.MapStatusReportTermDetails;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.model.Plan;
import org.jasig.ssp.model.PlanCourse;
import org.jasig.ssp.model.external.ExternalStudentTranscriptCourse;
import org.jasig.ssp.model.external.ExternalSubstitutableCourse;
import org.jasig.ssp.model.external.PlanStatus;
import org.jasig.ssp.transferobject.reports.MapStatusReportCoachEmailInfo;
import org.jasig.ssp.transferobject.reports.MapStatusReportPerson;
import org.jasig.ssp.transferobject.reports.MapStatusReportSummaryDetail;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.springframework.stereotype.Repository;

@Repository
public class MapStatusReportDao  extends AbstractPersonAssocAuditableCrudDao<MapStatusReport> implements PersonAssocAuditableCrudDao<MapStatusReport> { 

 

	public MapStatusReportDao() {
		super(MapStatusReport.class);
	}
	protected MapStatusReportDao(Class<MapStatusReport> persistentClass) {
		super(MapStatusReport.class);
	}

	public void deleteAllOldReports() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		String deleteSubstitutionDetailsHql = "delete org.jasig.ssp.model.MapStatusReportSubstitutionDetails msrsd";
		session.createQuery(deleteSubstitutionDetailsHql).executeUpdate();
		String deleteCourseDetailsHql = "delete org.jasig.ssp.model.MapStatusReportCourseDetails msrcd";
		session.createQuery(deleteCourseDetailsHql).executeUpdate();
		String deleteTermDetailsHql = "delete org.jasig.ssp.model.MapStatusReportTermDetails msrtd";
		session.createQuery(deleteTermDetailsHql).executeUpdate();
		String deleteMapStatusReportHql = "delete org.jasig.ssp.model.MapStatusReport msrtd";
		session.createQuery(deleteMapStatusReportHql).executeUpdate();
		
		tx.commit();
		session.close();

	} 
	@SuppressWarnings("unchecked")
	public List<MapStatusReportSummaryDetail> getSummaryDetails() {
		String detailsQuery = " select new org.jasig.ssp.transferobject.reports.MapStatusReportSummaryDetail(msr.planStatus,count(*)) from MapStatusReport msr group by msr.planStatus order by count(*) desc";
		return createHqlQuery(detailsQuery).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MapStatusReportCoachEmailInfo> getCoachesWithOffPlanStudent() {
		String detailsQuery = " select distinct new org.jasig.ssp.transferobject.reports.MapStatusReportCoachEmailInfo(plan.owner.id, plan.owner.primaryEmailAddress,plan.person.coach.primaryEmailAddress) "
				+ "from Plan plan, MapStatusReport msr "
				+ " where msr.plan = plan and msr.planStatus = :planStatus ";

		return createHqlQuery(detailsQuery).setString("planStatus", PlanStatus.OFF.name()).list();
	}
	

	@SuppressWarnings("unchecked")
	public List<MapStatusReportPerson> getOffPlanPlansForOwner(Person owner) {
		String getAllActivePlanIdQuery = "select new org.jasig.ssp.transferobject.reports.MapStatusReportPerson(plan.id, plan.person.id, plan.person.schoolId, plan.programCode,plan.catalogYearCode,plan.person.firstName,plan.person.lastName) "
									   + "from org.jasig.ssp.model.Plan plan , MapStatusReport msr "
									   + "where  msr.plan = plan and msr.planStatus = :planStatus and plan.owner = :owner";
		Query query = createHqlQuery(getAllActivePlanIdQuery);
		List<MapStatusReportPerson> result  = query.setEntity("owner", owner).setString("planStatus", PlanStatus.OFF.name()).list();
									   
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<MapStatusReportCourseDetails> getAllCourseDetailsForPerson(
			Person person) {
		String query = "select msrcd from MapStatusReportCourseDetails msrcd, MapStatusReport msr where msrcd.report = msr and msr.person = :person";
		return createHqlQuery(query).setEntity("person", person).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MapStatusReportTermDetails> getAllTermDetailsForPerson(
			Person person) {
		String query = "select msrtd from MapStatusReportTermDetails msrtd, MapStatusReport msr where msrtd.report = msr and msr.person = :person";
		return createHqlQuery(query).setEntity("person", person).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MapStatusReportSubstitutionDetails> getAllSubstitutionDetailsForPerson(
			Person person) {
		String query = "select msrsd from MapStatusReportSubstitutionDetails msrsd, MapStatusReport msr where msrsd.report = msr and msr.person = :person";
		return createHqlQuery(query).setEntity("person", person).list();
	}
	
	public void oldReportForStudent(UUID personId) {
		Collection<MapStatusReport> allForPersonId = getAllForPersonId(personId, null).getRows();
		for (MapStatusReport mapStatusReport : allForPersonId) {
			delete(mapStatusReport);
		}
	}



}
