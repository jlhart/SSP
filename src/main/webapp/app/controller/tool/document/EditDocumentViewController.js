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
Ext.define('Ssp.controller.tool.document.EditDocumentViewController', {
    extend: 'Deft.mvc.ViewController',
    mixins: [ 'Deft.mixin.Injectable' ],
    inject: {
    	apiProperties: 'apiProperties',
        appEventsController: 'appEventsController',
    	formUtils: 'formRendererUtils',
    	person: 'currentPerson',
    	model: 'currentDocument'
    },
    config: {
    	containerToLoadInto: 'tools',
    	formToDisplay: 'studentdocuments',
    	url: '',
    	inited: false
    },

    control: {
    	'saveButton': {
			click: 'onSaveClick'
		},
		
		'cancelButton': {
			click: 'onCancelClick'
		}   	
    },
    
	init: function() {
		this.url = this.apiProperties.createUrl( this.apiProperties.getItemUrl('personDocument') );
		this.url = this.url.replace('{id}',this.person.get('id'));
		
		this.initForm();
		
		return this.callParent(arguments);
    },
 
	initForm: function(){
		var id = this.model.get("id");
		this.getView().getForm().reset();
		this.getView().getForm().loadRecord( this.model );
		if (id != null && id != "")
		{
			Ext.ComponentQuery.query('#confidentialityLevelCombo')[0].setValue( this.model.get('confidentialityLevel').id );
		}
		this.inited=true;
	},    
    
	onSaveClick: function(button) {
		var me = this;
		var record, id, jsonData, url;
		var form = this.getView().getForm();
		var values = form.getValues();
		url = this.url;
		record = this.model;
		id = record.get('id');
		
		successFunc = function(response, view) {
			me.displayMain();
		};
		
		if (form.isValid())
		{
			form.updateRecord();    		
    		record.set('confidentialityLevel',{"id": form.getValues().confidentialityLevelId});

			jsonData = record.data;
			
			if (id.length > 0)
			{
				// editing
				this.apiProperties.makeRequest({
					url: url+"/"+id,
					method: 'PUT',
					jsonData: jsonData,
					successFunc: successFunc 
				});		
			}else{
				// adding
				this.apiProperties.makeRequest({
					url: url,
					method: 'POST',
					jsonData: jsonData,
					successFunc: successFunc 
				});		
			}
		
		}else{
			Ext.Msg.alert('Error','Please correct the errors in your document.');
		}

	},
	
	onCancelClick: function(button){
		this.displayMain();
	},
	
	displayMain: function(){
		var comp = this.formUtils.loadDisplay(this.getContainerToLoadInto(), this.getFormToDisplay(), true, {});
	}
});