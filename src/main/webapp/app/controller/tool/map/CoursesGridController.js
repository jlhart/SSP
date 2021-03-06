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
Ext.define('Ssp.controller.tool.map.CoursesGridController', {
    extend: 'Deft.mvc.ViewController',
    mixins: [ 'Deft.mixin.Injectable' ],
    inject: {
    	apiProperties: 'apiProperties',
        store: 'coursesStore',
		electiveStore : 'electivesStore',
        formUtils: 'formRendererUtils',
		appEventsController: 'appEventsController'
        
    },
	init: function() {
		var me=this;
		me.getView().setLoading(false,false);
		me.formUtils.reconfigureGridPanel( me.getView(), me.store);
		me.store.load();
		me.store.appEventsController = me.appEventsController;
		me.electiveStore.load();
		me.store.addListener('load', this.sortAfterLoad, me.store,{single:true});
	   	me.appEventsController.getApplication().addListener("onRequisiteLoad", me.showCourseDetails, me);
		return me.callParent(arguments);
    },
	
	sortAfterLoad: function(){
		var me = this;
		me.sort('formattedCourse','ASC');
		me.appEventsController.getApplication().fireEvent("onAfterCourseLoad");
	},
    
    control:{
    	view:{
    		    itemdblclick: 'onItemDblClick'
    		}
    },
    
    onItemDblClick: function(grid, record, item, index, e, eOpts) {
		var me = this;
   		me.courseDetailsPopUp = Ext.create('Ssp.view.tools.map.CourseDetails');
    	me.courseDetailsPopUp.record = record;
		me.courseDetailsPopUp.query("#formatted_course_title")[0].setValue( record.get("formattedCourse") + " : " + record.get("title"));
		me.courseDetailsPopUp.query("#description")[0].setValue(record.get("description"));
		me.courseDetailsPopUp.query("#minCreditHours")[0].setValue(record.get("minCreditHours"));
		me.courseDetailsPopUp.query("#maxCreditHours")[0].setValue(record.get("maxCreditHours"));
		me.courseDetailsPopUp.query("#departmentCode")[0].setValue(record.get("departmentCode"));
		me.courseDetailsPopUp.query("#divisionCode")[0].setValue(record.get("divisionCode"));
		me.courseDetailsPopUp.query("#tags")[0].setValue(record.get("tags"));
		
		
		var masterSylComponent = me.courseDetailsPopUp.query("#mastersyllabus")[0];
		if(!record.get("masterSyllabusLink") || record.get("masterSyllabusLink") =='')
		{
			masterSylComponent.hidden = true;
		}
		var academicLinkComponent = me.courseDetailsPopUp.query("#academiclink")[0];
		if(!record.get("academicLink") || record.get("academicLink") =='')
		{
			academicLinkComponent.hidden = true;
		}
		masterSylComponent.setFieldLabel("<a href=\""+record.get("masterSyllabusLink")+"\" target=\"_blank\">Master Syllabus</a>");
		academicLinkComponent.setFieldLabel("<a href=\""+record.get("academicLink")+"\" target=\"_blank\">Academic Link</a>");
		
		me.courseRequisitesStore = Ext.create('Ssp.store.external.CourseRequisites');
		me.courseRequisitesStore.load(record.get('code'));
	    
		
    },
    showCourseDetails:function(scope){
        var me=this;
        var reqs= '';
        me.courseRequisitesStore.each(function(req) {
    	{
    		reqFormattedCourse = req.get('requiredFormattedCourse') == me.courseDetailsPopUp.record.get("formattedCourse") 
    		? req.get('requiringCourseCode') : req.get('requiredFormattedCourse');
    		reqs = reqs + reqFormattedCourse+': '+req.get('requisiteCode')+','
    	}
    		});
            reqs = reqs.substring(0, reqs.length - 1);
        	me.courseDetailsPopUp.query("#prereqs")[0].setValue(reqs);
        	me.courseDetailsPopUp.show();
        },
    destroy:function(){
	    var me=this;
		me.appEventsController.getApplication().removeListener("onRequisiteLoad", me.showCourseDetails, me);
		if(me.courseDetailsPopUp != null && !me.courseDetailsPopUp.isDestroyed)
			me.courseDetailsPopUp.close();
	    return me.callParent( arguments );

    }
    
    
});