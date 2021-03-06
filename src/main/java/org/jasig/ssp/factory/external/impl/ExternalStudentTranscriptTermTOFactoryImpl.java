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
package org.jasig.ssp.factory.external.impl;

import org.jasig.ssp.dao.external.ExternalDataDao;
import org.jasig.ssp.factory.external.ExternalStudentTranscriptTermTOFactory;
import org.jasig.ssp.model.external.ExternalStudentTranscriptTerm;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.transferobject.external.ExternalStudentTranscriptTermTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExternalStudentTranscriptTermTOFactoryImpl extends
		AbstractExternalDataTOFactory<ExternalStudentTranscriptTermTO, ExternalStudentTranscriptTerm> implements
		ExternalStudentTranscriptTermTOFactory {


	public ExternalStudentTranscriptTermTOFactoryImpl() {
		super(ExternalStudentTranscriptTermTO.class, ExternalStudentTranscriptTerm.class);
	}

	@Override
	public ExternalStudentTranscriptTerm from(
			ExternalStudentTranscriptTermTO tObject)
			throws ObjectNotFoundException {
		final ExternalStudentTranscriptTerm model = super.from(tObject);
		model.setCreditCompletionRate(tObject.getCreditCompletionRate());
		model.setCreditHoursAttempted(tObject.getCreditHoursAttempted());
		model.setCreditHoursEarned(tObject.getCreditHoursEarned());
		model.setCreditHoursForGpa(tObject.getCreditHoursForGpa());
		model.setCreditHoursNotCompleted(tObject.getCreditHoursNotCompleted());
		model.setGradePointAverage(tObject.getGradePointAverage());
		model.setSchoolId(tObject.getSchoolId());
		model.setTermCode(tObject.getTermCode());
		model.setTotalQualityPoints(tObject.getTotalQualityPoints());
		return model;
	}



	@Override
	protected ExternalDataDao<ExternalStudentTranscriptTerm> getDao() {
		// TODO Auto-generated method stub
		return null;
	}

}
