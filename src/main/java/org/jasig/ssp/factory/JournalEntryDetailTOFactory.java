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
package org.jasig.ssp.factory;

import java.util.Collection;
import java.util.Set;

import org.jasig.ssp.model.JournalEntry;
import org.jasig.ssp.model.JournalEntryDetail;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.transferobject.JournalEntryDetailTO;

public interface JournalEntryDetailTOFactory extends
		TOFactory<JournalEntryDetailTO, JournalEntryDetail> {
	/**
	 * Convert transfer objects to equivalent models.
	 * 
	 * @param tObjects
	 *            collection of transfer objects to convert
	 * @param journalEntry
	 *            parent JournalEntry instance
	 * @return Set of equivalent models
	 * @throws ObjectNotFoundException
	 *             If any referenced data could not be found.
	 */
	Set<JournalEntryDetail> asSet(
			final Collection<JournalEntryDetailTO> tObjects,
			final JournalEntry journalEntry) throws ObjectNotFoundException;
}
