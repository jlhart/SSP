/*
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
Ext.define('Ssp.view.tools.sis.StudentInformationSystem', {
	extend: 'Ext.panel.Panel',
	alias : 'widget.studentinformationsystem',
	title: 'Student Information System',	
	width: '100%',
	height: '100%',   
	autoScroll: true,
	initComponent: function() {	
		var me=this;
		Ext.apply(me, 
				{		
					border: 0,
				    items: [ Ext.createWidget('tabpanel', {
				        width: '100%',
				        height: '100%',
				        activeTab: 0,
						border: 0,
				        items: [ { title: 'Registration',
				        	       autoScroll: true,
				        		   items: [{xtype: 'sisregistration'}]
				        		},{
				            		title: 'Transcript',
				            		autoScroll: true,
				            		border: 0,
				            		items: [{xtype: 'sistranscript'}]
				        		},{
				            		title: 'Assessment',
				            		autoScroll: true,
				            		border: 0,
				            		items: [{xtype: 'sisassessment'}]
				        		}]
				    })]
			    
		});
		
		return me.callParent(arguments);
	}
});