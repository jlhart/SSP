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
package org.jasig.ssp.factory.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.jasig.ssp.dao.PersonDao;
import org.jasig.ssp.dao.reference.ProgramStatusDao;
import org.jasig.ssp.dao.reference.SpecialServiceGroupDao;
import org.jasig.ssp.factory.AbstractTOFactory;
import org.jasig.ssp.factory.PersonSearchRequestTOFactory;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.model.PersonSearchRequest;
import org.jasig.ssp.model.reference.ProgramStatus;
import org.jasig.ssp.model.reference.SpecialServiceGroup;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.transferobject.PersonSearchRequestTO;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Person transfer object factory implementation
 */
@Service
@Transactional(readOnly = true)
public class PersonSearchRequestTOFactoryImpl extends AbstractTOFactory<PersonSearchRequest, PersonSearchRequestTO>
		implements PersonSearchRequestTOFactory {

	@Autowired
	private PersonDao personDao;
	
	@Autowired
	private ProgramStatusDao programStatusDao;
	
	@Autowired
	private SpecialServiceGroupDao specialServiceGroupDao;
	
	public PersonSearchRequestTOFactoryImpl() {
		super(PersonSearchRequestTO.class, PersonSearchRequest.class);
	}


	protected PersonDao getPersonDao() {
		return personDao;
	}

	@Override
	public PersonSearchRequest from(final PersonSearchRequestTO to)
			throws ObjectNotFoundException {
		final PersonSearchRequest model = new PersonSearchRequest();
		if(to.getCoachId() != null)
		{
			Person coach = getPersonDao().get(to.getCoachId());
			model.setCoach(coach);
		}
		model.setCurrentlyRegistered(to.getCurrentlyRegistered());
		model.setDeclaredMajor(to.getDeclaredMajor());
		model.setGpaEarnedMax(to.getGpaEarnedMax());
		model.setGpaEarnedMin(to.getGpaEarnedMin());
		model.setHoursEarnedMax(to.getHoursEarnedMax());
		model.setHoursEarnedMin(to.getHoursEarnedMin());
		model.setMapStatus(to.getMapStatus());
		if(to.getProgramStatus() != null)
		{
			ProgramStatus programStatus = getProgramStatusDao().get(to.getProgramStatus());
			model.setProgramStatus(programStatus);
		}
		
		if(to.getSpecialServiceGroup() != null)
		{
			SpecialServiceGroup specialServiceGroup = getSpecialServiceGroupDao().get(to.getSpecialServiceGroup());
			model.setSpecialServiceGroup(specialServiceGroup);
		}
		model.setSapStatusCode(to.getSapStatusCode());
		model.setSchoolId(to.getSchoolId());
		model.setFirstName(to.getFirstName());
		model.setLastName(to.getLastName());
		model.setPlanStatus(to.getPlanStatus());
		model.setMyCaseload(to.getMyCaseload());
		model.setMyPlans(to.getMyPlans());
		model.setBirthDate(to.getBirthDate());
		model.setEarlyAlertResponseLate(to.getEarlyAlertResponseLate());
		model.setPersonTableType(to.getPersonTableType());
		model.setSortAndPage(to.getSortAndPage());
		return model;
	}


	@Override
	public PersonSearchRequest from(UUID id) throws ObjectNotFoundException {
		return null;
	}


	public ProgramStatusDao getProgramStatusDao() {
		return programStatusDao;
	}
	
	public SpecialServiceGroupDao getSpecialServiceGroupDao() {
		return specialServiceGroupDao;
	}



	public void setProgramStatusDao(ProgramStatusDao programStatusDao) {
		this.programStatusDao = programStatusDao;
	}


	@Override
	public PersonSearchRequest from(String schoolId, String firstName, String lastName,
			String programStatus, String specialServiceGroup,
			String coachId, String declaredMajor, BigDecimal hoursEarnedMin,
			BigDecimal hoursEarnedMax, BigDecimal gpaEarnedMin,
			BigDecimal gpaEarnedMax, Boolean currentlyRegistered,String earlyAlertResponseLate,
			String sapStatusCode, String mapStatus, String planStatus, Boolean myCaseload, Boolean myPlans,
			Date birthDate, String personTableType , SortingAndPaging sortAndPage) throws ObjectNotFoundException {
		PersonSearchRequestTO to = new PersonSearchRequestTO();
		to.setSchoolId(schoolId);
		to.setFirstName(firstName);
		to.setLastName(lastName);
		to.setProgramStatus(programStatus == null ? null : UUID.fromString(programStatus));
		to.setSpecialServiceGroup(specialServiceGroup == null ? null : UUID.fromString(specialServiceGroup));
		to.setCoachId(coachId == null ? null : UUID.fromString(coachId));
		to.setDeclaredMajor(declaredMajor);
		to.setHoursEarnedMin(hoursEarnedMin);
		to.setHoursEarnedMax(hoursEarnedMax);
		to.setGpaEarnedMin(gpaEarnedMin);
		to.setGpaEarnedMax(gpaEarnedMax);
		to.setCurrentlyRegistered(currentlyRegistered);
		to.setEarlyAlertResponseLate(earlyAlertResponseLate);
		to.setSapStatusCode(sapStatusCode);
		to.setMapStatus(mapStatus);
		to.setPlanStatus(planStatus);
		to.setMyCaseload(myCaseload);
		to.setMyPlans(myPlans);
		to.setBirthDate(birthDate);
		to.setPersonTableType(personTableType);
		to.setSortAndPage(sortAndPage);
		return from(to);
	}
	
	@Override
	public PersonSearchRequest from(String schoolId, String firstName, String lastName,
			String programStatus, String specialServiceGroup,
			String coachId, String declaredMajor, BigDecimal hoursEarnedMin,
			BigDecimal hoursEarnedMax, BigDecimal gpaEarnedMin,
			BigDecimal gpaEarnedMax, Boolean currentlyRegistered,String earlyAlertResponseLate,
			String sapStatusCode, String mapStatus, String planStatus, Boolean myCaseload, Boolean myPlans,
			Date birthDate) throws ObjectNotFoundException {
		PersonSearchRequestTO to = new PersonSearchRequestTO();
		to.setSchoolId(schoolId);
		to.setFirstName(firstName);
		to.setLastName(lastName);
		to.setProgramStatus(programStatus == null ? null : UUID.fromString(programStatus));
		to.setSpecialServiceGroup(specialServiceGroup == null ? null : UUID.fromString(specialServiceGroup));
		to.setCoachId(coachId == null ? null : UUID.fromString(coachId));
		to.setDeclaredMajor(declaredMajor);
		to.setHoursEarnedMin(hoursEarnedMin);
		to.setHoursEarnedMax(hoursEarnedMax);
		to.setGpaEarnedMin(gpaEarnedMin);
		to.setGpaEarnedMax(gpaEarnedMax);
		to.setCurrentlyRegistered(currentlyRegistered);
		to.setEarlyAlertResponseLate(earlyAlertResponseLate);
		to.setSapStatusCode(sapStatusCode);
		to.setMapStatus(mapStatus);
		to.setPlanStatus(planStatus);
		to.setMyCaseload(myCaseload);
		to.setMyPlans(myPlans);
		to.setBirthDate(birthDate);
		return from(to);
	}
}