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
package org.jasig.ssp.web.api.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jasig.ssp.model.Person;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.util.service.stub.Stubs;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.base.Predicate;

import net.sf.jasperreports.engine.JRException;

public class AddressLabelsReportControllerIntegrationTest
		extends AbstractReportControllerIntegrationTest  {

	@Autowired
	private transient AddressLabelsReportController controller;

	@Test
	public void testGetAddressLabelsReturnsAllStudentsIfNoFiltersSet()
			throws IOException, ObjectNotFoundException, JRException {
		final MockHttpServletResponse response = new MockHttpServletResponse();
		controller.getAddressLabels(response, null, null, null, null, null,
				null, null, null, null, null, "csv");

		// "body" is the actual results and the header that describes its columns.
		// This is as opposed to rows which precede the header, which describe
		// the filtering criteria
		final List<String> expectedReportBodyLines = new ArrayList<String>(4);
		expectedReportBodyLines.add("FIRST,,,MIDDLE,LAST,,,Student ,TYP,Address,,CITY,ST,PHONE(H),EMAIL(SCHOOL),EMAIL(HOME)");
		expectedReportBodyLines.add("James,,,A,Gosling,,,,ILP,444 West Third Street ,,San Francisco,CA,908-123-4567,test@sinclair.edu,test@sinclair.edu");
		expectedReportBodyLines.add("Dennis,,,M,Ritchie,,,,CAP,444 West Third Street ,,Berkeley Heights,NJ,908-123-4567,test@sinclair.edu,test@sinclair.edu");
		expectedReportBodyLines.add("Kenneth,,,L,Thompson,,,,CAP,444 West Third Street ,,Murray Hill,NJ,908-123-4567,test@sinclair.edu,test@sinclair.edu");

		expectReportBodyLines(expectedReportBodyLines, response, afterHeader());
	}

	@Test
	public void testGetAddressLabelsFiltersByCoachId()
			throws IOException, ObjectNotFoundException, JRException {

		final Person dennisRitchie =
				personService.get(Stubs.PersonFixture.DMR.id());
		final Person kevinSmith =
				personService.get(Stubs.PersonFixture.KEVIN_SMITH.id());
		dennisRitchie.setCoach(kevinSmith);
		personService.save(dennisRitchie);
		sessionFactory.getCurrentSession().flush();

		final MockHttpServletResponse response = new MockHttpServletResponse();
		// Alan Turing, i.e. the coach assigned to our test student users
		// in our standard fixture
		final UUID coachId = Stubs.PersonFixture.ADVISOR_0.id();
		controller.getAddressLabels(response, null, coachId, null, null, null,
				null, null, null, null, null, "csv");

		// "body" is the actual results and the header that describes its columns.
		// This is as opposed to rows which precede the header, which describe
		// the filtering criteria
		final List<String> expectedReportBodyLines = new ArrayList<String>(4);
		// same as in testGetAddressLabelsReturnsAllStudentsIfNoFiltersSet(), but
		// Dennis Ritchie is missing
		expectedReportBodyLines.add("FIRST,,,MIDDLE,LAST,,,Student ,TYP,Address,,CITY,ST,PHONE(H),EMAIL(SCHOOL),EMAIL(HOME)");
		expectedReportBodyLines.add("James,,,A,Gosling,,,,ILP,444 West Third Street ,,San Francisco,CA,908-123-4567,test@sinclair.edu,test@sinclair.edu");
		expectedReportBodyLines.add("Kenneth,,,L,Thompson,,,,CAP,444 West Third Street ,,Murray Hill,NJ,908-123-4567,test@sinclair.edu,test@sinclair.edu");

		expectReportBodyLines(expectedReportBodyLines, response, afterHeader());
	}

	@Override
	protected Predicate<String> afterHeader() {
		return afterLineContaining("General Student Report");
	}

}
