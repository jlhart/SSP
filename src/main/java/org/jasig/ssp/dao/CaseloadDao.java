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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.jasig.ssp.model.CoachCaseloadRecordCountForProgramStatus;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.model.PersonSearchResult2;
import org.jasig.ssp.model.reference.ProgramStatus;
import org.jasig.ssp.transferobject.CaseloadReassignmentRequestTO;
import org.jasig.ssp.transferobject.reports.CaseLoadSearchTO;
import org.jasig.ssp.util.hibernate.BatchProcessor;
import org.jasig.ssp.util.hibernate.MultipleCountProjection;
import org.jasig.ssp.util.hibernate.NamespacedAliasToBeanResultTransformer;
import org.jasig.ssp.util.hibernate.OrderAsString;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

@Repository
public class CaseloadDao extends AbstractDao<Person> {

	public CaseloadDao() {
		super(Person.class);
	}

	@SuppressWarnings("unchecked")
	public PagingWrapper<PersonSearchResult2> caseLoadFor(
			final ProgramStatus programStatus, @NotNull final Person coach,
			final SortingAndPaging sAndP) {

		// This creation of the query is order sensitive as 2 queries are run
		// with the same restrictions. The first query simply runs the query to
		// find a count of the records. The second query returns the row data.
		// protected Criteria createCriteria() {
		// return sessionFactory.getCurrentSession().createCriteria(
		// this.persistentClass);
		// }
		final Criteria query = this.createCriteria();

		// Restrict by program status if provided
		if (programStatus != null) {
			final Criteria subquery = query.createAlias("programStatuses",
					"personProgramStatus");
			subquery.add(Restrictions.or(Restrictions
					.isNull("personProgramStatus.expirationDate"), Restrictions
					.ge("personProgramStatus.expirationDate", new Date())));
			subquery.add(Restrictions.eq("personProgramStatus.programStatus",
					programStatus));
		}

		// restrict to coach
		query.add(Restrictions.eq("coach", coach));
		if(sAndP.getStatus()!= null)
		{
			query.add(Restrictions.eq("objectStatus", sAndP.getStatus()));
		}


		// item count
		Long totalRows = 0L;
		if ((sAndP != null) && sAndP.isPaged()) {
			totalRows = (Long) query.setProjection(Projections.rowCount())
					.uniqueResult();
		}

		// clear the row count projection
		query.setProjection(null);

		//
		// Add Properties to return in the case load
		//
		// Set Columns to Return: id, firstName, middleName, lastName,
		// schoolId, and birthDate
		final ProjectionList projections = Projections.projectionList();
		projections.add(Projections.property("id").as("clr_personId"));
		projections.add(Projections.property("firstName").as("clr_firstName"));
		projections.add(Projections.property("middleName").as("clr_middleName"));
		projections.add(Projections.property("lastName").as("clr_lastName"));
		projections.add(Projections.property("schoolId").as("clr_schoolId"));
		projections.add(Projections.property("studentIntakeCompleteDate").as(
				"clr_studentIntakeCompleteDate"));
        projections.add(Projections.property("birthDate").as("clr_birthDate"));

		// Join to Student Type
		query.createAlias("studentType", "studentType",
				JoinType.LEFT_OUTER_JOIN);
		// add StudentTypeName Column
		projections.add(Projections.property("studentType.name").as(
				"clr_studentTypeName"));

		query.setProjection(projections);

		query.setResultTransformer(
				new NamespacedAliasToBeanResultTransformer(
						PersonSearchResult2.class, "clr_"));

		// Add Paging
		if (sAndP != null) {
			sAndP.addAll(query);
		}

		return new PagingWrapper<PersonSearchResult2>(totalRows, query.list());
	}

	@SuppressWarnings("unchecked")
	public Long caseLoadCountFor(final ProgramStatus programStatus,
			@NotNull final Person coach, List<UUID> studentTypeIds,
			Date dateFrom, Date dateTo) {

		final Criteria query = createCriteria();

		// Restrict by program status if provided
		if (programStatus != null) {

			final Criteria subquery = query.createAlias("programStatuses",
					"personProgramStatus");

			if (dateFrom != null) {
				subquery.add(Restrictions.ge(
						"personProgramStatus.effectiveDate", dateFrom));
			}
			if (dateTo != null) {
				subquery.add(Restrictions.le(
						"personProgramStatus.effectiveDate", dateTo));
			}

			subquery.add(Restrictions.or(Restrictions
					.isNull("personProgramStatus.expirationDate"), Restrictions
					.ge("personProgramStatus.expirationDate", new Date())));

			subquery.add(Restrictions.eq("personProgramStatus.programStatus",
					programStatus));
		}

		// restrict to coach
		query.add(Restrictions.eq("coach", coach));

		// add possible studentTypeId Check
		if (studentTypeIds != null && !studentTypeIds.isEmpty()) {
			query.add(Restrictions.in("studentType.id", studentTypeIds));
		}

		// item count
		Long totalRows = (Long) query.setProjection(Projections.rowCount())
				.uniqueResult();

		return totalRows;
	}


	public PagingWrapper<CoachCaseloadRecordCountForProgramStatus>
		currentCaseLoadCountsByStatus(CaseLoadSearchTO searchForm,
									  SortingAndPaging sAndP) {

		// Technically run the risk of returning multiple statuses
		// per user if the user's statuses are "pre-loaded" i.e. the current
		// status is set to expire at a future date and a subsequent status
		// has already been created in the db and is set to go into effect on
		// that future date... but that shouldn't happen under current use
		// cases
		
		if(searchForm == null)
			searchForm = new CaseLoadSearchTO();
		Criterion dateRestrictions =
				overlappingProgramStatusDateRestrictions(new Date(), null);

		return caseloadCountsByStatusWithDateRestrictions(dateRestrictions,
				 searchForm.getStudentTypeIds(),
				 searchForm.getServiceReasonIds(),
				 searchForm.getSpecialServiceGroupIds(), 
				 searchForm.getHomeDepartment(), 
				 sAndP);

	}

	public PagingWrapper<CoachCaseloadRecordCountForProgramStatus>
		caseLoadCountsByStatus(
			List<UUID> studentTypeIds,
			Date programStatusDateFrom,
			Date programStatusDateTo,
			String homeDepartment,
			SortingAndPaging sAndP) {

		Criterion dateRestrictions =
				overlappingProgramStatusDateRestrictions(programStatusDateFrom,
						programStatusDateTo);

		return caseloadCountsByStatusWithDateRestrictions(dateRestrictions,
					studentTypeIds, null, null, homeDepartment, sAndP);

	}

	private PagingWrapper<CoachCaseloadRecordCountForProgramStatus>
		caseloadCountsByStatusWithDateRestrictions(Criterion dateRestrictions,
													List<UUID> studentTypeIds,
													List<UUID> serviceReasonIds,
													List<UUID> specialServiceGroupIds,
													String homeDepartment,
												   SortingAndPaging sAndP) {

		final Criteria query = createCriteria();

		query.createAlias("programStatuses", "ps").createAlias("coach", "c");

		if ( dateRestrictions != null ) {
			query.add(dateRestrictions);
		}

		if (studentTypeIds != null && !studentTypeIds.isEmpty()) {
			query.add(Restrictions.in("studentType.id", studentTypeIds));
		}
		
		if (serviceReasonIds != null && !serviceReasonIds.isEmpty()) {
			query.createAlias("serviceReasons", "serviceReasons");
			query.createAlias("serviceReasons.serviceReason", "serviceReason");
			query.add(Restrictions.in("serviceReason.id", serviceReasonIds));
		}
		
		if (specialServiceGroupIds != null && !specialServiceGroupIds.isEmpty()) {
			query.createAlias("specialServiceGroups",
				"personSpecialServiceGroups")
				.add(Restrictions
						.in("personSpecialServiceGroups.specialServiceGroup.id",
								specialServiceGroupIds));
		}


		if(homeDepartment == null || homeDepartment.length() <= 0)
			query.createAlias("coach.staffDetails", "sd", JoinType.LEFT_OUTER_JOIN);
		else{
			query.createAlias("coach.staffDetails", "sd");
			query.add(Restrictions.eq("sd.departmentName", homeDepartment));
		}
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.groupProperty("c.id").as("coachId"));
		// TODO find a way to turn these into more generic and centralized
		// feature checks on the Dialect so we at least aren't scattering
		// Dialect-specific code all over the place
		Dialect dialect = ((SessionFactoryImplementor) sessionFactory).getDialect();
		if ( dialect instanceof SQLServerDialect) {
			// sql server requires all these to part of the grouping
			projectionList.add(Projections.groupProperty("c.lastName").as("coachLastName"))
					.add(Projections.groupProperty("c.firstName").as("coachFirstName"))
					.add(Projections.groupProperty("c.middleName").as("coachMiddleName"))
					.add(Projections.groupProperty("c.schoolId").as("coachSchoolId"))
					.add(Projections.groupProperty("c.username").as("coachUsername"));
		} else {
			// other dbs (postgres) don't need these in the grouping
			projectionList.add(Projections.property("c.lastName").as("coachLastName"))
					.add(Projections.property("c.firstName").as("coachFirstName"))
					.add(Projections.property("c.middleName").as("coachMiddleName"))
					.add(Projections.property("c.schoolId").as("coachSchoolId"))
					.add(Projections.property("c.username").as("coachUsername"));
		}
		projectionList.add(Projections.groupProperty("sd.departmentName").as("coachDepartmentName"))
				.add(Projections.groupProperty("ps.programStatus.id").as("programStatusId"))
				.add(Projections.count("ps.programStatus.id").as("count"));
		query.setProjection(projectionList);

		if ( sAndP == null || !(sAndP.isSorted()) ) {
			// there are assumptions in CaseloadServiceImpl about this
			// default ordering... make sure it stays synced up
			query.addOrder(Order.asc("c.lastName"))
					.addOrder(Order.asc("c.firstName"))
					.addOrder(Order.asc("c.middleName"));

			// can't sort on program status name without another join, but
			// sorting on id is non-deterministic across dbs (sqlserver sorts
			// UUIDs one way, Postgres another, so you can't write a single
			// integration test for both), so more dialect specific magic here.
			if ( dialect instanceof SQLServerDialect ) {
				query.addOrder(OrderAsString.asc("ps.programStatus.id"));
			} else {
				query.addOrder(Order.asc("ps.programStatus.id"));
			}
		}

		if ( sAndP != null ) {
			sAndP.addAll(query);
		}

		query.setResultTransformer(new AliasToBeanResultTransformer(
				CoachCaseloadRecordCountForProgramStatus.class));

		// item count
		Long totalRows = 0L;
		if ((sAndP != null) && sAndP.isPaged()) {
			query.setProjection(new MultipleCountProjection("c.id;ps.programStatus.id").setDistinct());
			totalRows = (Long) query.uniqueResult();

			if ( totalRows == 0 ) {
				Collection<CoachCaseloadRecordCountForProgramStatus> empty =
						Lists.newArrayListWithCapacity(0);
				return new PagingWrapper<CoachCaseloadRecordCountForProgramStatus>(0, empty);
			}

			// clear the row count projection
			query.setProjection(null);
		}
		return sAndP == null
				? new PagingWrapper<CoachCaseloadRecordCountForProgramStatus>(query.list())
				: new PagingWrapper<CoachCaseloadRecordCountForProgramStatus>(totalRows, query.list());
	}

	private Criterion overlappingProgramStatusDateRestrictions(
			Date programStatusDateFrom,
			Date programStatusDateTo) {

		// no date filtering
		if ( programStatusDateFrom == null && programStatusDateTo == null ) {
			return null;
		}

		// implicit range is 'beginning of time' through 'to'. anything
		// that became effective prior to or on 'to' intersects with that range
		if ( programStatusDateFrom == null ) {
			return Restrictions.le("ps.effectiveDate", programStatusDateTo);
		}

		// implicit range is 'from' to 'end of time'. the set of intersecting
		// statuses includes those that overlap with 'from' so just finding
		// statues that became effective after 'from' won't work.
		if ( programStatusDateTo == null ) {
			return Restrictions.or(

					// started before 'from', expired on or after
					Restrictions.and(
							Restrictions.le("ps.effectiveDate", programStatusDateFrom),
							expiresOnOrLaterThan(programStatusDateFrom)),

					// ... or... started on or after 'from'
					Restrictions.ge("ps.effectiveDate", programStatusDateFrom)

			);
		}

		// else both bounds were selected... we want to catch all
		// statuses overlapping that date range even if they only overlap from
		// or only overlap to or overlap both or fall completely in between.
		return Restrictions.and(
				Restrictions.le("ps.effectiveDate", programStatusDateTo),
				expiresOnOrLaterThan(programStatusDateFrom));

	}

	private Criterion expiresOnOrLaterThan(Date date) {
		return Restrictions.or(Restrictions.isNull("ps.expirationDate"),
								Restrictions.ge("ps.expirationDate", date));
	}

	public int reassignStudents(CaseloadReassignmentRequestTO obj, Person coach) {
		String sql = "update Person p set p.coach = :coach where p.schoolId in :studentId";
		BatchProcessor<String, Object> update = new BatchProcessor<String, Object>(Lists.newArrayList(obj.getStudentIds()));
		do{
			Query query = createHqlQuery( sql ).setEntity( "coach", coach );
			update.updateProcess(query, "studentId");
		}while(update.moreToProcess());
		
		return update.getCount().intValue();
	}

}