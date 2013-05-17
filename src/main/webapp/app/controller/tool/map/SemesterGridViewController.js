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
Ext.define('Ssp.controller.tool.map.SemesterGridViewController', {
    extend: 'Deft.mvc.ViewController',
    mixins: [ 'Deft.mixin.Injectable' ],
    inject:{
    	courseService:"courseService"
    },
	
	init: function() {
		var me=this;
		me.getView().view.addListener("beforedrop", me.onDrop, me);
		return me.callParent(arguments);
    },

    
    onDrop: function(node, data, dropRec, dropPosition){
    	var me = this;
    	me.droppedData = data.records[0];
		var termCode = me.getView().findParentByType('semesterpanel').itemId;
    	var serviceResponses = {
                failures: {},
                successes: {},
                responseCnt: 0,
                expectedResponseCnt: 1
            }
    	me.courseService.validateCourse(me.droppedData.get('code'), termCode,  {
            success: me.newServiceSuccessHandler('validCourse', me.onValidateSuccess, serviceResponses),
            failure: me.newServiceFailureHandler('validCourse', me.onValidateFailure, serviceResponses),
            scope: me
        });
		return true;
    },
    
    onValidateSuccess: function(serviceResponses){
		var me = this;
    	var courseValidation = serviceResponses.successes.validCourse;
    	
    	if(!courseValidation.valid){
    		Ext.MessageBox.confirm("Course Not Avaiable For Term", "This course is not scheduled to be offered in this term.  Are you sure you want to add it?", me.handleInvalidCourse, me);
    	}
    },
    
    handleInvalidCourse: function(buttonId){
		var me = this;
    	if(buttonId != 'yes'){
        	var index = me.getView().getStore().find("code", me.droppedData.get('code'));
			if(index >= 0)
        		me.getView().getStore().removeAt(index);
    	}
    },
    
    onValidateFailure: function(validate){
    	var me = this;
    },
    
    newServiceSuccessHandler: function(name, callback, serviceResponses) {
        var me = this;
        return me.newServiceHandler(name, callback, serviceResponses, function(name, serviceResponses, response) {
            serviceResponses.successes[name] = response;
        });
    },

    newServiceFailureHandler: function(name, callback, serviceResponses) {
        var me = this;
        return me.newServiceHandler(name, callback, serviceResponses, function(name, serviceResponses, response) {
            serviceResponses.failures[name] = response;
        });
    },

    newServiceHandler: function(name, callback, serviceResponses, serviceResponsesCallback) {
        return function(r, scope) {
            var me = scope;
            serviceResponses.responseCnt++;
            if ( serviceResponsesCallback ) {
                serviceResponsesCallback.apply(me, [name, serviceResponses, r]);
            }
            if ( callback ) {
                callback.apply(me, [ serviceResponses ]);
            }
            me.afterServiceHandler(serviceResponses);
        };
    },

	afterServiceHandler: function(serviceResponses){
		
	}
});